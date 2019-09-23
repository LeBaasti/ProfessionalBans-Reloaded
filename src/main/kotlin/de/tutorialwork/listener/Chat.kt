package de.tutorialwork.listener

import de.tutorialwork.global.*
import de.tutorialwork.utils.*
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.*

object Chat : Listener {

    @EventHandler
    fun onChat(event: ChatEvent) {
        val player = event.sender as ProxiedPlayer
        if (event.message.startsWith("/")) return
        if (player in activechats) {
            event.isCancelled = true
            val target = activechats[player] ?: return
            target.msg("§9§lSUPPORT §8• §c${player.name} §8» ${event.message}")
            player.msg("§9§lSUPPORT §8• §aDu §8» ${event.message}")
            for (key in activechats.keys) {
                //Key has started the support chat
                key.msg("§9§lSUPPORT §8• §c${player.name} §8» ${event.message}")
            }
        }
        val uuid = player.uniqueId
        if (uuid.isMuted) {
            if (uuid.rawEnd == -1L) {
                event.isCancelled = true
                player.msg(config.getString("LAYOUT.MUTE").replace("%grund%", uuid.reasonString).translateColors())
            } else {
                if (System.currentTimeMillis() >= uuid.rawEnd) uuid.unMute()
                else {
                    event.isCancelled = true
                    player.sendTempMute()
                }
            }

        } else {
            if (player.hasPermission("${permissionPrefix}blacklist.bypass")) insertMessage(uuid, event.message, player.server.info.name)
            else {
                if (config.getBoolean("AUTOMUTE.ENABLED")) {
                    insertMessage(uuid, event.message, player.server.info.name)
                    for (blacklist in blacklist) {
                        if (event.message.toUpperCase().contains(blacklist.toUpperCase())) {
                            event.isCancelled = true
                            uuid.mute(config.getInt("AUTOMUTE.MUTEID").reason, consoleName)
                            LogManager.createEntry(uuid.toString(), consoleName, ActionType.Blacklist("AUTOMUTE_BLACKLIST"))
                            ActionType.Mute(config.getInt("AUTOMUTE.MUTEID")).sendNotify(uuid.name, consoleName)
                            player.sendMute()
                            return
                        }
                    }
                    for (adblacklist in adblacklist) {
                        if (event.message.toUpperCase().contains(adblacklist.toUpperCase())) {
                            if (!adwhitelist.contains(event.message.toUpperCase())) {
                                event.isCancelled = true
                                uuid.mute(config.getInt("AUTOMUTE.ADMUTEID").reason, consoleName)
                                LogManager.createEntry(uuid.toString(), consoleName, ActionType.Blacklist("AUTOMUTE_ADBLACKLIST"))
                                ActionType.Mute(config.getInt("AUTOMUTE.ADMUTEID")).sendNotify(uuid.name, consoleName)
                                player.sendMute()
                                return
                            }
                        }
                    }
                } else {
                    insertMessage(uuid, event.message, player.server.info.name)
                    if (config.getBoolean("AUTOMUTE.AUTOREPORT")) {
                        for (blacklist in blacklist) {
                            if (event.message.toUpperCase().contains(blacklist.toUpperCase())) {
                                event.isCancelled = true
                                player.msg("${prefix}§cAchte auf deine Wortwahl")
                                val logId = createChatlog(uuid, consoleName)
                                uuid.createReport(consoleName, "VERHALTEN", logId)
                                ActionType.Report("VERHALTEN").sendNotify(player.name, consoleName)
                                return
                            }
                        }
                        for (adblacklist in adblacklist) {
                            if (event.message.toUpperCase().contains(adblacklist.toUpperCase())) {
                                if (!adwhitelist.contains(event.message.toUpperCase())) {
                                    event.isCancelled = true
                                    player.msg("${prefix}§cDu darfst keine Werbung machen")
                                    val logId = createChatlog(uuid, consoleName)
                                    uuid.createReport(consoleName, "WERBUNG", logId)
                                    ActionType.Report("WERBUNG").sendNotify(player.name, consoleName)
                                    return
                                }
                            }
                        }
                    }
                }
            }


        }
    }


    private fun insertMessage(uuid: UUID, Message: String, Server: String) {
        mysql.update("INSERT INTO chat(UUID, SERVER, MESSAGE, SENDDATE) " +
                "VALUES ('" + uuid + "', '" + Server + "', '" + Message + "', '" + System.currentTimeMillis() + "')")
    }

    fun createChatlog(uuid: UUID, createdUUID: String): String {
        val rs = mysql.query("SELECT * FROM chat WHERE uuid='$uuid'") ?: return "null"
        val id = 20.randomString()
        val now = System.currentTimeMillis()
        while (rs.next()) {
            val tenMinutes = 10 * 60 * 1000
            val tenAgo = System.currentTimeMillis() - tenMinutes
            if (rs.getString("SENDDATE").toLong() > tenAgo) {
                mysql.update("""INSERT INTO chatlog(LOGID, uuid, CREATOR_UUID, SERVER, MESSAGE, SENDDATE, CREATED_AT) VALUES ('$id' 
                            |,'$uuid', '$createdUUID', '${rs.getString("SERVER")}', '${rs.getString("MESSAGE")}'
                            |, '${rs.getString("SENDDATE")}', '$now')""".trimMargin())
            }
        }
        return id
    }

    fun hasMessages(uuid: UUID): Boolean {
        val rs = mysql.query("SELECT * FROM chat WHERE uuid='$uuid'") ?: return false
        var i = 0
        while (rs.next()) i++
        return i != 0
    }

}