package de.tutorialwork.commands

import de.tutorialwork.global.config
import de.tutorialwork.global.executor
import de.tutorialwork.global.prefix
import de.tutorialwork.global.proxyServer
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

object Kick : Command(simpleName<Kick>()) {

    override fun execute(sender: CommandSender, args: Array<String>) = sender.hasPermission(name.toLowerCase()) {
        if (args.size != 2) {
            sender.msg("$prefix/${name.toLowerCase()} <Spieler> <Grund>")
            return
        }
        val tokick = proxyServer.getPlayer(args[0])
        if (tokick != null) {
            val grund = args.joinToString(" ")
            ActionType.Kick(grund).sendNotify(tokick.name, sender.name)
            LogManager.createEntry(tokick.uniqueId.toString(), sender.executor, ActionType.Kick(grund))
            tokick.kick(config.getString("LAYOUT.KICK")
                    .replace("%grund%", grund).translateColors())
        } else sender.msg("${prefix}§cDieser Spieler ist nicht online")
    }
}