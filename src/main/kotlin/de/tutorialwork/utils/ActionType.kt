package de.tutorialwork.utils

private const val bannedName = "%banned-name%"
private const val senderName = "%sender-name%"

@Suppress("CanBeParameter")
sealed class ActionType {

    abstract val message: String

    class Ban(private val reason: String) : ActionType() {

        constructor(id: Int) : this(id.reason)

        override val message: String = "§e§l$bannedName §7wurde von §c§l$senderName §cgebannt §7wegen §a$reason"
    }

    class IpBan(private val reason: String) : ActionType() {

        constructor(id: Int) : this(id.reason)

        override val message: String = "§7Die IP §e§l$bannedName §7wurde von §c§l$senderName §cgebannt §7wegen §a$reason"
    }

    class Mute(private val reason: String) : ActionType() {

        constructor(id: Int) : this(id.reason)

        override val message: String = "§e§l$bannedName §7wurde von §c§l$senderName §cgemutet §7wegen §a$reason"
    }

    class Kick(private val reason: String) : ActionType() {

        constructor(id: Int) : this(id.reason)

        override val message: String = "§e§l$bannedName §7wurde von §c§l$senderName §cgekickt §7wegen §a$reason"
    }

    object UnBan : ActionType() {
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §aentbannt"
    }

    object UnBanIp : ActionType() {
        override val message: String = "§c§l$senderName §7hat die IP-Adresse §e§l$bannedName §aentbannt"
    }

    object UnMute : ActionType() {
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §aentmutet"
    }

    class Report(private val reason: String) : ActionType() {
        override val message: String = "§c§l$senderName §7hat §e§l$bannedName §7wegen §a$reason §7gemeldet"
    }

}