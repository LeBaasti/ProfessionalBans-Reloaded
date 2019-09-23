package de.tutorialwork.commands

import de.tutorialwork.global.config
import de.tutorialwork.global.players
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

object IPBan : Command(IPBan::class.java.simpleName) {

    override fun execute(sender: CommandSender, args: Array<String>) = sender.hasPermission(name.toLowerCase()) {
        if (sender is ProxiedPlayer) {
            if (args.isEmpty() || args.size == 1) {
                for (zaehler in 1 until countReasons() + 1) {
                    if (zaehler.isBanReason) {
                        sender.sendBanReasonsList()
                        sender.msg("§7$zaehler §8| §e${zaehler.reason}")
                    }
                }
                sender.msg("$prefix/ipban <IP/Spieler> <Grund-ID>")
            } else {
                val ip = args[0]
                val id = Integer.valueOf(args[1])
                if (validate(ip)) {
                    if (ip.ipExists) {
                        ip.ban(id, sender.uniqueId.toString())
                        ip.bans += 1
                        ActionType.IpBan(id).sendNotify(ip, sender.name)
                    } else {
                        ip.insertIP(sender.uniqueId)
                        ip.ban(id, sender.uniqueId.toString())
                        ip.bans += 1
                        ActionType.IpBan(id).sendNotify(ip, sender.name)
                    }
                    disconnectIPBannedPlayers(ip)
                    LogManager.createEntry("", sender.uniqueId.toString(), ActionType.IpBan("IPBAN_IP"))
                } else {
                    val uuid = UUIDFetcher.getUUID(args[0]) ?: return
                    val dbip = uuid.ip
                    dbip.ban(id, sender.uniqueId.toString())
                    ip.bans += 1
                    ActionType.IpBan(id).sendNotify(dbip, sender.name)
                    disconnectIPBannedPlayers(dbip)
                    LogManager.createEntry("", sender.uniqueId.toString(), ActionType.IpBan("IPBAN_PLAYER"))
                }
            }
        }
    }

    private fun disconnectIPBannedPlayers(ip: String) {
        for (all in players) {
            if (all.address.hostString == ip) {
                if (ip.rawEnd == -1L) {
                    all.kick(config.getString("LAYOUT.IPBAN").replace("%grund%", ip.reason).translateColors())
                } else {
                    if (System.currentTimeMillis() < ip.rawEnd) {
                        var msg = config.getString("LAYOUT.TEMPIPBAN")
                        msg = msg.replace("%grund%", ip.reason)
                        msg = msg.replace("%dauer%", getEnd(ip))
                        all.kick(msg.translateColors())
                    } else ip.unBan()
                }
            }
        }
    }

    private val PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$".toPattern()
    fun validate(ip: String): Boolean = PATTERN.matcher(ip).matches()

}