package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.configs.config
import de.tutorialwork.global.console
import de.tutorialwork.global.prefix
import de.tutorialwork.global.proxyServer
import de.tutorialwork.global.reportReasons
import de.tutorialwork.utils.*

class Report : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (source !is Player) {
            console.msg("${prefix}§e§lReports §7sind nur als Spieler verfügbar")
            return
        }
        if (args.size <= 1) {
            val reasons = reportReasons.joinToString()
            source.msg("${prefix}Verfügbare Reportgründe: §e§l$reasons")
            source.msg("$prefix/report <Spieler> <Grund>")
        } else {
            if (args[0].toUpperCase() == source.username.toUpperCase()) source.msg("${prefix}§cDu kannst dich nicht selbst melden")
            else {
                val reason = args[1].toUpperCase()
                if (reason in reportReasons) {
                    val targetPlayer = proxyServer.getPlayer(args[0])
                    val target = targetPlayer.get()
                    if (targetPlayer.isPresent) {
                        target.uniqueId.createReport(source.uniqueId.toString(), reason, null)
                        source.msg("""${prefix}Der Spieler §e§l${target.username} §7wurde erfolgreich wegen §e§l$reason §7gemeldet""")
                        ActionType.Report(reason).sendNotify(target.username, source.username)
                        target.uniqueId.createLogEntry(source.uniqueId.toString(), ActionType.Report(reason))
                    } else {
                        if (config.offlineReports) {
                            val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                            if (uuid.playerExists()) {
                                uuid.createReport(source.uniqueId.toString(), reason, null)
                                source.msg("${prefix}Der Spieler §e§l${args[0]} §7(§4Offline§7) wurde erfolgreich wegen §e§l$reason §7gemeldet")
                                uuid.createLogEntry(source.uniqueId.toString(), ActionType.Report(reason))
                            } else source.msg("${prefix}§cDieser Spieler wurde nicht gefunden")
                        } else source.msg("${prefix}§cDieser Spieler ist offline")

                    }
                } else source.msg("${prefix}§cDer eingegebene Reportgrund wurde nicht gefunden")
            }
        }
    }


}