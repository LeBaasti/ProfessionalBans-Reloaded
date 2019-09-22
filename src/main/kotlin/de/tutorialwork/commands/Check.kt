package de.tutorialwork.commands

import de.tutorialwork.noPerms
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Command
import java.lang.Long

class Check(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender.hasPermission("professionalbans.check")) {
            if (args.isEmpty()) sender.msg("$prefix/check <Spieler/IP>")
            else {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                if (IPBan.validate(args[0])) {
                    val ip = args[0]
                    if (ip.ipExists) {
                        sender.msg("§8[]===================================[]")
                        sender.msg("§7Spieler: §e§l${ip.player.name}")
                        sender.msg("§7IP: §e§l${uuid.ip}")
                        sender.msg("§7IP-Ban: ${if (!ip.isBanned) "§a§lNein" else "§c§lJa §8/ ${uuid.ip.reason}"}")
                        sender.msg("§7Bans: §e§l${ip.bans}")
                        sender.msg("§7Zuletzt genutzt: §e§l${ip.lastUseLong.formatTime}")
                        sender.msg("§8[]===================================[]")
                    } else {
                        sender.msg("$prefix§cZu dieser IP-Adresse sind keine Informationen verfügbar")
                    }
                } else {
                    if (uuid.playerExists()) {
                        sender.msg("§8[]===================================[]")
                        sender.msg("§7Spieler: §e§l${uuid.name}")
                        if (sender.hasPermission("professionalbans.check.ip")) sender.msg("§7IP: §e§l${uuid.ip}")
                        sender.msg("§7Gebannt: ${if (!uuid.isBanned) "§a§lNein" else "§c§lJa §8/ ${uuid.reasonString}"}")
                        sender.msg("§7Gemutet: ${if (!uuid.isMuted) "§a§lNein" else "§c§lJa §8/ ${uuid.reasonString}"}")
                        sender.msg("§7IP-Ban: ${if (!uuid.ip.isBanned) "§a§lNein" else "§c§lJa §8/ ${uuid.ip.reason}"}")
                        sender.msg("§7Bans: §e§l${uuid.bans}")
                        sender.msg("§7Mutes: §e§l${uuid.mutes}")
                        sender.msg("§7Letzter Login: §e§l${Long.valueOf(uuid.lastLogin).formatTime}")
                        sender.msg("§7Erster Login: §e§l${Long.valueOf(uuid.firstLogin).formatTime}")
                        sender.msg("§8[]===================================[]")
                    } else {
                        sender.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
                    }
                }
            }
        } else sender.msg(noPerms)
    }
}