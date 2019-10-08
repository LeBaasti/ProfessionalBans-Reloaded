package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.global.console
import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.prefix
import de.tutorialwork.global.proxyServer
import de.tutorialwork.utils.*
import net.kyori.text.TextComponent
import net.kyori.text.event.ClickEvent
import net.kyori.text.event.HoverEvent

class Reports : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (source !is Player) {
            console.msg("${prefix}§e§lReports §7sind nur als Spieler verfügbar")
            return
        }
        if (args.isEmpty()) {
            if (countOpenReports() != 0) {
                source.msg("§8[]===================================[]")
                source.msg("§e§loffene Reports §7(§8" + countOpenReports() + "§7)")
                var offline = 0
                for (element in iDsFromOpenReports) {
                    val targetPlayer = proxyServer.getPlayer(element.reportName)
                    val target = targetPlayer.get()
                    if (targetPlayer.isPresent) {
                        val tc = TextComponent.builder()
                        tc.content("§e§l${target} §7gemeldet wegen §c§l ${element.reportReason} §8| §7Online auf §e§l${target.currentServer.get().serverInfo.name}")
                        tc.clickEvent(ClickEvent.runCommand("/report jump ${target.username} $element"))
                        tc.hoverEvent(HoverEvent.showText(TextComponent.of("§7Klicken um §e§l${target.username} §7nachzuspringen")))
                        source.sendMessage(tc.build())
                    } else offline++
                }
                if (offline != 0) source.msg("§4§o$offline Reports §7§ovon Spieler die offline sind ausgeblendet")
                source.msg("§8[]===================================[]")
            } else source.msg("${prefix}§cEs sind derzeit keine Reports offen")
        } else if (args[0].equals("jump", ignoreCase = true)) {
            val targetPlayer = proxyServer.getPlayer(args[1])
            val target = targetPlayer.get()
            if (!targetPlayer.isPresent) {
                source.msg("${prefix}§cDieser Spieler ist nicht mehr online")
                return
            }
            source.connect(target.currentServer.get().serverInfo.name)
            val id = args[2].toInt()
            id.setReportDone()
            source.uniqueId.reportTeam(id)
            source.uniqueId.createLogEntry(null, ActionType.Report("REPORT_ACCEPT"))
            source.msg("${prefix}Du hast den Report von §e§l${id.reportName} §7wegen §c§l${id.reportReason} §aangenommen")
        }
    }

    override fun hasPermission(source: CommandSource, args: Array<out String>): Boolean = source.hasPermission("$permissionPrefix.commands.reports")
}