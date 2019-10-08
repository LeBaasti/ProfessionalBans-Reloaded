package de.tutorialwork.utils

import de.tutorialwork.global.proxyServer
import java.util.*

private const val bannedName = "%banned-name%"
private const val senderName = "%sender-name%"

@Suppress("CanBeParameter")
sealed class ActionType {

    abstract val reason: String
    abstract val message: String
    open val node: String? = null

    open fun execute(uuid: UUID, executor: String) {}

    class Ban(override val reason: String) : ActionType() {

        constructor(id: Int) : this(id.reason)

        override val message: String = "§e§l$bannedName §7wurde von §c§l$senderName §cgebannt §7wegen §a$reason"

        override fun execute(uuid: UUID, executor: String) {
            uuid.ban(reason, executor)
            val banned = proxyServer.getPlayer(uuid).get()
            banned.sendBan()
        }

    }

    class IpBan(override val reason: String) : ActionType() {

        constructor(id: Int) : this(id.reason)

        override val message: String = "§7Die IP §e§l$bannedName §7wurde von §c§l$senderName §cgebannt §7wegen §a$reason"
    }

    class Mute(override val reason: String) : ActionType() {

        constructor(id: Int) : this(id.reason)

        override val message: String = "§e§l$bannedName §7wurde von §c§l$senderName §cgemutet §7wegen §a$reason"

        override fun execute(uuid: UUID, executor: String) {
            uuid.mute(reason, executor)
            val banned = proxyServer.getPlayer(uuid).get()
            banned.sendMute()
        }

    }

    class Kick(override val reason: String) : ActionType() {
        override val message: String = "§e§l$bannedName §7wurde von §c§l$senderName §cgekickt §7wegen §a$reason"

    }

    object UnBan : ActionType() {
        override val reason: String = "UNBAN_BAN"
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §aentbannt"
    }

    object UnBanIp : ActionType() {
        override val reason: String = "UNBAN_IP"
        override val message: String = "§c§l$senderName §7hat die IP-Adresse §e§l$bannedName §aentbannt"
    }

    object UnMute : ActionType() {
        override val reason: String = "UNBAN_MUTE"
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §aentmutet"
    }

    class Report(override val reason: String) : ActionType() {
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §7wegen §a$reason §7gemeldet"
    }

    class Blacklist(override val reason: String) : ActionType() {
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §7wegen §a$reason §7gemeldet"
    }

    class Chatlog(override val reason: String, override val node: String?) : ActionType() {
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §7wegen §a$reason §7gemeldet"
    }

    class WebAccount(override val reason: String) : ActionType() {
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §7wegen §a$reason §7gemeldet"
    }

}