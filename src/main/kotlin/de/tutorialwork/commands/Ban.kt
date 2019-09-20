package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.main.Main
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.LogManager
import de.tutorialwork.utils.UUIDFetcher
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.IOException

class Ban(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.ban")) {
                if (args.size == 1) {
                    val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                    val id = args[1].toIntOrNull() ?: return //add fail message
                    if (BanManager.playerExists(uuid)) {
                        if (BanManager.isWebaccountAdmin(uuid)) {
                            sender.sendMessage(Main.prefix + "§cDiesen Spieler kannst du nicht bannen/muten")
                            return
                        }
                        BanManager.setReasonBans(id, BanManager.getReasonBans(id) ?: 0 + 1)
                        if (BanManager.isBanReason(id)) {
                            if (BanManager.hasExtraPerms(id)) {
                                if (!sender.hasPermission(BanManager.getExtraPerms(id))) {
                                    sender.sendMessage(Main.prefix + "§cDu hast keine Berechtigung diesen Bangrund zu nutzen")
                                    return
                                }
                            }
                            BanManager.ban(uuid, id, sender.uniqueId.toString(), Main.increaseValue, Main.increaseBans)
                            LogManager.createEntry("", sender.uniqueId.toString(), "BAN", id.toString())
                            BanManager.setBans(uuid, BanManager.getBans(uuid) + 1)
                            BanManager.sendNotify("BAN", BanManager.getNameByUUID(uuid).toString(), sender.name, BanManager.getReasonByID(id))
                            val banned = Main.instance.proxy.getPlayer(args[0])
                            val config = File(Main.instance.dataFolder, "config.yml")
                            try {
                                val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                                if (BanManager.getRAWEnd(banned.uniqueId) == -1L) {
                                    banned.disconnect(ChatColor.translateAlternateColorCodes('&',
                                            configcfg.getString("LAYOUT.BAN").replace("%grund%", BanManager.getReasonByID(id).toString())))
                                } else {
                                    var msg = configcfg.getString("LAYOUT.TEMPBAN")
                                    msg = msg.replace("%grund%", BanManager.getReasonString(uuid))
                                    msg = msg.replace("%dauer%", BanManager.getEnd(uuid))
                                    banned.disconnect(ChatColor.translateAlternateColorCodes('&', msg))
                                }
                                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        } else {
                            if (BanManager.hasExtraPerms(id)) {
                                if (!sender.hasPermission(BanManager.getExtraPerms(id))) {
                                    sender.sendMessage(Main.prefix + "§cDu hast keine Berechtigung diesen Mutegrund zu nutzen")
                                    return
                                }
                            }
                            BanManager.mute(uuid, id, sender.uniqueId.toString())
                            LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "MUTE", id.toString())
                            BanManager.setMutes(uuid, BanManager.getMutes(uuid) ?: 0 + 1)
                            BanManager.sendNotify("MUTE", BanManager.getNameByUUID(uuid).toString(), sender.name, BanManager.getReasonByID(id).toString())
                            val banned = ProxyServer.getInstance().getPlayer(args[0])
                            if (banned != null) {
                                val config = File(Main.instance.dataFolder, "config.yml")
                                try {
                                    val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                                    if (BanManager.getRAWEnd(banned.uniqueId) == -1L) {
                                        banned.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                configcfg.getString("LAYOUT.MUTE").replace("%grund%", BanManager.getReasonByID(id).toString())))
                                    } else {
                                        var MSG = configcfg.getString("LAYOUT.TEMPMUTE")
                                        MSG = MSG.replace("%grund%", BanManager.getReasonString(uuid))
                                        MSG = MSG.replace("%dauer%", BanManager.getEnd(uuid))
                                        banned.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG))
                                    }
                                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                            }
                        }
                    } else {
                        sender.sendMessage(Main.prefix + "§cDieser Spieler hat den Server noch nie betreten")
                    }
                } else {
                    BanManager.getBanReasonsList(sender)
                    sender.sendMessage(Main.prefix + "/ban <Spieler> <Grund-ID>")
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        } else {
            if (args.isEmpty() || args.size == 1) {
                for (zaehler in 1 until (BanManager.countReasons() ?: 0) + 1) {
                    if (BanManager.isBanReason(zaehler)) {
                        console.sendMessage("§7" + zaehler + " §8| §e" + BanManager.getReasonByID(zaehler))
                    } else {
                        console.sendMessage("§7" + zaehler + " §8| §e" + BanManager.getReasonByID(zaehler) + " §8(§cMUTE§8)")
                    }
                }
                console.sendMessage(Main.prefix + "/ban <Spieler> <Grund-ID>")
            } else {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                val id = Integer.valueOf(args[1])
                if (BanManager.playerExists(uuid)) {
                    if (BanManager.isWebaccountAdmin(uuid)) {
                        console.sendMessage(Main.prefix + "§cDiesen Spieler kannst du nicht bannen/muten")
                        return
                    }
                    BanManager.setReasonBans(id, BanManager.getReasonBans(id) ?: 0 + 1)
                    if (BanManager.isBanReason(id)) {
                        BanManager.ban(uuid, id, "KONSOLE", Main.increaseValue, Main.increaseBans)
                        LogManager.createEntry(uuid.toString(), "KONSOLE", "BAN", id.toString())
                        BanManager.setBans(uuid, BanManager.getBans(uuid) ?: 0 + 1)
                        BanManager.sendNotify("BAN", BanManager.getNameByUUID(uuid).toString(), "KONSOLE", BanManager.getReasonByID(id).toString())
                        val banned = ProxyServer.getInstance().getPlayer(args[0])
                        if (banned != null) {
                            val config = File(Main.instance.dataFolder, "config.yml")
                            try {
                                val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                                if (BanManager.getRAWEnd(banned.uniqueId) == -1L) {
                                    banned.disconnect(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.BAN")
                                            .replace("%grund%", BanManager.getReasonByID(id).toString())))
                                } else {
                                    var MSG = configcfg.getString("LAYOUT.TEMPBAN")
                                    MSG = MSG.replace("%grund%", BanManager.getReasonString(uuid).toString())
                                    MSG = MSG.replace("%dauer%", BanManager.getEnd(uuid))
                                    banned.disconnect(ChatColor.translateAlternateColorCodes('&', MSG))
                                }
                                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }
                    } else {
                        BanManager.mute(uuid, id, "KONSOLE")
                        LogManager.createEntry(uuid.toString(), "KONSOLE", "MUTE", id.toString())
                        BanManager.setMutes(uuid, BanManager.getMutes(uuid) ?: 0 + 1)
                        BanManager.sendNotify("MUTE", BanManager.getNameByUUID(uuid).toString(), "KONSOLE", BanManager.getReasonByID(id).toString())
                        val banned = ProxyServer.getInstance().getPlayer(args[0])
                        if (banned != null) {
                            val config = File(Main.instance.dataFolder, "config.yml")
                            try {
                                val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                                if (BanManager.getRAWEnd(banned.uniqueId) == -1L) {
                                    banned.sendMessage(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.MUTE")
                                            .replace("%grund%", BanManager.getReasonByID(id).toString())))
                                } else {
                                    var msg = configcfg.getString("LAYOUT.TEMPMUTE")
                                    msg = msg.replace("%grund%", BanManager.getReasonString(uuid).toString())
                                    msg = msg.replace("%dauer%", BanManager.getEnd(uuid))
                                    banned.sendMessage(ChatColor.translateAlternateColorCodes('&', msg))
                                }
                                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }
                    }
                } else {
                    console.sendMessage(Main.prefix + "§cDieser Spieler hat den Server noch nie betreten")
                }
            }
        }
    }
}