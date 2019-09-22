package de.tutorialwork.listener

import de.tutorialwork.commands.SupportChat
import de.tutorialwork.consoleName
import de.tutorialwork.main.Main
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import net.md_5.bungee.event.EventHandler

import java.io.File
import java.io.IOException

object Login : Listener {

    @EventHandler
    fun onPreLoginEvent(event: PreLoginEvent) {
        val uuid = UUIDFetcher.getUUID(event.connection.name) ?: return
        val ip = event.connection.virtualHost.hostName
        /*val ip = event.connection.address.hostString*/
        BanManager.createPlayer(uuid, event.connection.name)
        IPManager.insertIP(ip, uuid)
        val config = File(Main.instance.dataFolder, "configFile.yml")
        try {
            val cfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
            if (cfg.getBoolean("VPN.BLOCKED")) {
                if (!Main.ipwhitelist.contains(ip)) {
                    if (IPManager.isVPN(ip)) {
                        if (cfg.getBoolean("VPN.KICK")) {
                            event.isCancelled = true
                            event.cancelReason = ChatColor.translateAlternateColorCodes('&', cfg.getString("VPN.KICKMSG"))
                        }
                        if (cfg.getBoolean("VPN.BAN")) {
                            val id = cfg.getInt("VPN.BANID")
                            BanManager.ban(uuid, id, consoleName, Main.increaseValue, Main.increaseBans)
                            ActionType.IpBan(id).sendNotify(event.connection.address.hostString, consoleName)
                            event.isCancelled = true
                            if (BanManager.getRAWEnd(uuid) == -1L) {
                                event.cancelReason = ChatColor.translateAlternateColorCodes('&', cfg.getString("LAYOUT.IPBAN").replace("%grund%", id.reason!!))
                            } else {
                                var MSG = cfg.getString("LAYOUT.TEMPIPBAN")
                                MSG = MSG.replace("%grund%", uuid.reasonString!!)
                                MSG = MSG.replace("%dauer%", BanManager.getEnd(uuid))
                                event.cancelReason = ChatColor.translateAlternateColorCodes('&', MSG)
                            }
                        }
                    }
                }
            }
        } catch (er: IOException) {
            er.printStackTrace()
        }

        if (IPManager.isBanned(ip)) {
            try {
                val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)

                if (IPManager.getRAWEnd(ip) == -1L) {
                    event.isCancelled = true
                    event.cancelReason = ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.IPBAN").replace("%grund%", IPManager.getReasonString(ip)!!))
                } else {
                    if (System.currentTimeMillis() < IPManager.getRAWEnd(ip) ?: 0) {
                        event.isCancelled = true
                        var msg = configcfg.getString("LAYOUT.TEMPIPBAN")
                        msg = msg.replace("%grund%", IPManager.getReasonString(ip)!!)
                        msg = msg.replace("%dauer%", IPManager.getEnd(ip))
                        event.cancelReason = ChatColor.translateAlternateColorCodes('&', msg)
                    } else {
                        IPManager.unban(ip)
                    }
                }

                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
            } catch (e2: IOException) {
                e2.printStackTrace()
            }

        }
        if (BanManager.isBanned(uuid)) {
            try {
                val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)

                if (BanManager.getRAWEnd(uuid) == -1L) {
                    event.isCancelled = true
                    event.cancelReason = ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.BAN").replace("%grund%", uuid.reasonString!!))
                } else {
                    if (System.currentTimeMillis() < BanManager.getRAWEnd(uuid) ?: 0) {
                        event.isCancelled = true
                        var msg = configcfg.getString("LAYOUT.TEMPBAN")
                        msg = msg.replace("%grund%", uuid.reasonString!!)
                        msg = msg.replace("%dauer%", BanManager.getEnd(uuid))
                        event.cancelReason = ChatColor.translateAlternateColorCodes('&', msg)
                    } else {
                        BanManager.unban(uuid)
                    }
                }

                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
            } catch (e2: IOException) {
                e2.printStackTrace()
            }

        }
    }

    @EventHandler
    fun onFinalLogin(e: PostLoginEvent) {
        val p = e.player
        if (p.hasPermission("professionalbans.reports") || p.hasPermission("professionalbans.*")) {
            if (BanManager.countOpenReports() != 0) {
                p.sendMessage(prefix + "Derzeit sind noch Â§eÂ§l" + BanManager.countOpenReports() + " Reports Â§7offen")
            }
        }
        if (p.hasPermission("professionalbans.supportchat") || p.hasPermission("professionalbans.*")) {
            if (SupportChat.openchats.size != 0) {
                p.sendMessage(prefix + "Derzeit sind noch Â§eÂ§l" + SupportChat.openchats.size + " Â§7Support Chat Anfragen Â§aoffen")
            }
        }
        //Update Check
        if (p.hasPermission("professionalbans.*")) {
            if (Main.callURL("https://api.spigotmc.org/legacy/update.php?resource=63657") != Main.Version) {
                p.sendMessage("Â§8[]===================================[]")
                p.sendMessage("Â§eÂ§lProfessionalBans Â§7Reloaded Â§8| Â§7Version Â§c" + Main.Version)
                p.sendMessage("Â§cDu benutzt eine Â§cÂ§lVERALTETE Â§cVersion des Plugins!")
                p.sendMessage("Â§7Update: Â§4Â§lhttps://spigotmc.org/resources/63657")
                p.sendMessage("Â§8[]===================================[]")
            }
        }
    }

}