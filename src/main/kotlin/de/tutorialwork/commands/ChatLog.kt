package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.configs.config
import de.tutorialwork.global.executor
import de.tutorialwork.global.prefix
import de.tutorialwork.listener.Chat
import de.tutorialwork.utils.*

class ChatLog : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (args.size != 1) {
            source.msg("$prefix/chatlog <Spieler>")
            return
        }
        val target = args[0].getUUID(source) ?: return
        target.exists(source) ?: return
        if (source is Player && source.uniqueId == target) {
            source.msg("${prefix}§cDu kannst kein ChatLog von dir selbst erstellen")
            return
        }
        if (!Chat.hasMessages(target)) {
            source.msg("${prefix}§cDieser Spieler hat in der letzten Zeit keine Nachrichten verfasst")
            return
        }
        val executor = source.executor
        val id = Chat.createChatLog(target, executor)
        target.createLogEntry(executor, ActionType.Chatlog("CREATE_CHATLOG", id))
        source.msg("${prefix}Der ChatLog von §e§l${target.name} §7wurde erfolgreich erstellt")
        source.msg("${prefix}Link: §e§l${config.chatlogUrl}$id")
    }


}