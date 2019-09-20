package de.tutorialwork.commands

import de.tutorialwork.listener.Chat
import de.tutorialwork.main.Main
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.LogManager
import de.tutorialwork.utils.exists
import de.tutorialwork.utils.getUUID
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File

class Chatlog(name: String) : Command(name) {

    private val chatLogUrl by lazy {
        val config = File(Main.instance.dataFolder, "config.yml")
        val cfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
        cfg.getString("CHATLOG.URL")
    }


    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is ProxiedPlayer) return
        if (args.size != 1) {

            val target = args[0].getUUID(sender) ?: return
            target.exists(sender) ?: return

            if (sender.uniqueId != target) {
                if (Chat.hasMessages(target)) {
                    val id = Chat.createChatlog(target, sender.uniqueId.toString()) ?: return
                    sender.sendMessage(Main.prefix + "Der Chatlog von §e§l" + BanManager.getNameByUUID(target) + " §7wurde erfolgreich erstellt")
                    sender.sendMessage(Main.prefix + "Link: §e§l$chatLogUrl$id")
                    LogManager.createEntry(target.toString(), sender.uniqueId.toString(), "CREATE_CHATLOG", id)
                } else sender.sendMessage(Main.prefix + "§cDieser Spieler hat in der letzten Zeit keine Nachrichten verfasst")
            } else sender.sendMessage(Main.prefix + "§cDu kannst kein Chatlog von dir selbst erstellen")
        } else sender.sendMessage(Main.prefix + "/chatlog <Spieler>")
    }

}