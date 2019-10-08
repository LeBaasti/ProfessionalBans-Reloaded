package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import de.tutorialwork.global.executor
import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*
import de.tutorialwork.utils.ActionType.UnBanIp
import net.darkdevelopers.darkbedrock.darkness.general.functions.toUUIDOrNull

class UnBan : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (args.isEmpty() || args.size >= 2) source.msg("$prefix/unBan <Spieler/IP>")
        else {
            val executor = source.executor
            val uuid = UUIDFetcher.getUUID(args[0]) ?: return
            if (validate(args[0])) {
                args[0].unBan()
                UnBanIp.sendNotify(args[0], executor.toUUIDOrNull()?.name ?: "none")
                source.msg("${prefix}§7Die IP-Adresse §e§l${args[0]} §7wurde §aerfolgreich §7entbannt")
                null.createLogEntry(executor, UnBanIp)
            } else {
                if (uuid.playerExists()) {
                    if (uuid.ip.isBanned) {
                        uuid.ip.unBan()
                        source.msg("${prefix}Die IP §e§l${uuid.ip} §7war gebannt und wurde ebenfalls §aentbannt")
                    }
                    when {
                        uuid.isBanned -> {
                            uuid.unBan()
                            ActionType.UnBan.sendNotify(uuid.name, executor.toUUIDOrNull()?.name ?: "none")
                            source.msg("${prefix}§e§l${uuid.name} §7wurde §aerfolgreich §7entbannt")
                            uuid.createLogEntry(executor, ActionType.UnBan)
                        }
                        uuid.isMuted -> {
                            uuid.unMute()
                            ActionType.UnMute.sendNotify(uuid.name, executor.toUUIDOrNull()?.name ?: "none")
                            source.msg("${prefix}§e§l${uuid.name} §7wurde §aerfolgreich §7entmutet")
                            uuid.createLogEntry(executor, ActionType.UnMute)
                        }
                        else -> source.msg("${prefix}§e§l${uuid.name} §7ist weder gebannt oder gemutet")
                    }
                } else source.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
            }
        }
    }

    override fun hasPermission(source: CommandSource, args: Array<out String>): Boolean = source.hasPermission("$permissionPrefix.commands.unban")
    private val pattern = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$".toPattern()
    private fun validate(ip: String): Boolean = pattern.matcher(ip).matches()
}