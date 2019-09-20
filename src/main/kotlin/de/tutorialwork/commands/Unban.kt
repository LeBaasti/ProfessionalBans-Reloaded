package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.main.Main
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.IPManager
import de.tutorialwork.utils.LogManager
import de.tutorialwork.utils.UUIDFetcher
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Unban(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.unban") || sender.hasPermission("professionalbans.*")) {
                if (args.isEmpty()) {
                    sender.sendMessage(Main.prefix + "/unban <Spieler/IP>")
                } else {
                    val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                    if (IPBan.validate(args[0])) {
                        IPManager.unban(args[0])
                        BanManager.sendNotify("UNBANIP", args[0], sender.name, null)
                        sender.sendMessage(Main.prefix + "§7Die IP-Adresse §e§l" + args[0] + " §7wurde §aerfolgreich §7entbannt")
                        LogManager.createEntry("", sender.uniqueId.toString(), "UNBAN_IP", args[0])
                    } else {
                        if (BanManager.playerExists(uuid)) {
                            if (IPManager.isBanned(IPManager.getIPFromPlayer(uuid).toString())) {
                                IPManager.unban(IPManager.getIPFromPlayer(uuid).toString())
                                sender.sendMessage(Main.prefix + "Die IP §e§l" + IPManager.getIPFromPlayer(uuid) + " §7war gebannt und wurde ebenfalls §aentbannt")
                            }
                            when {
                                BanManager.isBanned(uuid) -> {
                                    BanManager.unban(uuid)
                                    BanManager.sendNotify("UNBAN", BanManager.getNameByUUID(uuid).toString(), sender.name, "null")
                                    sender.sendMessage(Main.prefix + "§e§l" + BanManager.getNameByUUID(uuid) + " §7wurde §aerfolgreich §7entbannt")
                                    LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "UNBAN_BAN", null)
                                }
                                BanManager.isMuted(uuid) -> {
                                    BanManager.unmute(uuid)
                                    BanManager.sendNotify("UNMUTE", BanManager.getNameByUUID(uuid).toString(), sender.name, "null")
                                    sender.sendMessage(Main.prefix + "§e§l" + BanManager.getNameByUUID(uuid) + " §7wurde §aerfolgreich §7entmutet")
                                    LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "UNBAN_MUTE", null)
                                }
                                else -> sender.sendMessage(Main.prefix + "§e§l" + BanManager.getNameByUUID(uuid) + " §7ist weder gebannt oder gemutet")
                            }
                        } else {
                            sender.sendMessage(Main.prefix + "§cDieser Spieler hat den Server noch nie betreten")
                        }
                    }
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        } else {
            if (args.isEmpty()) {
                console.sendMessage(Main.prefix + "/unban <Spieler/IP>")
            } else {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                if (IPBan.validate(args[0])) {
                    IPManager.unban(args[0])
                    BanManager.sendNotify("UNBANIP", args[0], "KONSOLE", null)
                    console.sendMessage(Main.prefix + "§7Die IP-Adresse §e§l" + args[0] + " §7wurde §aerfolgreich §7entbannt")
                    LogManager.createEntry("", "KONSOLE", "UNBAN_IP", args[0])
                } else {
                    if (BanManager.playerExists(uuid)) {
                        if (IPManager.isBanned(IPManager.getIPFromPlayer(uuid).toString())) {
                            IPManager.unban(IPManager.getIPFromPlayer(uuid).toString())
                            console.sendMessage(Main.prefix + "Die IP §e§l" + IPManager.getIPFromPlayer(uuid) + " §7war gebannt und wurde ebenfalls §aentbannt")
                        }
                        when {
                            BanManager.isBanned(uuid) -> {
                                BanManager.unban(uuid)
                                BanManager.sendNotify("UNBAN", BanManager.getNameByUUID(uuid).toString(), "KONSOLE", "null")
                                console.sendMessage(Main.prefix + "§e§l" + BanManager.getNameByUUID(uuid) + " §7wurde §aerfolgreich §7entbannt")
                                LogManager.createEntry(uuid.toString(), "KONSOLE", "UNBAN_BAN", null)
                            }
                            BanManager.isMuted(uuid) -> {
                                BanManager.unmute(uuid)
                                BanManager.sendNotify("UNMUTE", BanManager.getNameByUUID(uuid).toString(), "KONSOLE", "null")
                                console.sendMessage(Main.prefix + "§e§l" + BanManager.getNameByUUID(uuid) + " §7wurde §aerfolgreich §7entmutet")
                                LogManager.createEntry(uuid.toString(), "KONSOLE", "UNBAN_MUTE", null)
                            }
                            else -> console.sendMessage(Main.prefix + "§e§l" + BanManager.getNameByUUID(uuid) + " §7ist weder gebannt oder gemutet")
                        }
                    } else {
                        console.sendMessage(Main.prefix + "§cDieser Spieler hat den Server noch nie betreten")
                    }
                }
            }
        }
    }
}