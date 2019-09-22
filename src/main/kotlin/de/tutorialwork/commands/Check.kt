package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.main.Main
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Check(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.check") || sender.hasPermission("professionalbans.*")) {
                if (args.isEmpty()) {
                    sender.sendMessage(prefix + "/check <Spieler/IP>")
                } else {
                    val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                    if (IPBan.validate(args[0])) {
                        val ip = args[0]
                        if (IPManager.ipExists(ip)) {
                            sender.sendMessage("§8[]===================================[]")
                            if (IPManager.getPlayerFromIP(ip) != null) {
                                sender.sendMessage("§7Spieler: §e§l" + (IPManager.getPlayerFromIP(ip)
                                        ?: return).name)
                            } else {
                                sender.sendMessage("§7Spieler: §c§lKeiner")
                            }
                            if (IPManager.isBanned(ip)) {
                                sender.sendMessage("§7IP-Ban: §c§lJa §8/ " + IPManager.getReasonString(IPManager.getIPFromPlayer(uuid)))
                            } else {
                                sender.sendMessage("§7IP-Ban: §a§lNein")
                            }
                            sender.sendMessage("§7Bans: §e§l" + IPManager.getBans(ip))
                            sender.sendMessage("§7Zuletzt genutzt: §e§l" + BanManager.formatTimestamp(IPManager.getLastUseLong(ip)))
                            sender.sendMessage("§8[]===================================[]")
                        } else {
                            sender.sendMessage(prefix + "§cZu dieser IP-Adresse sind keine Informationen verfügbar")
                        }
                    } else {
                        if (BanManager.playerExists(uuid)) {
                            sender.sendMessage("§8[]===================================[]")
                            sender.sendMessage("§7Spieler: §e§l" + uuid.name)
                            if (BanManager.isBanned(uuid)) {
                                sender.sendMessage("§7Gebannt: §c§lJa §8/ " + uuid.reasonString)
                            } else {
                                sender.sendMessage("§7Gebannt: §a§lNein")
                            }
                            if (BanManager.isMuted(uuid)) {
                                sender.sendMessage("§7Gemutet: §c§lJa §8/ " + uuid.reasonString)
                            } else {
                                sender.sendMessage("§7Gemutet: §a§lNein")
                            }
                            if (IPManager.isBanned(IPManager.getIPFromPlayer(uuid).toString())) {
                                sender.sendMessage("§7IP-Ban: §c§lJa §8/ " + IPManager.getReasonString(IPManager.getIPFromPlayer(uuid).toString()))
                            } else {
                                sender.sendMessage("§7IP-Ban: §a§lNein")
                            }
                            sender.sendMessage("§7Bans: §e§l" + BanManager.getBans(uuid))
                            sender.sendMessage("§7Mutes: §e§l" + BanManager.getMutes(uuid))
                            sender.sendMessage("§7Letzter Login: §e§l" + BanManager.formatTimestamp(java.lang.Long.valueOf(BanManager.getLastLogin(uuid))))
                            sender.sendMessage("§7Erster Login: §e§l" + BanManager.formatTimestamp(java.lang.Long.valueOf(BanManager.getFirstLogin(uuid))))
                            sender.sendMessage("§8[]===================================[]")
                        } else {
                            sender.sendMessage(prefix + "§cDieser Spieler hat den Server noch nie betreten")
                        }
                    }
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        } else {
            if (args.isEmpty()) {
                console.sendMessage(prefix + "/check <Spieler/IP>")
            } else {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                if (IPBan.validate(args[0])) {
                    val ip = args[0]
                    if (IPManager.ipExists(ip)) {
                        console.sendMessage("§8[]===================================[]")
                        if (IPManager.getPlayerFromIP(ip) != null) {
                            console.sendMessage("§7Spieler: §e§l" + (IPManager.getPlayerFromIP(ip)
                                    ?: return).name)
                        } else {
                            console.sendMessage("§7Spieler: §c§lKeiner")
                        }
                        if (IPManager.isBanned(ip)) {
                            console.sendMessage("§7IP-Ban: §c§lJa §8/ " + IPManager.getReasonString(IPManager.getIPFromPlayer(uuid).toString()))
                        } else {
                            console.sendMessage("§7IP-Ban: §a§lNein")
                        }
                        console.sendMessage("§7Bans: §e§l" + IPManager.getBans(ip))
                        console.sendMessage("§7Zuletzt genutzt: §e§l" + BanManager.formatTimestamp(IPManager.getLastUseLong(ip)))
                        console.sendMessage("§8[]===================================[]")
                    } else {
                        console.sendMessage(prefix + "§cZu dieser IP-Adresse sind keine Informationen verfügbar")
                    }
                } else {
                    if (BanManager.playerExists(uuid)) {
                        console.sendMessage("§8[]===================================[]")
                        console.sendMessage("§7Spieler: §e§l" + uuid.name)
                        if (BanManager.isBanned(uuid)) {
                            console.sendMessage("§7Gebannt: §c§lJa §8/ " + uuid.reasonString)
                        } else {
                            console.sendMessage("§7Gebannt: §a§lNein")
                        }
                        if (BanManager.isMuted(uuid)) {
                            console.sendMessage("§7Gemutet: §c§lJa §8/ " + uuid.reasonString)
                        } else {
                            console.sendMessage("§7Gemutet: §a§lNein")
                        }
                        if (IPManager.isBanned(IPManager.getIPFromPlayer(uuid).toString())) {
                            console.sendMessage("§7IP-Ban: §c§lJa §8/ " + IPManager.getReasonString(IPManager.getIPFromPlayer(uuid).toString()))
                        } else {
                            console.sendMessage("§7IP-Ban: §a§lNein")
                        }
                        console.sendMessage("§7Bans: §e§l" + BanManager.getBans(uuid))
                        console.sendMessage("§7Mutes: §e§l" + BanManager.getMutes(uuid))
                        if (BanManager.getLastLogin(uuid) != null) {
                            console.sendMessage("§7Letzter Login: §e§l" + BanManager.formatTimestamp(java.lang.Long.valueOf(BanManager.getLastLogin(uuid))))
                        }
                        if (BanManager.getFirstLogin(uuid) != null) {
                            console.sendMessage("§7Erster Login: §e§l" + BanManager.formatTimestamp(java.lang.Long.valueOf(BanManager.getFirstLogin(uuid))))
                        }
                        console.sendMessage("§8[]===================================[]")
                    } else {
                        console.sendMessage(prefix + "§cDieser Spieler hat den Server noch nie betreten")
                    }
                }
            }
        }
    }
}