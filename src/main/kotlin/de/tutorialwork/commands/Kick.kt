package de.tutorialwork.commands

import de.tutorialwork.*
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Kick(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        val executor = if (sender is ProxiedPlayer) sender.uniqueId.toString() else consoleName
        if (sender.hasPermission("professionalbans.kick")) {
            if (args.isEmpty() || args.size == 1) {
                sender.msg("$prefix/kick <Spieler> <Grund>")
            } else {
                val tokick = proxyServer.getPlayer(args[0])
                if (tokick != null) {
                    val grund = args.joinToString { " " }
                    ActionType.Kick(grund).sendNotify(tokick.name, sender.name)
                    LogManager.createEntry(tokick.uniqueId.toString(), executor, ActionType.Kick(grund))
                    tokick.kick(config.getString("LAYOUT.KICK")
                            .replace("%grund%", grund).translateColors())

                } else sender.msg("$prefix§cDieser Spieler ist nicht online")
            }
        } else sender.msg(noPerms)
    }
}