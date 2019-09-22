package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.consoleName
import de.tutorialwork.main.Main
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import de.tutorialwork.utils.ActionType.UnBanIp
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Unban(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.unban") || sender.hasPermission("professionalbans.*")) {
                if (args.isEmpty()) {
                    sender.sendMessage(prefix + "/unban <Spieler/IP>")
                } else {
                    val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                    if (IPBan.validate(args[0])) {
                        IPManager.unban(args[0])
                        UnBanIp.sendNotify(args[0], sender.name)
                        sender.sendMessage(prefix + "§7Die IP-Adresse §e§l" + args[0] + " §7wurde §aerfolgreich §7entbannt")
                        LogManager.createEntry("", sender.uniqueId.toString(), "UNBAN_IP", args[0])
                    } else {
                        if (BanManager.playerExists(uuid)) {
                            if (IPManager.isBanned(IPManager.getIPFromPlayer(uuid).toString())) {
                                IPManager.unban(IPManager.getIPFromPlayer(uuid).toString())
                                sender.sendMessage(prefix + "Die IP §e§l" + IPManager.getIPFromPlayer(uuid) + " §7war gebannt und wurde ebenfalls §aentbannt")
                            }
                            when {
                                BanManager.isBanned(uuid) -> {
                                    BanManager.unban(uuid)
                                    ActionType.UnBan.sendNotify(uuid.name.toString(), sender.name)
                                    sender.sendMessage(prefix + "§e§l" + uuid.name + " §7wurde §aerfolgreich §7entbannt")
                                    LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "UNBAN_BAN", null)
                                }
                                BanManager.isMuted(uuid) -> {
                                    BanManager.unmute(uuid)
                                    ActionType.UnMute.sendNotify(uuid.name, sender.name)
                                    sender.sendMessage(prefix + "§e§l" + uuid.name + " §7wurde §aerfolgreich §7entmutet")
                                    LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "UNBAN_MUTE", null)
                                }
                                else -> sender.sendMessage(prefix + "§e§l" + uuid.name + " §7ist weder gebannt oder gemutet")
                            }
                        } else {
                            sender.sendMessage("$prefix§cDieser Spieler hat den Server noch nie betreten")
                        }
                    }
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        } else {
            if (args.isEmpty()) {
                console.sendMessage(prefix + "/unban <Spieler/IP>")
            } else {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                if (IPBan.validate(args[0])) {
                    IPManager.unban(args[0])
                    UnBanIp.sendNotify(args[0], consoleName)
                    console.sendMessage(prefix + "§7Die IP-Adresse §e§l" + args[0] + " §7wurde §aerfolgreich §7entbannt")
                    LogManager.createEntry("", consoleName, UnBanIp, args[0])
                } else {
                    if (BanManager.playerExists(uuid)) {
                        if (IPManager.isBanned(IPManager.getIPFromPlayer(uuid).toString())) {
                            IPManager.unban(IPManager.getIPFromPlayer(uuid).toString())
                            console.sendMessage(prefix + "Die IP §e§l" + IPManager.getIPFromPlayer(uuid) + " §7war gebannt und wurde ebenfalls §aentbannt")
                        }
                        when {
                            BanManager.isBanned(uuid) -> {
                                BanManager.unban(uuid)
                                ActionType.UnBan.sendNotify(uuid.name.toString(), consoleName)
                                console.sendMessage(prefix + "§e§l" + uuid.name + " §7wurde §aerfolgreich §7entbannt")
                                LogManager.createEntry(uuid.toString(), consoleName, "UNBAN_BAN")
                            }
                            BanManager.isMuted(uuid) -> {
                                BanManager.unmute(uuid)
                                ActionType.UnMute.sendNotify(uuid.name.toString(), consoleName)
                                console.sendMessage(prefix + "§e§l" + uuid.name + " §7wurde §aerfolgreich §7entmutet")
                                LogManager.createEntry(uuid.toString(), consoleName, "UNBAN_MUTE")
                            }
                            else -> console.sendMessage(prefix + "§e§l" + uuid.name + " §7ist weder gebannt oder gemutet")
                        }
                    } else {
                        console.sendMessage(prefix + "§cDieser Spieler hat den Server noch nie betreten")
                    }
                }
            }
        }
    }
}