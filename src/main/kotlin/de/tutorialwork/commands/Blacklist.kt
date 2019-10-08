package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import de.tutorialwork.configs.blacklistConfig
import de.tutorialwork.configs.setValue
import de.tutorialwork.global.blacklist
import de.tutorialwork.global.executor
import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.ActionType
import de.tutorialwork.utils.createLogEntry
import de.tutorialwork.utils.msg

class Blacklist : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (args.size != 2) {
            source.msg("${prefix}Derzeit sind §e§l${blacklist.size} Wörter §7auf der Blacklist")
            source.msg("$prefix/blacklist <add/del> <Wort>")
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
                    source.msg("${prefix}§cDieses Wort steht nicht auf der Blacklist")
                    return
                }
                blacklist.remove(word)
                "entfernt"
            }
            else -> return
        }
        null.createLogEntry(source.executor, ActionType.Blacklist("${args[0].toUpperCase()}_WORD_BLACKLIST"))
        source.msg("${prefix}§e§l$word §7wurde zur Blacklist $x")
        setValue(blacklistConfig::blackList, blacklist)
    }

    override fun hasPermission(source: CommandSource, args: Array<out String>): Boolean = source.hasPermission("$permissionPrefix.commands.blacklist")


}