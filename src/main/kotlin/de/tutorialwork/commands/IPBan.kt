package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.configs.config
import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.players
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*

class IPBan : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (source is Player) {
            if (args.isEmpty() || args.size == 1) {
                for (zaehler in 1 until countReasons() + 1) {
                    if (zaehler.isBanReason) {
                        source.sendBanReasonsList()
                        source.msg("§7$zaehler §8| §e${zaehler.reason}")
                    }
                }
                source.msg("$prefix/ipban <IP/Spieler> <Grund-ID>")
            } else {
                val ip = args[0]
                val id = Integer.valueOf(args[1])
                if (validate(ip)) {
                    if (ip.ipExists) {
                        ip.ban(id, source.uniqueId.toString())
                        ip.bans += 1
                        ActionType.IpBan(id).sendNotify(ip, source.username)
                    } else {
                        ip.insertIP(source.uniqueId)
                        ip.ban(id, source.uniqueId.toString())
                        ip.bans += 1
                        ActionType.IpBan(id).sendNotify(ip, source.username)
                    }
                    disconnectIPBannedPlayers(ip)
                    null.createLogEntry(source.uniqueId.toString(), ActionType.IpBan("IPBAN_IP"))
                } else {
                    val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                    val dbip = uuid.ip
                    dbip.ban(id, source.uniqueId.toString())
                    ip.bans += 1
                    ActionType.IpBan(id).sendNotify(dbip, source.username)
                    disconnectIPBannedPlayers(dbip)
                    null.createLogEntry(source.uniqueId.toString(), ActionType.IpBan("IPBAN_PLAYER"))
                }
            }
        }
    }

    override fun hasPermission(source: CommandSource, args: Array<out String>): Boolean = source.hasPermission("$permissionPrefix.commands.ipban")


    private fun disconnectIPBannedPlayers(ip: String) {
        for (all in players) {
            if (all.remoteAddress.hostString == ip) {
                if (ip.rawEnd == -1L) {
                    all.kick(config.layoutIpBan.replace("%grund%", ip.reason).translateColors())
                } else {
                    if (System.currentTimeMillis() < ip.rawEnd) {
                        var msg = config.layoutTempIpBan
                        msg = msg.replace("%grund%", ip.reason)
                        msg = msg.replace("%dauer%", getEnd(ip))
                        all.kick(msg.translateColors())
                    } else ip.unBan()
                }
            }
        }
    }

    private val pattern = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$".toPattern()
    private fun validate(ip: String): Boolean = pattern.matcher(ip).matches()

}