package de.tutorialwork.utils

import com.google.gson.JsonObject
import de.tutorialwork.global.APIKey
import de.tutorialwork.global.mysql
import net.darkdevelopers.darkbedrock.darkness.general.functions.asString
import net.darkdevelopers.darkbedrock.darkness.general.functions.load
import java.util.*

val String.ipExists: Boolean
    get() {
        val rs = mysql.query("SELECT IP FROM ips WHERE IP='$this'")
        return rs?.next() == true
    }

fun getEnd(ip: String): String {
    val time = System.currentTimeMillis()
    val end = ip.rawEnd
    val millis = end - time
    return millis.formatTime()
}

fun String.insertIP(uuid: UUID) = if (!this.ipExists) {
    mysql.update("""INSERT INTO ips(IP, USED_BY, USED_AT, BANNED, REASON, END, TEAMUUID, BANS) VALUES ('${this}'
            |, '$uuid', '${System.currentTimeMillis()}', '0', 'null', 'null', 'null', '0')""".trimMargin())
} else this.updateIPInfos(uuid)

fun String.updateIPInfos(newUUID: UUID) {
    if (this.ipExists) mysql.update("""UPDATE ips SET USED_BY = '$newUUID', USED_AT='${System.currentTimeMillis()}' WHERE IP='${this}'""")
}

fun String.ban(reasonID: Int, teamUUID: String) {
    val current = System.currentTimeMillis()
    val end = current + reasonID.reasonTime * 60000L
    //1. Perma Ban
    //2. Temp Ban
    if (reasonID.reasonTime == -1) mysql.update("""UPDATE ips SET BANNED='1', REASON='${reasonID.reason}', END='-1', TEAMUUID='$teamUUID' WHERE IP='${this}'""")
    else mysql.update("""UPDATE ips SET BANNED='1', REASON='${reasonID.reason}', END='$end', TEAMUUID='$teamUUID' WHERE IP='${this}'""")
}

val String.isBanned: Boolean
    get() {
        val rs = mysql.query("SELECT BANNED FROM ips WHERE IP='$this'")
        return if (rs?.next() == true) rs.getInt("BANNED") == 1 else false
    }

val String.reason: String
    get() {
        val rs = mysql.query("SELECT REASON FROM ips WHERE IP='$this'")
        return if (rs?.next() == true) rs.getString("REASON") else "none"
    }

val String.rawEnd: Long
    get() {
        val rs = mysql.query("SELECT END FROM ips WHERE IP='$this'")
        return if (rs?.next() == true) rs.getLong("END") else 0
    }

fun String.unBan() {
    if (this.ipExists) mysql.update("UPDATE ips SET BANNED='0' WHERE IP='${this}'")
}


val UUID.ip: String
    get() {
        val rs = mysql.query("SELECT IP FROM ips WHERE USED_BY='$this'")
        return if (rs?.next() == true) rs.getString("IP") else "none"
    }

val String.player: UUID
    get() {
        val rs = mysql.query("SELECT USED_BY FROM ips WHERE IP='$this'")
        return if (rs?.next() == true) UUID.fromString(rs.getString("USED_BY")) else UUID.fromString("")
    }

var String.bans: Int
    get() {
        val rs = mysql.query("SELECT BANS FROM ips WHERE IP='$this'")
        return if (rs?.next() == true) rs.getInt("BANS") else 0
    }
    set(value) {
        if (this.ipExists) {
            mysql.update("UPDATE ips SET BANS='$value' WHERE IP='$this'")
        }
    }

val String.lastUseLong: Long
    get() {
        val rs = mysql.query("SELECT USED_AT FROM ips WHERE IP='$this'")
        return if (rs?.next() == true) rs.getString("USED_AT").toLong() else 0
    }

val String.isVpn: Boolean
    get() {
        return if (this != "127.0.0.1") {
            val json = if (APIKey != null)
                callURL("""http://proxycheck.io/v2/$this?key=$APIKey""")
            else callURL("http://proxycheck.io/v2/$this?key=318n07-0o7054-y9y82a-75o3hr")

            val jsonObject = json.load<JsonObject>()
            val jsonElement = jsonObject[this] as? JsonObject ?: return false
            jsonElement["proxy"].asString() == "yes"
        } else false
    }