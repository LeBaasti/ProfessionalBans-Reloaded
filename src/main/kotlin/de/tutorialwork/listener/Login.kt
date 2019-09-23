package de.tutorialwork.listener

import de.tutorialwork.global.*
import de.tutorialwork.utils.*
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

object Login : Listener {

    @EventHandler
    fun onPreLoginEvent(event: PreLoginEvent) {
        val uuid = UUIDFetcher.getUUID(event.connection.name) ?: return
        val ip = event.connection.virtualHost.hostName
        /*val ip = event.connection.address.hostString*/
        uuid.updatePlayer(event.connection.name)
        ip.insertIP(uuid)
        if (config.getBoolean("VPN.BLOCKED")) {
            if (!ipwhitelist.contains(ip)) {
                if (ip.isVpn) {
                    if (config.getBoolean("VPN.KICK")) {
                        event.isCancelled = true
                        event.cancelReason = config.getString("VPN.KICKMSG").translateColors()
                    }
                    if (config.getBoolean("VPN.BAN")) {
                        val id = config.getInt("VPN.BANID")
                        uuid.ban(id.reason, consoleName)
                        ActionType.IpBan(id).sendNotify(event.connection.address.hostString, consoleName)
                        event.isCancelled = true
                        if (uuid.rawEnd == -1L) {
                            event.cancelReason = config.getString("LAYOUT.IPBAN").replace("%grund%", id.reason).translateColors()
                        } else {
                            var msg = config.getString("LAYOUT.TEMPIPBAN")
                            msg = msg.replace("%grund%", uuid.reasonString)
                            msg = msg.replace("%dauer%", uuid.endTime)
                            event.cancelReason = msg.translateColors()
                        }
                    }
                }
            }
        }

        if (ip.isBanned) {

            if (ip.rawEnd == -1L) {
                event.isCancelled = true
                event.setCancelReason(TextComponent(config.getString("LAYOUT.IPBAN").translateColors().replace("%grund%", ip.reason)))

            } else {
                if (System.currentTimeMillis() < ip.rawEnd) {
                    event.isCancelled = true
                    var msg = config.getString("LAYOUT.TEMPIPBAN")
                    msg = msg.replace("%grund%", ip.reason)
                    msg = msg.replace("%dauer%", getEnd(ip))
                    event.cancelReason = msg.translateColors()
                } else {
                    ip.unBan()
                }
            }

        }
        if (uuid.isBanned) {
            if (uuid.rawEnd == -1L) {
                event.isCancelled = true
                event.cancelReason = config.getString("LAYOUT.BAN").replace("%grund%", uuid.reasonString).translateColors()
            } else {
                if (System.currentTimeMillis() < uuid.rawEnd) {
                    event.isCancelled = true
                    var msg = config.getString("LAYOUT.TEMPBAN")
                    msg = msg.replace("%grund%", uuid.reasonString)
                    msg = msg.replace("%dauer%", uuid.endTime)
                    event.cancelReason = msg.translateColors()
                } else {
                    uuid.unBan()
                }
            }
        }
    }

    @EventHandler
    fun onFinalLogin(e: PostLoginEvent) {
        val p = e.player
        if (p.hasPermission("${permissionPrefix}reports")) {
            if (countOpenReports() != 0) p.msg("${prefix}Derzeit sind noch §l${countOpenReports()} Reports §7offen")
        }
        if (p.hasPermission("${permissionPrefix}supportchat")) {
            if (openchats.isNotEmpty()) p.msg("${prefix}Derzeit sind noch §l${openchats.size} §7Support Chat Anfragen §aoffen")
        }
    }

}