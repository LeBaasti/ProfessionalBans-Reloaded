package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import de.tutorialwork.global.executor
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.toUUIDOrNull

class Ban : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (args.size != 2) {
            source.sendBanReasonsList()
            source.msg("$prefix/ban <Spieler> <Grund-ID>")
            return
        }
        val uuid = UUIDFetcher.getUUID(args[0]) ?: return
        if (!uuid.playerExists()) {
            source.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
            return
        }
        if (uuid.isWebaccountAdmin) {
            source.msg("${prefix}§cDiesen Spieler kannst du nicht bannen/muten")
            return
        }
        val id = args[1].toIntOrNull() ?: return //add fail message
        val type = if (id.isBanReason) ActionType.Ban(id) else ActionType.Mute(id)
        if (id.hasExtraPermissions && !source.hasPermission(id.extraPermissions)) {
            source.msg("${prefix}§cDu hast keine Berechtigung diesen ${type::class.java.simpleName}grund zu nutzen")
            return
        }
        val executor = source.executor
        type.execute(uuid, executor)
        type.sendNotify(uuid.name, executor.toUUIDOrNull()?.name ?: "none")
        uuid.createLogEntry(executor, type)
    }


}