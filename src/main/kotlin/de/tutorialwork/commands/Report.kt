package de.tutorialwork.commands

import de.tutorialwork.config
import de.tutorialwork.console
import de.tutorialwork.main.Main
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Report(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (args.isEmpty() || args.size == 1) {
                var reasons = ""
                var komma = Main.reportreasons.size
                for (reason in Main.reportreasons) {
                    komma--
                    reasons = if (komma != 0) {
                        "$reasons$reason, "
                    } else {
                        reasons + reason
                    }
                }
                sender.sendMessage(prefix + "Verfügbare Reportgründe: §e§l" + reasons)
                sender.sendMessage(prefix + "/report <Spieler> <Grund>")
            } else {
                if (args[0].toUpperCase() == sender.name.toUpperCase()) {
                    sender.sendMessage(prefix + "§cDu kannst dich nicht selbst melden")
                    return
                }
                val reason = args[1].toUpperCase()
                if (Main.reportreasons.contains(reason)) {
                    val target = ProxyServer.getInstance().getPlayer(args[0])
                    if (target != null) {
                        BanManager.createReport(target.uniqueId, sender.uniqueId.toString(), reason, null)
                        sender.sendMessage(prefix + "Der Spieler §e§l" + target.name + " §7wurde erfolgreich wegen §e§l" + reason + " §7gemeldet")
                        ActionType.Report(reason).sendNotify(target.name, sender.name)
                        LogManager.createEntry(target.uniqueId.toString(), sender.uniqueId.toString(), ActionType.Report(reason))
                    } else {
                        if (config.getBoolean("REPORTS.OFFLINEREPORTS")) {
                            val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                            if (BanManager.playerExists(uuid)) {
                                BanManager.createReport(uuid, sender.uniqueId.toString(), reason, null)
                                sender.sendMessage(prefix + "Der Spieler §e§l" + args[0] + " §7(§4Offline§7) wurde erfolgreich wegen §e§l" + reason + " §7gemeldet")
                                LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "REPORT_OFFLINE", reason)
                            } else {
                                sender.sendMessage(prefix + "§cDieser Spieler wurde nicht gefunden")
                            }
                        } else {
                            sender.sendMessage(prefix + "§cDieser Spieler ist offline")
                        }

                    }
                } else {
                    sender.sendMessage(prefix + "§cDer eingegebene Reportgrund wurde nicht gefunden")
                }
            }
        } else {
            console.sendMessage(prefix + "§e§lReports §7sind nur als Spieler verfügbar")
        }
    }
}