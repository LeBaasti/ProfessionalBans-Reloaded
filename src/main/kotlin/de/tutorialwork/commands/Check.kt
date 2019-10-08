package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*

class Check : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (args.size != 1) {
            source.msg("$prefix/check <Spieler/IP>")
            return
        }
        val value = args[0]
        val uuid = UUIDFetcher.getUUID(value) ?: return
        val no = "§a§lNein"
        val yes = "§c§lJa §8/ "
        val header = "§8[]===================================[]"
        if (validate(value)) {
            if (!value.ipExists) {
                source.msg("${prefix}§cZu dieser IP-Adresse sind keine Informationen verfügbar")
                return
            }
            source.msg(
                    header,
                    "§7Spieler: §e§l${value.player.name}",
                    "§7IP: §e§l${uuid.ip}",
                    "§7IP-Ban: ${if (!value.isBanned) no else "$yes${uuid.ip.reason}"}",
                    "§7Bans: §e§l${value.bans}",
                    "§7Zuletzt genutzt: §e§l${value.lastUseLong.formatTime}"
            )
        } else {
            if (!uuid.playerExists()) {
                source.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
                return
            }
            source.msg(header, "§7Spieler: §e§l${uuid.name}")
            if (source.hasPermission("${permissionPrefix}check.ip")) source.msg("§7IP: §e§l${uuid.ip}")
            source.msg("§7IP-Ban: ${if (!uuid.ip.isBanned) no else "$yes${uuid.ip.reason}"}",
                    "§7Bans: §e§l${uuid.bans}",
                    "§7Mutes: §e§l${uuid.mutes}",
                    "§7Gebannt: ${if (!uuid.isBanned) no else "$yes${uuid.reasonString}"}",
                    "§7Gemutet: ${if (!uuid.isMuted) no else "$yes${uuid.reasonString}"}",
                    "§7Letzter Login: §e§l${uuid.lastLogin.toLong().formatTime}",
                    "§7Erster Login: §e§l${uuid.firstLogin.toLong().formatTime}"
            )
        }
        source.msg(header)
    }

    override fun hasPermission(source: CommandSource, args: Array<out String>): Boolean = source.hasPermission("$permissionPrefix.commands.check")
    private val pattern = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$".toPattern()
    private fun validate(ip: String): Boolean = pattern.matcher(ip).matches()
}