package de.tutorialwork.commands

import de.tutorialwork.global.config
import de.tutorialwork.global.executor
import de.tutorialwork.global.prefix
import de.tutorialwork.listener.Chat
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

object ChatLog : Command(simpleName<ChatLog>()) {

	private val chatLogUrl by lazy { config.getString("CHATLOG.URL") }

	override fun execute(sender: CommandSender, args: Array<String>) {
		if (args.size != 1) {
			sender.msg("$prefix/${name.toLowerCase()} <Spieler>")
			return
		}
		val target = args[0].getUUID(sender) ?: return
		target.exists(sender) ?: return
		if (sender is ProxiedPlayer && sender.uniqueId == target) {
			sender.msg("${prefix}§cDu kannst kein ChatLog von dir selbst erstellen")
			return
		}
		if (!Chat.hasMessages(target)) {
			sender.msg("${prefix}§cDieser Spieler hat in der letzten Zeit keine Nachrichten verfasst")
			return
		}
		val executor = sender.executor
		val id = Chat.createChatLog(target, executor)
		target.createLogEntry(executor, ActionType.Chatlog("CREATE_CHATLOG", id))
		sender.msg("${prefix}Der ChatLog von §e§l${target.name} §7wurde erfolgreich erstellt")
		sender.msg("${prefix}Link: §e§l$chatLogUrl$id")
	}

}