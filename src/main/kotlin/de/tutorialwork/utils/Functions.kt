package de.tutorialwork.utils

import de.tutorialwork.config
import de.tutorialwork.configFile
import de.tutorialwork.configProvider
import de.tutorialwork.prefix
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.config.Configuration
import java.io.File
import java.net.URL
import java.net.URLConnection
import java.security.SecureRandom
import java.util.*

fun saveConfig(configuration: Configuration = config, file: File = configFile) = configProvider.save(configuration, file)


fun Int.randomString(): String {
    val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    val rnd = SecureRandom()
    val sb = StringBuilder(this)
    for (i in 0 until this)
        sb.append(alphabet[rnd.nextInt(alphabet.length)])
    return sb.toString()
}

fun ProxiedPlayer.sendTempban() {
    var msg = config.getString("LAYOUT.TEMPBAN")
    msg = msg.replace("%grund%", uniqueId.reasonString)
    msg = msg.replace("%dauer%", uniqueId.endTime)
    kick(msg.translateColors())
}


fun ProxiedPlayer.sendTempmute() {
    var msg = config.getString("LAYOUT.TEMPMUTE")
    msg = msg.replace("%grund%", uniqueId.reasonString)
    msg = msg.replace("%dauer%", uniqueId.endTime)
    msg(msg.translateColors())
}

fun UUID.exists(sender: CommandSender): Unit? {
    val exists = this.playerExists()
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

fun ProxiedPlayer.kick(string: String) = this.disconnect(TextComponent(string))

fun String.translateColors(): String = ChatColor.translateAlternateColorCodes('&', this)

fun Long.formatTime(): String {

    val IMPORTANT = ChatColor.DARK_GRAY
    val TEXT = ChatColor.GRAY

    val time = Math.abs(this)
    val sec = time % 60
    val min = time / 60 % 60
    val hour = time / 3600 % 24
    val day = time / 86400
    var remainingTime = ""
    if (day == 1L) remainingTime += "${IMPORTANT}ein$TEXT Tag "
    else if (day != 0L) remainingTime += "$IMPORTANT$day$TEXT Tage "
    if (hour == 1L) remainingTime += "${IMPORTANT}eine$TEXT Stunde "
    else if (hour != 0L) remainingTime += "$IMPORTANT$hour$TEXT Stunden "
    if (min == 1L) remainingTime += "${IMPORTANT}eine$TEXT Minute "
    else if (min != 0L) remainingTime += "$IMPORTANT$min$TEXT Minuten "
    if (sec == 1L) remainingTime += "$IMPORTANT$sec$TEXT Sekunde "
    else if (time != 0L) remainingTime += "$IMPORTANT$sec$TEXT Sekunden "
    return if (remainingTime.isBlank()) "${IMPORTANT}0 ${TEXT}Sekunden" else remainingTime.dropLast(1)
}

fun callURL(myURL: String): String {
    val url = URL(myURL)
    val urlConn: URLConnection? = url.openConnection()
    if (urlConn != null) urlConn.readTimeout = 60 * 1000
    val inputStream = urlConn?.getInputStream() ?: return ""
    return inputStream.reader().readText()
}