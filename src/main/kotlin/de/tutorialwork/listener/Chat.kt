package de.tutorialwork.listener

import de.tutorialwork.*
import de.tutorialwork.utils.*
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.*

object Chat : Listener {

    @EventHandler
    fun onChat(e: ChatEvent) {
        val p = e.sender as ProxiedPlayer
        if (!e.message.startsWith("/")) {
            if (activechats.containsKey(p)) {
                e.isCancelled = true
                val target = activechats[p] ?: return
                target.msg("§9§lSUPPORT §8• §c" + p.name + " §8» " + e.message)
                p.msg("§9§lSUPPORT §8• §aDu §8» " + e.message)
            }
            if (activechats.containsValue(p)) {
                e.isCancelled = true
                for (key in activechats.keys) {
                    //Key has started the support chat
                    key.msg("§9§lSUPPORT §8• §c" + p.name + " §8» " + e.message)
                }
                p.msg("§9§lSUPPORT §8• §aDu §8» " + e.message)
            }
            val uuid = p.uniqueId
            if (uuid.isMuted) {
                if (uuid.rawEnd == -1L) {
                    e.isCancelled = true
                    p.msg(config.getString("LAYOUT.MUTE").replace("%grund%", uuid.reasonString).translateColors())
                } else {
                    if (System.currentTimeMillis() < uuid.rawEnd) {
                        e.isCancelled = true
                        p.sendTempmute()
                    } else {
                        uuid.unMute()
                    }
                }

            } else {
                if (!p.hasPermission("professionalbans.blacklistFile.bypass")) {
                    if (config.getBoolean("AUTOMUTE.ENABLED")) {
                        insertMessage(uuid, e.message, p.server.info.name)
                        for (blacklist in blacklist) {
                            if (e.message.toUpperCase().contains(blacklist.toUpperCase())) {
                                e.isCancelled = true
                                uuid.mute(config.getInt("AUTOMUTE.MUTEID"), consoleName)
                                LogManager.createEntry(uuid.toString(), consoleName, ActionType.Blacklist("AUTOMUTE_BLACKLIST"))
                                ActionType.Mute(config.getInt("AUTOMUTE.MUTEID")).sendNotify(uuid.name, consoleName)
                                if (uuid.rawEnd == -1L) {
                                    p.msg(config.getString("LAYOUT.MUTE")
                                            .replace("%grund%", config.getInt("AUTOMUTE.MUTEID").reason).translateColors())
                                } else p.sendTempmute()
                                return
                            }
                        }
                        for (adblacklist in adblacklist) {
                            if (e.message.toUpperCase().contains(adblacklist.toUpperCase())) {
                                if (!adwhitelist.contains(e.message.toUpperCase())) {
                                    e.isCancelled = true
                                    uuid.mute(config.getInt("AUTOMUTE.ADMUTEID"), consoleName)
                                    LogManager.createEntry(uuid.toString(), consoleName, ActionType.Blacklist("AUTOMUTE_ADBLACKLIST"))
                                    uuid.mutes += 1
                                    ActionType.Mute(config.getInt("AUTOMUTE.ADMUTEID")).sendNotify(uuid.name, consoleName)
                                    if (uuid.rawEnd == -1L) {
                                        p.msg(config.getString("LAYOUT.MUTE")
                                                .replace("%grund%", config.getInt("AUTOMUTE.MUTEID").reason).translateColors())
                                    } else p.sendTempmute()
                                    return
                                }
                            }
                        }
                    } else {
                        insertMessage(uuid, e.message, p.server.info.name)
                        if (config.getBoolean("AUTOMUTE.AUTOREPORT")) {
                            for (blacklist in blacklist) {
                                if (e.message.toUpperCase().contains(blacklist.toUpperCase())) {
                                    e.isCancelled = true
                                    p.msg("$prefix§cAchte auf deine Wortwahl")
                                    val logId = createChatlog(uuid, consoleName)
                                    uuid.createReport(consoleName, "VERHALTEN", logId)
                                    ActionType.Report("VERHALTEN").sendNotify(p.name, consoleName)
                                    return
                                }
                            }
                            for (adblacklist in adblacklist) {
                                if (e.message.toUpperCase().contains(adblacklist.toUpperCase())) {
                                    if (!adwhitelist.contains(e.message.toUpperCase())) {
                                        e.isCancelled = true
                                        p.msg("$prefix§cDu darfst keine Werbung machen")
                                        val logId = createChatlog(uuid, consoleName)
                                        uuid.createReport(consoleName, "WERBUNG", logId)
                                        ActionType.Report("WERBUNG").sendNotify(p.name, consoleName)
                                        return
                                    }
                                }
                            }
                        }
                    }
                } else {
                    insertMessage(uuid, e.message, p.server.info.name)
                }


            }
        }
    }


    private fun insertMessage(uuid: UUID, Message: String, Server: String) {
        mysql.update("INSERT INTO chat(UUID, SERVER, MESSAGE, SENDDATE) " +
                "VALUES ('" + uuid + "', '" + Server + "', '" + Message + "', '" + System.currentTimeMillis() + "')")
    }

    fun createChatlog(uuid: UUID, createdUUID: String): String {
        val rs = mysql.query("SELECT * FROM chat WHERE uuid='$uuid'") ?: return "Null"
        val id = 20.randomString()
        val now = System.currentTimeMillis()
        while (rs.next()) {
            val tenMinutes = 10 * 60 * 1000
            val tenAgo = System.currentTimeMillis() - tenMinutes
            if (java.lang.Long.valueOf(rs.getString("SENDDATE")) > tenAgo) {
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
        while (rs.next()) {
            i++
        }
        return i != 0
    }


}