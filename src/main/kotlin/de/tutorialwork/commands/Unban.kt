package de.tutorialwork.commands

import de.tutorialwork.consoleName
import de.tutorialwork.noPerms
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import de.tutorialwork.utils.ActionType.UnBanIp
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Unban(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        val executor = if (sender is ProxiedPlayer) sender.uniqueId.toString() else consoleName
        if (sender.hasPermission("professionalbans.unban")) {
            if (args.isEmpty() || args.size >= 2) sender.msg("$prefix/unBan <Spieler/IP>")
            else {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                if (IPBan.validate(args[0])) {
                    args[0].unBan()
                    UnBanIp.sendNotify(args[0], sender.name)
                    sender.msg("$prefix§7Die IP-Adresse §e§l${args[0]} §7wurde §aerfolgreich §7entbannt")
                    LogManager.createEntry("", executor, UnBanIp)
                } else {
                    if (uuid.playerExists()) {
                        if (uuid.ip.isBanned) {
                            uuid.ip.unBan()
                            sender.msg("${prefix}Die IP §e§l${uuid.ip} §7war gebannt und wurde ebenfalls §aentbannt")
                        }
                        when {
                            uuid.isBanned -> {
                                uuid.unBan()
                                ActionType.UnBan.sendNotify(uuid.name, sender.name)
                                sender.msg("$prefix§e§l${uuid.name} §7wurde §aerfolgreich §7entbannt")
                                LogManager.createEntry(uuid.toString(), executor, ActionType.UnBan)
                            }
                            uuid.isMuted -> {
                                uuid.unMute()
                                ActionType.UnMute.sendNotify(uuid.name, sender.name)
                                sender.msg("$prefix§e§l${uuid.name} §7wurde §aerfolgreich §7entmutet")
                                LogManager.createEntry(uuid.toString(), executor, ActionType.UnMute)
                            }
                            else -> sender.msg("$prefix§e§l${uuid.name} §7ist weder gebannt oder gemutet")
                        }
                    } else sender.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
                }
            }
        } else sender.msg(noPerms)
    }
}