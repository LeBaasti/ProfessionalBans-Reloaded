package de.tutorialwork.listener

import de.tutorialwork.commands.SupportChat
import de.tutorialwork.main.Main
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.LogManager
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import net.md_5.bungee.event.EventHandler
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import java.sql.SQLException
import java.util.*

class Chat : Listener {

    @EventHandler
    fun onChat(e: ChatEvent) {
        val p = e.sender as ProxiedPlayer
        if (!e.message.startsWith("/")) {
            if (SupportChat.activechats.containsKey(p)) {
                e.isCancelled = true
                val target = SupportChat.activechats[p] ?: return
                target.sendMessage("§9§lSUPPORT §8• §c" + p.name + " §8» " + e.message)
                p.sendMessage("§9§lSUPPORT §8• §aDu §8» " + e.message)
            }
            if (SupportChat.activechats.containsValue(p)) {
                e.isCancelled = true
                for (key in SupportChat.activechats.keys) {
                    //Key has started the support chat
                    key.sendMessage("§9§lSUPPORT §8• §c" + p.name + " §8» " + e.message)
                }
                p.sendMessage("§9§lSUPPORT §8• §aDu §8» " + e.message)
            }
            val uuid = p.uniqueId
            if (BanManager.isMuted(uuid)) {
                val config = File(Main.instance.dataFolder, "config.yml")
                try {
                    val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)

                    if (BanManager.getRAWEnd(uuid) == -1L) {
                        e.isCancelled = true
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.MUTE").replace("%grund%", BanManager.getReasonString(uuid))))
                    } else {
                        if (System.currentTimeMillis() < BanManager.getRAWEnd(uuid) ?: 0) {
                            e.isCancelled = true
                            var MSG = configcfg.getString("LAYOUT.TEMPMUTE")
                            MSG = MSG.replace("%grund%", BanManager.getReasonString(uuid))
                            MSG = MSG.replace("%dauer%", BanManager.getEnd(uuid))
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG))
                        } else {
                            BanManager.unmute(uuid)
                        }
                    }

                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                } catch (e2: IOException) {
                    e2.printStackTrace()
                }

            } else {
                val config = File(Main.instance.dataFolder, "config.yml")
                try {
                    val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)

                    if (!p.hasPermission("professionalbans.blacklist.bypass") || !p.hasPermission("professionalbans.*")) {
                        if (configcfg.getBoolean("AUTOMUTE.ENABLED")) {
                            insertMessage(uuid, e.message, p.server.info.name)
                            for (blacklist in Main.blacklist) {
                                if (e.message.toUpperCase().contains(blacklist.toUpperCase())) {
                                    e.isCancelled = true
                                    BanManager.mute(uuid, configcfg.getInt("AUTOMUTE.MUTEID"), "KONSOLE")
                                    LogManager.createEntry(uuid.toString(), "KONSOLE", "AUTOMUTE_BLACKLIST", e.message)
                                    BanManager.setMutes(uuid, BanManager.getMutes(uuid) + 1)
                                    BanManager.sendNotify("MUTE", BanManager.getNameByUUID(uuid).toString()
                                            , "KONSOLE", BanManager.getReasonByID(configcfg.getInt("AUTOMUTE.MUTEID")))
                                    if (BanManager.getRAWEnd(uuid) == -1L) {
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.MUTE")
                                                .replace("%grund%", BanManager.getReasonByID(configcfg.getInt("AUTOMUTE.MUTEID")).toString())))
                                    } else {
                                        var MSG = configcfg.getString("LAYOUT.TEMPMUTE")
                                        MSG = MSG.replace("%grund%", BanManager.getReasonString(uuid))
                                        MSG = MSG.replace("%dauer%", BanManager.getEnd(uuid))
                                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG))
                                    }
                                    return
                                }
                            }
                            for (adblacklist in Main.adblacklist) {
                                if (e.message.toUpperCase().contains(adblacklist.toUpperCase())) {
                                    if (!Main.adwhitelist.contains(e.message.toUpperCase())) {
                                        e.isCancelled = true
                                        BanManager.mute(uuid, configcfg.getInt("AUTOMUTE.ADMUTEID"), "KONSOLE")
                                        LogManager.createEntry(uuid.toString(), "KONSOLE", "AUTOMUTE_ADBLACKLIST", e.message)
                                        BanManager.setMutes(uuid, BanManager.getMutes(uuid) + 1)
                                        BanManager.sendNotify("MUTE", BanManager.getNameByUUID(uuid).toString(), "KONSOLE", BanManager.getReasonByID(configcfg.getInt("AUTOMUTE.ADMUTEID")))
                                        if (BanManager.getRAWEnd(uuid) == -1L) {
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.MUTE")
                                                    .replace("%grund%", BanManager.getReasonByID(configcfg.getInt("AUTOMUTE.MUTEID")))))
                                        } else {
                                            var msg = configcfg.getString("LAYOUT.TEMPMUTE")
                                            msg = msg.replace("%grund%", BanManager.getReasonString(uuid))
                                            msg = msg.replace("%dauer%", BanManager.getEnd(uuid))
                                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg))
                                        }
                                        return
                                    }
                                }
                            }
                        } else {
                            insertMessage(uuid, e.message, p.server.info.name)
                            if (configcfg.getBoolean("AUTOMUTE.AUTOREPORT")) {
                                for (blacklist in Main.blacklist) {
                                    if (e.message.toUpperCase().contains(blacklist.toUpperCase())) {
                                        e.isCancelled = true
                                        p.sendMessage(Main.prefix + "§cAchte auf deine Wortwahl")
                                        val LogID = Chat.createChatlog(uuid, "KONSOLE")
                                        BanManager.createReport(uuid, "KONSOLE", "VERHALTEN", LogID)
                                        BanManager.sendNotify("REPORT", p.name, "KONSOLE", "VERHALTEN")
                                        return
                                    }
                                }
                                for (adblacklist in Main.adblacklist) {
                                    if (e.message.toUpperCase().contains(adblacklist.toUpperCase())) {
                                        if (!Main.adwhitelist.contains(e.message.toUpperCase())) {
                                            e.isCancelled = true
                                            p.sendMessage(Main.prefix + "§cDu darfst keine Werbung machen")
                                            val LogID = Chat.createChatlog(uuid, "KONSOLE")
                                            BanManager.createReport(uuid, "KONSOLE", "WERBUNG", LogID)
                                            BanManager.sendNotify("REPORT", p.name, "KONSOLE", "WERBUNG")
                                            return
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        insertMessage(uuid, e.message, p.server.info.name)
                    }

                } catch (e2: IOException) {
                    e2.printStackTrace()
                }

            }
        }
    }

    companion object {

        fun insertMessage(uuid: UUID, Message: String, Server: String) {
            Main.mysql.update("INSERT INTO chat(UUID, SERVER, MESSAGE, SENDDATE) " +
                    "VALUES ('" + uuid + "', '" + Server + "', '" + Message + "', '" + System.currentTimeMillis() + "')")
        }

        fun createChatlog(uuid: UUID, createdUUID: String): String {
            try {
                val rs = Main.mysql.query("SELECT * FROM chat WHERE uuid='$uuid'") ?: return "Null"
                val id = randomString(20)
                val now = System.currentTimeMillis()
                while (rs.next()) {
                    val tenMinutes = 10 * 60 * 1000
                    val tenAgo = System.currentTimeMillis() - tenMinutes
                    if (java.lang.Long.valueOf(rs.getString("SENDDATE")) > tenAgo) {
                        Main.mysql.update("INSERT INTO chatlog(LOGID, uuid, CREATOR_UUID, SERVER, MESSAGE, SENDDATE, CREATED_AT) " +
                                "VALUES ('" + id + "' ,'" + uuid + "', '" + createdUUID + "', '" + rs.getString("SERVER") + "', '" + rs.getString("MESSAGE") + "', '" + rs.getString("SENDDATE") + "', '" + now + "')")
                    }
                }
                return id
            } catch (exc: SQLException) {

            }

            return "Null"
        }

        fun hasMessages(uuid: UUID): Boolean {
            return try {
                val rs = Main.mysql.query("SELECT * FROM chat WHERE uuid='$uuid'") ?: return false
                var i = 0
                while (rs.next()) {
                    i++
                }
                i != 0
            } catch (exc: SQLException) {
                false
            }
        }

        private const val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        private var rnd = SecureRandom()

        private fun randomString(len: Int): String {
            val sb = StringBuilder(len)
            for (i in 0 until len)
                sb.append(alphabet[rnd.nextInt(alphabet.length)])
            return sb.toString()
        }
    }

}