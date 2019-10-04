package de.tutorialwork.listener

import de.tutorialwork.global.*
import de.tutorialwork.sql.Chat
import de.tutorialwork.utils.*
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

object Chat : Listener {

    @EventHandler
    fun onChat(event: ChatEvent) {
        val player = event.sender as ProxiedPlayer
        if (event.message.startsWith("/")) return
        if (player in activechats) {
            event.isCancelled = true
            val target = activechats[player] ?: return
            val prefix = "§9§lSUPPORT §8• §c"
            target.msg(
                    "$prefix§c${player.name} §8» ${event.message}",
                    "$prefix§aDu §8» ${event.message}"
            )
            activechats.keys.forEach { it.msg("$prefix§c${player.name} §8» ${event.message}") }
        }
        val uuid = player.uniqueId
        if (uuid.isMuted) {
            if (System.currentTimeMillis() >= uuid.rawEnd) {
                uuid.unMute()
                return
            }
            event.isCancelled = true
            player.sendMute()
        } else {
            insertMessage(uuid, event.message, player.server.info.name)
            if (player.hasPermission("${permissionPrefix}blacklist.bypass")) return
            val predicate: (String) -> Boolean = { it.toUpperCase() in event.message.toUpperCase() }
            when {
                config.getBoolean("AUTOMUTE.ENABLED") -> {
                    val a = if (blacklist.any(predicate)) "" else {
                        if (adblacklist.any(predicate) && event.message.toUpperCase() !in adwhitelist) "AD" else return
                    }
                    uuid.mute(config.getInt("AUTOMUTE.${a}MUTEID").reason, consoleName)
                    LogManager.createEntry(uuid.toString(), consoleName, ActionType.Blacklist("AUTOMUTE_${a}BLACKLIST"))
                    ActionType.Mute(config.getInt("AUTOMUTE.MUTEID")).sendNotify(uuid.name, consoleName)
                    player.sendMute()
                }
                config.getBoolean("AUTOMUTE.AUTOREPORT") -> {
                    val reason: String = if (blacklist.any(predicate)) {
                        player.msg("${prefix}§cAchte auf deine Wortwahl")
                        "VERHALTEN"
                    } else {
                        if (adblacklist.any(predicate) && event.message.toUpperCase() !in adwhitelist) {
                            player.msg("${prefix}§cDu darfst keine Werbung machen")
                            "WERBUNG"
                        } else return
                    }
                    val logId = createChatLog(uuid, consoleName)
                    uuid.createReport(consoleName, reason, logId)
                    ActionType.Report(reason).sendNotify(player.name, consoleName)
                }
                else -> return
            }
            event.isCancelled = true
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