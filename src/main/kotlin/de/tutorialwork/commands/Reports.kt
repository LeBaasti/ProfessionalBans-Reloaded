package de.tutorialwork.commands

import de.tutorialwork.global.console
import de.tutorialwork.global.prefix
import de.tutorialwork.global.proxyServer
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

object Reports : Command(simpleName<Reports>()) {

	override fun execute(sender: CommandSender, args: Array<String>) = sender.hasPermission(name) {
		if (sender !is ProxiedPlayer) {
			console.msg("${prefix}§e§lReports §7sind nur als Spieler verfügbar")
			return
		}
		if (args.isEmpty()) {
			if (countOpenReports() != 0) {
				sender.msg("§8[]===================================[]")
				sender.msg("§e§loffene Reports §7(§8" + countOpenReports() + "§7)")
				var offline = 0
				for (element in iDsFromOpenReports) {
					val target = proxyServer.getPlayer(element.reportName)
					if (target != null) {
						val tc = TextComponent()
						tc.text = "§e§l${target.name} §7gemeldet wegen §c§l ${element.reportReason} §8| §7Online auf §e§l${target.server.info.name}"
						tc.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/${name.toLowerCase()} jump ${target.name} $element")
						tc.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("§7Klicken um §e§l${target.name} §7nachzuspringen").create())
						sender.sendMessage(tc)
					} else offline++
				}
				if (offline != 0) sender.msg("§4§o$offline Reports §7§ovon Spieler die offline sind ausgeblendet")
				sender.msg("§8[]===================================[]")
			} else sender.msg("${prefix}§cEs sind derzeit keine Reports offen")
		} else if (args[0].equals("jump", ignoreCase = true)) {
			val target = proxyServer.getPlayer(args[1])
			if (target == null) {
				sender.msg("${prefix}§cDieser Spieler ist nicht mehr online")
				return@hasPermission
			}
			sender.connect(target.server.info)
			val id = args[2].toInt()
			id.setReportDone()
			sender.uniqueId.reportTeam(id)
			sender.uniqueId.createLogEntry(null, ActionType.Report("REPORT_ACCEPT"))
			sender.msg("${prefix}Du hast den Report von §e§l${id.reportName} §7wegen §c§l${id.reportReason} §aangenommen")
		}
	}
}