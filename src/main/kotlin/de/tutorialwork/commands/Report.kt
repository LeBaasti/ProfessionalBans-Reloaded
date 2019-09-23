package de.tutorialwork.commands

import de.tutorialwork.global.*
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

object Report : Command(simpleName<Report>()) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is ProxiedPlayer) {
            console.msg("${prefix}§e§lReports §7sind nur als Spieler verfügbar")
            return
        }
        if (args.size <= 1) {
            val reasons = reportreasons.joinToString()
            sender.msg("${prefix}Verfügbare Reportgründe: §e§l$reasons")
            sender.msg("$prefix/${name.toLowerCase()} <Spieler> <Grund>")
        } else {
            if (args[0].toUpperCase() == sender.name.toUpperCase()) sender.msg("${prefix}§cDu kannst dich nicht selbst melden")
            else {
                val reason = args[1].toUpperCase()
                if (reason in reportreasons) {
                    val target = proxyServer.getPlayer(args[0])
                    if (target != null) {
                        target.uniqueId.createReport(sender.uniqueId.toString(), reason, null)
                        sender.msg("""${prefix}Der Spieler §e§l${target.name} §7wurde erfolgreich wegen §e§l$reason §7gemeldet""")
                        ActionType.Report(reason).sendNotify(target.name, sender.name)
                        LogManager.createEntry(target.uniqueId.toString(), sender.uniqueId.toString(), ActionType.Report(reason))
                    } else {
                        if (config.getBoolean("REPORTS.OFFLINEREPORTS")) {
                            val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                            if (uuid.playerExists()) {
                                uuid.createReport(sender.uniqueId.toString(), reason, null)
                                sender.msg("${prefix}Der Spieler §e§l${args[0]} §7(§4Offline§7) wurde erfolgreich wegen §e§l$reason §7gemeldet")
                                LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), ActionType.Report(reason))
                            } else sender.msg("${prefix}§cDieser Spieler wurde nicht gefunden")
                        } else sender.msg("${prefix}§cDieser Spieler ist offline")

                    }
                } else sender.msg("${prefix}§cDer eingegebene Reportgrund wurde nicht gefunden")
            }
        }
    }
}