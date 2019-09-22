package de.tutorialwork.commands

import de.tutorialwork.config
import de.tutorialwork.listener.Chat
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Chatlog(name: String = Chatlog::class.java.simpleName) : Command(name) {

    private val chatLogUrl by lazy { config.getString("CHATLOG.URL") }


    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is ProxiedPlayer) return
        if (args.size != 1) {

            val target = args[0].getUUID(sender) ?: return
            target.exists(sender) ?: return

            if (sender.uniqueId != target) {
                if (Chat.hasMessages(target)) {
                    val id = Chat.createChatlog(target, sender.uniqueId.toString())
                    sender.msg(prefix + "Der Chatlog von §e§l" + target.name + " §7wurde erfolgreich erstellt")
                    sender.msg(prefix + "Link: §e§l$chatLogUrl$id")
                    LogManager.createEntry(target.toString(), sender.uniqueId.toString(), ActionType.Chatlog("CREATE_CHATLOG"))
                } else sender.msg("$prefix§cDieser Spieler hat in der letzten Zeit keine Nachrichten verfasst")
            } else sender.msg("$prefix§cDu kannst kein Chatlog von dir selbst erstellen")
        } else sender.msg("$prefix/chatlog <Spieler>")
    }

}