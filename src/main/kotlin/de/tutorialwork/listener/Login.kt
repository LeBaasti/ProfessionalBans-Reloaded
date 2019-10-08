package de.tutorialwork.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.connection.PreLoginEvent
import de.tutorialwork.configs.config
import de.tutorialwork.global.*
import de.tutorialwork.utils.*
import net.kyori.text.TextComponent

class Login {

    @Subscribe
    fun onPreLoginEvent(event: PreLoginEvent) {
        val ip = event.connection.virtualHost.get().hostName
        val uuid = UUIDFetcher.getUUID(ip) ?: return
        /*val ip = event.connection.address.hostString*/
        uuid.updatePlayer(ip)
        ip.insertIP(uuid)
        if (config.vpnBlocked && ip !in ipWhiteList && ip.isVpn) {
            if (config.vpnBan) {
                val id = config.vpnBanID
                uuid.ban(id.reason, consoleName)
                ActionType.IpBan(id).sendNotify(event.connection.virtualHost.get().hostString, consoleName)
                if (uuid.rawEnd == -1L) {
                    event.result = PreLoginEvent.PreLoginComponentResult.denied(TextComponent.of(config.layoutIpBan.replace("%grund%", id.reason).translateColors()))
                } else {
                    var msg = config.layoutTempIpBan
                    msg = msg.replace("%grund%", uuid.reasonString)
                    msg = msg.replace("%dauer%", uuid.endTime)
                    event.result = PreLoginEvent.PreLoginComponentResult.denied(TextComponent.of(msg.translateColors()))
                }
            } else if (config.vpnKick) {
                event.result = PreLoginEvent.PreLoginComponentResult.denied(TextComponent.of(config.vpnKickMessage.translateColors()))
            }
        }

        if (ip.isBanned) {

            if (ip.rawEnd == -1L) {
                event.result = PreLoginEvent.PreLoginComponentResult.denied(TextComponent.of(config.layoutIpBan.translateColors().replace("%grund%", ip.reason)))
            } else {
                if (System.currentTimeMillis() < ip.rawEnd) {
                    var msg = config.layoutTempIpBan
                    msg = msg.replace("%grund%", ip.reason)
                    msg = msg.replace("%dauer%", getEnd(ip))
                    event.result = PreLoginEvent.PreLoginComponentResult.denied(TextComponent.of(msg.translateColors()))
                } else {
                    ip.unBan()
                }
            }

        }
        if (uuid.isBanned) {
            if (uuid.rawEnd == -1L) {
                event.result = PreLoginEvent.PreLoginComponentResult.denied(TextComponent.of(config.layoutBan.replace("%grund%", uuid.reasonString).translateColors()))
            } else {
                if (System.currentTimeMillis() < uuid.rawEnd) {
                    var msg = config.layoutTempBan
                    msg = msg.replace("%grund%", uuid.reasonString)
                    msg = msg.replace("%dauer%", uuid.endTime)
                    event.result = PreLoginEvent.PreLoginComponentResult.denied(TextComponent.of(msg.translateColors()))
                } else {
                    uuid.unBan()
                }
            }
        }
    }

    @Subscribe
    fun onPostLoginEvent(event: PostLoginEvent) {
        val player = event.player
        if (player.hasPermission("${permissionPrefix}reports")) {
            val countOpenReports = countOpenReports()
            if (countOpenReports != 0) player.msg("${prefix}Derzeit sind noch §l$countOpenReports Reports §7offen")
        }
        if (player.hasPermission("${permissionPrefix}supportchat"))
            if (openChats.isNotEmpty()) player.msg("${prefix}Derzeit sind noch §l${openChats.size} §7Support Chat Anfragen §aoffen")
    }

}