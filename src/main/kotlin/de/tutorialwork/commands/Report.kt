package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.main.Main
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.LogManager
import de.tutorialwork.utils.UUIDFetcher
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration

import java.io.File
import java.io.IOException

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
                sender.sendMessage(Main.prefix + "Verfügbare Reportgründe: §e§l" + reasons)
                sender.sendMessage(Main.prefix + "/report <Spieler> <Grund>")
            } else {
                if (args[0].toUpperCase() == sender.name.toUpperCase()) {
                    sender.sendMessage(Main.prefix + "§cDu kannst dich nicht selbst melden")
                    return
                }
                if (Main.reportreasons.contains(args[1].toUpperCase())) {
                    val target = ProxyServer.getInstance().getPlayer(args[0])
                    if (target != null) {
                        BanManager.createReport(target.uniqueId, sender.uniqueId.toString(), args[1].toUpperCase(), null)
                        sender.sendMessage(Main.prefix + "Der Spieler §e§l" + target.name + " §7wurde erfolgreich wegen §e§l" + args[1].toUpperCase() + " §7gemeldet")
                        BanManager.sendNotify("REPORT", target.name, sender.name, args[1].toUpperCase())
                        LogManager.createEntry(target.uniqueId.toString(), sender.uniqueId.toString(), "REPORT", args[1].toUpperCase())
                    } else {
                        try {
                            val file = File(Main.instance.dataFolder, "config.yml")
                            val cfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
                            if (cfg.getBoolean("REPORTS.OFFLINEREPORTS")) {
                                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                                if (BanManager.playerExists(uuid)) {
                                    BanManager.createReport(uuid, sender.uniqueId.toString(), args[1].toUpperCase(), null)
                                    sender.sendMessage(Main.prefix + "Der Spieler §e§l" + args[0] + " §7(§4Offline§7) wurde erfolgreich wegen §e§l" + args[1].toUpperCase() + " §7gemeldet")
                                    LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "REPORT_OFFLINE", args[1].toUpperCase())
                                } else {
                                    sender.sendMessage(Main.prefix + "§cDieser Spieler wurde nicht gefunden")
                                }
                            } else {
                                sender.sendMessage(Main.prefix + "§cDieser Spieler ist offline")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    sender.sendMessage(Main.prefix + "§cDer eingegebene Reportgrund wurde nicht gefunden")
                }
            }
        } else {
            console.sendMessage(Main.prefix + "§e§lReports §7sind nur als Spieler verfügbar")
        }
    }
}