package de.tutorialwork.commands

import de.tutorialwork.global.executor
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*
import de.tutorialwork.utils.ActionType.UnBanIp
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

object UnBan : Command(simpleName<UnBan>()) {

	override fun execute(sender: CommandSender, args: Array<String>) = sender.hasPermission(name) {
		if (args.isEmpty() || args.size >= 2) sender.msg("$prefix/unBan <Spieler/IP>")
		else {
			val executor = sender.executor
			val uuid = UUIDFetcher.getUUID(args[0]) ?: return
			if (IPBan.validate(args[0])) {
				args[0].unBan()
				UnBanIp.sendNotify(args[0], sender.name)
				sender.msg("${prefix}§7Die IP-Adresse §e§l${args[0]} §7wurde §aerfolgreich §7entbannt")
				null.createLogEntry(executor, UnBanIp)
			} else {
				if (uuid.playerExists()) {
					if (uuid.ip.isBanned) {
						uuid.ip.unBan()
						sender.msg("${prefix}Die IP §e§l${uuid.ip} §7war gebannt und wurde ebenfalls §aentbannt")
					}
					when {
						uuid.isBanned -> {
							uuid.unBan()
							ActionType.UnBan.sendNotify(uuid.name, sender.name)
							sender.msg("${prefix}§e§l${uuid.name} §7wurde §aerfolgreich §7entbannt")
							uuid.createLogEntry(executor, ActionType.UnBan)
						}
						uuid.isMuted -> {
							uuid.unMute()
							ActionType.UnMute.sendNotify(uuid.name, sender.name)
							sender.msg("${prefix}§e§l${uuid.name} §7wurde §aerfolgreich §7entmutet")
							uuid.createLogEntry(executor, ActionType.UnMute)
						}
						else -> sender.msg("${prefix}§e§l${uuid.name} §7ist weder gebannt oder gemutet")
					}
				} else sender.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
			}
		}
	}
}