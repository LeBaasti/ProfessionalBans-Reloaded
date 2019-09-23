package de.tutorialwork.commands

import de.tutorialwork.global.*
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

object Blacklist : Command(simpleName<Blacklist>()) {

    override fun execute(sender: CommandSender, args: Array<String>) = sender.hasPermission(name) {
        if (args.size != 2) {
            sender.msg("${prefix}Derzeit sind §e§l${blacklist.size} Wörter §7auf der Blacklist")
            sender.msg("$prefix/${name.toLowerCase()} <add/del> <Wort>")
            return
        }
        val word = args[1]
        val x = when (args[0].toLowerCase()) {
            "add" -> {
                blacklist.add(word)
                "hinzugefügt"
            }
            "del" -> {
                if (word !in blacklist) {
                    sender.msg("${prefix}§cDieses Wort steht nicht auf der Blacklist")
                    return
                }
                blacklist.remove(word)
                "entfernt"
            }
            else -> return
        }
        LogManager.createEntry("", sender.executor, ActionType.Blacklist("${args[0].toUpperCase()}_WORD_BLACKLIST"))
        sender.msg("${prefix}§e§l$word §7wurde zur Blacklist $x")
        blacklistConfig.set("BLACKLIST", blacklist)
        saveConfig(blacklistConfig, blacklistFile)
    }

}