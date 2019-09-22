package de.tutorialwork.commands

import de.tutorialwork.*
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

object Ban : Command(Ban::class.simpleName) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.ban")) {
                if (args.size == 1) {
                    val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                    val id = args[1].toIntOrNull() ?: return //add fail message
                    if (uuid.playerExists()) {
                        if (uuid.isWebaccountAdmin) {
                            sender.msg("$prefix§cDiesen Spieler kannst du nicht bannen/muten")
                            return
                        }
                        if (id.isBanReason) {
                            if (id.hasExtraPermissions) {
                                if (!sender.hasPermission(id.extraPermissions)) {
                                    sender.msg("$prefix§cDu hast keine Berechtigung diesen Bangrund zu nutzen")
                                    return
                                }
                            }
                            uuid.ban(id, sender.uniqueId.toString(), increaseValue, increaseBans)
                            LogManager.createEntry("", sender.uniqueId.toString(), ActionType.Ban(id))
                            uuid.bans += 1
                            ActionType.Ban(id).sendNotify(uuid.name, sender.name)
                            val banned = instance.proxy.getPlayer(args[0])
                            if (banned.uniqueId.rawEnd == -1L) {
                                banned.kick(config.getString("LAYOUT.BAN").replace("%grund%", id.reason).translateColors())
                            } else {
                                var msg = config.getString("LAYOUT.TEMPBAN")
                                msg = msg.replace("%grund%", uuid.reasonString)
                                msg = msg.replace("%dauer%", uuid.endTime)
                                banned.kick(msg.translateColors())
                            }
                            saveConfig()

                        } else {
                            if (id.hasExtraPermissions) {
                                if (!sender.hasPermission(id.extraPermissions)) {
                                    sender.msg("$prefix§cDu hast keine Berechtigung diesen Mutegrund zu nutzen")
                                    return
                                }
                            }
                            uuid.mute(id, sender.uniqueId.toString())
                            LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), ActionType.Mute(id))
                            uuid.mutes += 1
                            ActionType.Mute(id).sendNotify(uuid.name, sender.name)
                            val banned = proxyServer.getPlayer(args[0])
                            if (banned != null) {
                                if (banned.uniqueId.rawEnd == -1L) {
                                    banned.msg(config.getString("LAYOUT.MUTE").replace("%grund%", id.reason).translateColors())
                                } else {
                                    var msg = config.getString("LAYOUT.TEMPMUTE")
                                    msg = msg.replace("%grund%", uuid.reasonString)
                                    msg = msg.replace("%dauer%", uuid.endTime)
                                    banned.msg(msg.translateColors())
                                }
                                saveConfig()
                            }
                        }
                    } else sender.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
                } else {
                    sender.sendBanReasonsList()
                    sender.msg("$prefix/ban <Spieler> <Grund-ID>")
                }
            } else {
                sender.msg(noPerms)
            }
        } else {
            if (args.isEmpty() || args.size == 1) {
                for (zaehler in 1 until countReasons() + 1) {
                    val string = "§7$zaehler §8| §e${zaehler.reason}" + if (zaehler.isBanReason) "" else " §8(§cMUTE§8)"
                    console.msg(string)
                }
                console.msg("$prefix/ban <Spieler> <Grund-ID>")
            } else {
                val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                val id = Integer.valueOf(args[1])
                if (uuid.playerExists()) {
                    if (uuid.isWebaccountAdmin) {
                        console.msg("$prefix§cDiesen Spieler kannst du nicht bannen/muten")
                        return
                    }
                    if (id.isBanReason) {
                        uuid.ban(id, consoleName, increaseValue, increaseBans)
                        LogManager.createEntry(uuid.toString(), consoleName, ActionType.Ban(id))
                        uuid.bans += 1
                        ActionType.Ban(id).sendNotify(uuid.name, consoleName)
                        val banned = proxyServer.getPlayer(uuid)
                        if (banned != null) {


                            if (banned.uniqueId.rawEnd == -1L) {
                                banned.kick(config.getString("LAYOUT.BAN")
                                        .replace("%grund%", id.reason).translateColors())
                            } else banned.sendTemp("BAN")
                        }
                    } else {
                        uuid.mute(id, consoleName)
                        LogManager.createEntry(uuid.toString(), consoleName, ActionType.Mute(id))
                        uuid.mutes += 1
                        ActionType.Mute(id).sendNotify(uuid.name, consoleName)
                        val banned = proxyServer.getPlayer(args[0])
                        if (banned != null) {
                            if (banned.uniqueId.rawEnd == -1L) {
                                banned.msg(config.getString("LAYOUT.MUTE")
                                        .replace("%grund%", id.reason).translateColors())
                            } else banned.sendTempmute()
                            saveConfig()

                        }
                    }
                } else {
                    console.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
                }
            }
        }
    }
}