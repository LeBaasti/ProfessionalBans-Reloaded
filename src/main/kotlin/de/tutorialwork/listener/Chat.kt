package de.tutorialwork.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChatEvent
import de.tutorialwork.configs.config
import de.tutorialwork.global.*
import de.tutorialwork.sql.Chat
import de.tutorialwork.utils.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

object Chat {

    @Subscribe
    fun onPlayerChatEvent(event: PlayerChatEvent) {
        if (event.message.startsWith("/")) return
        val player = event.player ?: return
        if (player in activeChats) {
            event.result = PlayerChatEvent.ChatResult.denied()
            val target = activeChats[player] ?: return
            val prefix = "§9§lSUPPORT §8• §c"
            target.msg(
                    "$prefix§c${player.username} §8» ${event.message}",
                    "$prefix§aDu §8» ${event.message}"
            )
            activeChats.keys.forEach { it.msg("$prefix§c${player.username} §8» ${event.message}") }
        }
        val uuid = player.uniqueId
        if (uuid.isMuted) {
            if (System.currentTimeMillis() >= uuid.rawEnd) {
                uuid.unMute()
                return
            }
            event.result = PlayerChatEvent.ChatResult.denied()
            player.sendMute()
        } else {
            insertMessage(uuid, event.message, player.currentServer.get().serverInfo.name)
            if (player.hasPermission("${permissionPrefix}blacklist.bypass")) return
            val predicate: (String) -> Boolean = { it.toUpperCase() in event.message.toUpperCase() }
            when {
                config.autoMuteEnabled -> {
                    val a = when {
                        blacklist.any(predicate) -> config.autoMuteID
                        adBlackList.any(predicate) && event.message.toUpperCase() !in adWhiteList -> config.autoMuteAdID
                        else -> return
                    }
                    uuid.createLogEntry(consoleName, ActionType.Blacklist("AUTOMUTE_${a}BLACKLIST"))
                    ActionType.Mute(a).run {
                        execute(uuid, consoleName)
                        sendNotify(uuid.name, consoleName)
                    }
                }
                config.autoMuteAutoReport -> {
                    val reason: String = when {
                        blacklist.any(predicate) -> {
                            player.msg("${prefix}§cAchte auf deine Wortwahl")
                            "VERHALTEN"
                        }
                        adBlackList.any(predicate) && event.message.toUpperCase() !in adWhiteList -> {
                            player.msg("${prefix}§cDu darfst keine Werbung machen")
                            "WERBUNG"
                        }
                        else -> return
                    }
                    val logId = createChatLog(uuid, consoleName)
                    uuid.createReport(consoleName, reason, logId)
                    ActionType.Report(reason).sendNotify(player.username, consoleName)
                }
                else -> return
            }
            event.result = PlayerChatEvent.ChatResult.denied()
        }
    }


    private fun insertMessage(uuid: UUID, message: String, server: String) {
        Chat.insert {
            it[this.uuid] = uuid.toString()
            it[this.server] = server
            it[this.message] = message
            it[this.sendDate] = System.currentTimeMillis()
        }
    }

    fun createChatLog(uuid: UUID, createdUUID: String): String {
        val query = Chat.getByUUID(uuid)
        val id = 20.randomString()
        val now = System.currentTimeMillis()
        val tenMinutes = 10 * 60 * 1000
        query.forEach {
            val tenAgo = System.currentTimeMillis() - tenMinutes
            val sendDate = it[Chat.sendDate]
            if (sendDate > tenAgo) {
                mysql.update("""INSERT INTO chatlog(LOGID, uuid, CREATOR_UUID, SERVER, MESSAGE, SENDDATE, CREATED_AT) VALUES ('$id' 
                            |,'$uuid', '$createdUUID', '${it[Chat.server]}', '${it[Chat.message]}'
                            |, '${sendDate}', '$now')""".trimMargin())
            }
        }
        return id
    }

    fun hasMessages(uuid: UUID): Boolean = Chat.getByUUID(uuid).any()

    private fun Chat.getByUUID(uuid: UUID) = select { Chat.uuid.eq(uuid.toString()) }

}