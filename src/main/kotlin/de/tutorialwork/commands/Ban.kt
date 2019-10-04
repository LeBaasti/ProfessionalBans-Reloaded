package de.tutorialwork.commands

import de.tutorialwork.global.executor
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

object Ban : Command(simpleName<Ban>()) {

	override fun execute(sender: CommandSender, args: Array<String>) = sender.hasPermission(name.toLowerCase()) {
		if (args.size != 2) {
			sender.sendBanReasonsList()
			sender.msg("$prefix/${name.toLowerCase()} <Spieler> <Grund-ID>")
			return
		}
		val uuid = UUIDFetcher.getUUID(args[0]) ?: return
		if (!uuid.playerExists()) {
			sender.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
			return
		}
		if (uuid.isWebaccountAdmin) {
			sender.msg("${prefix}§cDiesen Spieler kannst du nicht bannen/muten")
			return
		}
		val id = args[1].toIntOrNull() ?: return //add fail message
		val type = if (id.isBanReason) ActionType.Ban(id) else ActionType.Mute(id)
		if (id.hasExtraPermissions && !sender.hasPermission(id.extraPermissions)) {
			sender.msg("${prefix}§cDu hast keine Berechtigung diesen ${type::class.java.simpleName}grund zu nutzen")
			return
		}
		val executor = sender.executor
		type.execute(uuid, executor)
		type.sendNotify(uuid.name, sender.name)
		uuid.createLogEntry(executor, type)
	}
}