package de.tutorialwork.utils

import de.tutorialwork.prefix
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import java.util.*


fun UUID.exists(sender: CommandSender): Unit? {
    val exists = BanManager.playerExists(this)
    return if (exists) sender.msg("$prefix§cDieser Spieler wurde nicht gefunden")
    else null
}

fun String.getUUID(sender: CommandSender): UUID? {
    val uuid = UUIDFetcher.getUUID(this)
    return if (uuid != null) uuid else {
        sender.msg("$prefix§cDieser Spieler wurde nicht gefunden")
        null
    }
}

fun CommandSender.msg(string: String) = sendMessage(TextComponent(string))

fun String.translateColors(): String = ChatColor.translateAlternateColorCodes('&', this)