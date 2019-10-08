package de.tutorialwork.utils

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.configs.config
import de.tutorialwork.global.instance
import de.tutorialwork.global.noPerms
import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.prefix
import net.kyori.text.TextComponent
import java.net.URL
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/*fun saveConfig(configuration: Configuration = config, file: File = configFile) = configProvider.save(configuration, file)*/

fun Int.randomString(): String {
    val alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    val rnd = SecureRandom()
    val sb = StringBuilder(this)
    for (i in 0 until this)
        sb.append(alphabet[rnd.nextInt(alphabet.length)])
    return sb.toString()
}

fun Player.connect(serverName: String) {
    val toConnect = instance.server.getServer(serverName)
    if (!toConnect.isPresent) {
        sendMessage(TextComponent.of("$prefix §cDieser Server existiert nicht"))
        return
    }
    createConnectionRequest(toConnect.get()).fireAndForget()
}

fun String.reasonTranslate(player: Player) = this.replace("%grund%", player.uniqueId.reasonString)
        .replace("%dauer%", player.uniqueId.endTime)
        .translateColors()

fun Player.sendTempBan() = kick(config.layoutTempBan.reasonTranslate(this))
fun Player.sendTempMute() = msg(config.layoutTempMute.reasonTranslate(this))


fun Player.sendBan() {
    if (uniqueId.rawEnd == -1L)
        kick(config.layoutBan.reasonTranslate(this))
    else sendTempBan()
}

fun Player.sendMute() = if (uniqueId.rawEnd == -1L)
    msg(config.layoutMute.reasonTranslate(this))
else sendTempMute()

fun UUID.exists(sender: CommandSource): Unit? {
    val exists = this.playerExists()
    return if (exists) sender.msg("${prefix}§cDieser Spieler wurde nicht gefunden")
    else null
}

fun String.getUUID(sender: CommandSource): UUID? {
    val uuid = UUIDFetcher.getUUID(this)
    return if (uuid != null) uuid else {
        sender.msg("${prefix}§cDieser Spieler wurde nicht gefunden")
        null
    }
}

inline fun CommandSource.hasPermission(permission: String, code: () -> Unit) =
        if (hasPermission("$permissionPrefix.$permission")) code() else msg(noPerms)

fun CommandSource.msg(vararg messages: String) = sendMessage(TextComponent.of(messages.map { TextComponent.of(it) }.toTypedArray().toString()))

fun Player.kick(string: String) = this.disconnect(TextComponent.of(string))

fun String.translateColors(): String = replace("&", "§")

fun Long.formatTime(): String {

    val important = "§8"
    val text = "§7"

    val time = abs(this) / 1000
    val sec = time % 60
    val min = time / 60 % 60
    val hour = time / 3600 % 24
    val day = time / 86400
    var remainingTime = ""
    if (day == 1L) remainingTime += "${important}ein$text Tag "
    else if (day != 0L) remainingTime += "$important$day$text Tage "
    if (hour == 1L) remainingTime += "${important}eine$text Stunde "
    else if (hour != 0L) remainingTime += "$important$hour$text Stunden "
    if (min == 1L) remainingTime += "${important}eine$text Minute "
    else if (min != 0L) remainingTime += "$important$min$text Minuten "
    if (sec == 1L) remainingTime += "$important$sec$text Sekunde "
    else if (time != 0L) remainingTime += "$important$sec$text Sekunden "
    return if (remainingTime.isBlank()) "${important}0 ${text}Sekunden" else remainingTime.dropLast(1)
}

fun callURL(myURL: String): String {
    val url = URL(myURL)
    val urlConn = url.openConnection() ?: return ""
    urlConn.readTimeout = TimeUnit.MINUTES.toMillis(1).toInt()
    val inputStream = urlConn.getInputStream()
    return inputStream.reader().readText()
}