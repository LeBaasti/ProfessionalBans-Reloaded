package de.tutorialwork.commands

import de.tutorialwork.*
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

object Ban : Command(Ban::class.simpleName) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        val executor = if (sender is ProxiedPlayer) sender.uniqueId.toString() else consoleName
        if (sender.hasPermission("professionalbans.ban")) {
            if (args.size == 2) {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                val id = args[1].toIntOrNull() ?: return //add fail message
                val banned = instance.proxy.getPlayer(uuid)
                if (uuid.playerExists()) {
                    if (uuid.isWebaccountAdmin) sender.msg("$prefix§cDiesen Spieler kannst du nicht bannen/muten")
                    else {
                        if (id.isBanReason) {
                            if (id.hasExtraPermissions) {
                                if (!sender.hasPermission(id.extraPermissions)) sender.msg("$prefix§cDu hast keine Berechtigung diesen Bangrund zu nutzen")
                            } else {
                                uuid.ban(id, executor, increaseValue, increaseBans)
                                LogManager.createEntry("", executor, ActionType.Ban(id))
                                uuid.bans += 1
                                ActionType.Ban(id).sendNotify(uuid.name, sender.name)
                                if (banned.uniqueId.rawEnd == -1L) {
                                    banned.kick(config.getString("LAYOUT.BAN").replace("%grund%", id.reason).translateColors())
                                } else banned.sendTempban()
                                saveConfig()
                            }
                        } else {
                            if (id.hasExtraPermissions) {
                                if (!sender.hasPermission(id.extraPermissions)) sender.msg("$prefix§cDu hast keine Berechtigung diesen Mutegrund zu nutzen")
                            } else {
                                uuid.mute(id, executor)
                                LogManager.createEntry(uuid.toString(), executor, ActionType.Mute(id))
                                uuid.mutes += 1
                                ActionType.Mute(id).sendNotify(uuid.name, sender.name)
                                if (banned != null) {
                                    if (banned.uniqueId.rawEnd == -1L) {
                                        banned.msg(config.getString("LAYOUT.MUTE").replace("%grund%", id.reason).translateColors())
                                    } else banned.sendTempmute()
                                    saveConfig()
                                }
                            }
                        }
                    }
                } else sender.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
            } else {
                sender.sendBanReasonsList()
                sender.msg("$prefix/ban <Spieler> <Grund-ID>")
            }
        } else sender.msg(noPerms)
    }
}