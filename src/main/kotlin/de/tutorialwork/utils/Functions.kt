package de.tutorialwork.utils

import de.tutorialwork.main.Main
import net.md_5.bungee.api.CommandSender
import java.util.*


fun UUID.exists(sender: CommandSender): Unit? {
    val exists = BanManager.playerExists(this)
    return if (exists) sender.sendMessage(Main.prefix + "§cDieser Spieler wurde nicht gefunden")
    else null
}

fun String.getUUID(sender: CommandSender): UUID? {
    val uuid = UUIDFetcher.getUUID(this)
    return if (uuid != null) uuid else {
        sender.sendMessage(Main.prefix + "§cDieser Spieler wurde nicht gefunden")
        null
    }
}
