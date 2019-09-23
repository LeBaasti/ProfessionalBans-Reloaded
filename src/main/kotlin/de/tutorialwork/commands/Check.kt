package de.tutorialwork.commands

import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command

object Check : Command(simpleName<Check>()) {

    override fun execute(sender: CommandSender, args: Array<String>) = sender.hasPermission(name.toLowerCase()) {
        if (args.size != 1) {
            sender.msg("$prefix/${name.toLowerCase()} <Spieler/IP>")
            return
        }
        val value = args[0]
        val uuid = UUIDFetcher.getUUID(value) ?: return
        val no = "§a§lNein"
        val yes = "§c§lJa §8/ "
        val header = "§8[]===================================[]"
        if (IPBan.validate(value)) {
            if (!value.ipExists) {
                sender.msg("${prefix}§cZu dieser IP-Adresse sind keine Informationen verfügbar")
                return
            }
            sender.msg(
                    header,
                    "§7Spieler: §e§l${value.player.name}",
                    "§7IP: §e§l${uuid.ip}",
                    "§7IP-Ban: ${if (!value.isBanned) no else "$yes${uuid.ip.reason}"}",
                    "§7Bans: §e§l${value.bans}",
                    "§7Zuletzt genutzt: §e§l${value.lastUseLong.formatTime}"
            )
        } else {
            if (!uuid.playerExists()) {
                sender.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
                return
            }
            sender.msg(header, "§7Spieler: §e§l${uuid.name}")
            if (sender.hasPermission("${permissionPrefix}check.ip")) sender.msg("§7IP: §e§l${uuid.ip}")
            sender.msg("§7IP-Ban: ${if (!uuid.ip.isBanned) no else "$yes${uuid.ip.reason}"}",
                    "§7Bans: §e§l${uuid.bans}",
                    "§7Mutes: §e§l${uuid.mutes}",
                    "§7Gebannt: ${if (!uuid.isBanned) no else "$yes${uuid.reasonString}"}",
                    "§7Gemutet: ${if (!uuid.isMuted) no else "$yes${uuid.reasonString}"}",
                    "§7Letzter Login: §e§l${uuid.lastLogin.toLong().formatTime}",
                    "§7Erster Login: §e§l${uuid.firstLogin.toLong().formatTime}"
            )
        }
        sender.msg(header)
    }
}