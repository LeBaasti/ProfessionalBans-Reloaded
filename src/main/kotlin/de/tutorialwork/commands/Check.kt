package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.noPerms
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import java.lang.Long

class Check(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.check")) {
                if (args.isEmpty()) {
                    sender.msg("$prefix/check <Spieler/IP>")
                } else {
                    val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                    if (IPBan.validate(args[0])) {
                        val ip = args[0]
                        if (ip.ipExists) {
                            sender.msg("§8[]===================================[]")
                            sender.msg("§7Spieler: §e§l" + ip.player.name)
                            if (ip.isBanned) {
                                sender.msg("§7IP-Ban: §c§lJa §8/ " + uuid.ip.reason)
                            } else {
                                sender.msg("§7IP-Ban: §a§lNein")
                            }
                            sender.msg("§7Bans: §e§l" + ip.bans)
                            sender.msg("§7Zuletzt genutzt: §e§l" + ip.lastUseLong.formatTime)
                            sender.msg("§8[]===================================[]")
                        } else {
                            sender.msg("$prefix§cZu dieser IP-Adresse sind keine Informationen verfügbar")
                        }
                    } else {
                        if (uuid.playerExists()) {
                            sender.msg("§8[]===================================[]")
                            sender.msg("§7Spieler: §e§l" + uuid.name)
                            if (uuid.isBanned) {
                                sender.msg("§7Gebannt: §c§lJa §8/ " + uuid.reasonString)
                            } else {
                                sender.msg("§7Gebannt: §a§lNein")
                            }
                            if (uuid.isMuted) {
                                sender.msg("§7Gemutet: §c§lJa §8/ " + uuid.reasonString)
                            } else {
                                sender.msg("§7Gemutet: §a§lNein")
                            }
                            if (uuid.ip.isBanned) {
                                sender.msg("§7IP-Ban: §c§lJa §8/ " + uuid.ip.reason)
                            } else {
                                sender.msg("§7IP-Ban: §a§lNein")
                            }
                            sender.msg("§7Bans: §e§l" + uuid.bans)
                            sender.msg("§7Mutes: §e§l" + uuid.mutes)
                            sender.msg("§7Letzter Login: §e§l" + Long.valueOf(uuid.lastLogin).formatTime)
                            sender.msg("§7Erster Login: §e§l" + Long.valueOf(uuid.firstLogin).formatTime)
                            sender.msg("§8[]===================================[]")
                        } else {
                            sender.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
                        }
                    }
                }
            } else {
                sender.msg(noPerms)
            }
        } else {
            if (args.isEmpty()) {
                console.msg("$prefix/check <Spieler/IP>")
            } else {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                if (IPBan.validate(args[0])) {
                    val ip = args[0]
                    if (ip.ipExists) {
                        console.msg("§8[]===================================[]")
                        console.msg("§7Spieler: §e§l" + ip.player.name)
                        if (ip.isBanned) {
                            console.msg("§7IP-Ban: §c§lJa §8/ " + uuid.ip.reason)
                        } else {
                            console.msg("§7IP-Ban: §a§lNein")
                        }
                        console.msg("§7Bans: §e§l" + ip.bans)
                        console.msg("§7Zuletzt genutzt: §e§l" + ip.lastUseLong.formatTime)
                        console.msg("§8[]===================================[]")
                    } else {
                        console.msg("$prefix§cZu dieser IP-Adresse sind keine Informationen verfügbar")
                    }
                } else {
                    if (uuid.playerExists()) {
                        console.msg("§8[]===================================[]")
                        console.msg("§7Spieler: §e§l" + uuid.name)
                        if (uuid.isBanned) {
                            console.msg("§7Gebannt: §c§lJa §8/ " + uuid.reasonString)
                        } else {
                            console.msg("§7Gebannt: §a§lNein")
                        }
                        if (uuid.isMuted) {
                            console.msg("§7Gemutet: §c§lJa §8/ " + uuid.reasonString)
                        } else {
                            console.msg("§7Gemutet: §a§lNein")
                        }
                        if (uuid.ip.isBanned) {
                            console.msg("§7IP-Ban: §c§lJa §8/ " + uuid.ip.reason)
                        } else {
                            console.msg("§7IP-Ban: §a§lNein")
                        }
                        console.msg("§7Bans: §e§l" + uuid.bans)
                        console.msg("§7Mutes: §e§l" + uuid.mutes)
                        console.msg("§7Letzter Login: §e§l" + Long.valueOf(uuid.lastLogin).formatTime)
                        console.msg("§7Erster Login: §e§l" + Long.valueOf(uuid.firstLogin).formatTime)
                        console.msg("§8[]===================================[]")
                    } else {
                        console.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
                    }
                }
            }
        }
    }
}