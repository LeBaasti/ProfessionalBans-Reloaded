package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import de.tutorialwork.configs.config
import de.tutorialwork.global.executor
import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.prefix
import de.tutorialwork.global.proxyServer
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.toUUIDOrNull

class Kick : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (args.size != 2) {
            source.msg("$prefix/kick <Spieler> <Grund>")
            return
        }
        val tokickPlayer = proxyServer.getPlayer(args[0])
        val tokick = tokickPlayer.get()
        if (tokickPlayer.isPresent) {
            val grund = args.joinToString(" ")
            ActionType.Kick(grund).sendNotify(tokick.executor.toUUIDOrNull()?.name
                    ?: "none", source.executor.toUUIDOrNull()?.name ?: "none")
            tokick.uniqueId.createLogEntry(source.executor, ActionType.Kick(grund))
            tokick.kick(config.layoutKick
                    .replace("%grund%", grund).translateColors())
        } else source.msg("${prefix}§cDieser Spieler ist nicht online")
    }

    override fun hasPermission(source: CommandSource, args: Array<out String>): Boolean = source.hasPermission("$permissionPrefix.commands.kick")

}