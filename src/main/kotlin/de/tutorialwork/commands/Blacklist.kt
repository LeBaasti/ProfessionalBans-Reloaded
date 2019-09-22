package de.tutorialwork.commands

import de.tutorialwork.*
import de.tutorialwork.utils.ActionType
import de.tutorialwork.utils.LogManager
import de.tutorialwork.utils.msg
import de.tutorialwork.utils.saveConfig
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Blacklist : Command(Blacklist::class.java.simpleName) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender.hasPermission("professionalbans.blacklist")) {
            sender.msg(noPerms)
            return
        }
        if (args.size != 2) {
            sender.msg("${prefix}Derzeit sind §e§l${blacklist.size} Wörter §7auf der Blacklist")
            sender.msg("$prefix/blacklistFile <add/del> <Wort>")
            return
        }
        val executor = if (sender is ProxiedPlayer) sender.uniqueId.toString() else consoleName
        val word = args[1]
        when (args[0].toLowerCase()) {
            "add" -> {
                blacklist.add(word)
                sender.msg("$prefix§e§l$word §7wurde zur Blacklist hinzugefügt")
                LogManager.createEntry("", executor, ActionType.Blacklist("ADD_WORD_BLACKLIST"))
            }
            "del" -> {
                if (word !in blacklist) {
                    sender.msg("$prefix§cDieses Wort steht nicht auf der Blacklist")
                    return
                }
                blacklist.remove(word)
                sender.msg("$prefix§e§l$word §7wurde von der Blacklist entfernt")
                LogManager.createEntry("", executor, ActionType.Blacklist("DEL_WORD_BLACKLIST"))
            }
        }
        blacklistConfig.set("BLACKLIST", blacklist)
        saveConfig(blacklistConfig, blacklistFile)
    }

}