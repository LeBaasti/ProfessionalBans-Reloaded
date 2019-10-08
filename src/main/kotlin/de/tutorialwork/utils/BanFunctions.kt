package de.tutorialwork.utils

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.global.*
import java.text.SimpleDateFormat
import java.util.*

private const val none = "none"
private const val never = "never"

val iDsFromOpenReports: List<Int>
    get() {
        val rs = mysql.query("SELECT ID FROM reports WHERE STATUS = 0")
        val ids = ArrayList<Int>()
        while (rs?.next() == true) ids.add(rs.getInt("ID"))
        return ids
    }

val UUID.endTime: String
    get() {
        val time = System.currentTimeMillis()
        val end = rawEnd
        val millis = end - time
        return millis.formatTime()
    }

val UUID.rawEnd: Long
    get() {
        val rs = mysql.query("SELECT END FROM bans WHERE uuid='$this'")
        return if (rs?.next() == true) rs.getLong("END") else 0
    }

val UUID.isBanned: Boolean
    get() {
        val rs = mysql.query("SELECT BANNED FROM bans WHERE UUID='$this'")
        return if (rs?.next() == true) rs.getInt("BANNED") == 1 else false
    }

val UUID.isMuted: Boolean
    get() {
        val rs = mysql.query("SELECT MUTED FROM bans WHERE uuid='$this'")
        return if (rs?.next() == true) rs.getInt("MUTED") == 1 else false
    }

var UUID.bans: Int
    get() {
        val rs = mysql.query("SELECT BANS FROM bans WHERE uuid='$this'")
        return if (rs?.next() == true) rs.getInt("BANS") else 0
    }
    private set(value) {
        if (playerExists()) mysql.update("UPDATE bans SET BANS='$value' WHERE uuid='$this'")
    }

var UUID.mutes: Int
    get() {
        val rs = mysql.query("SELECT MUTES FROM bans WHERE uuid='$this'")
        return if (rs?.next() == true) rs.getInt("MUTES") else 0
    }
    private set(value) {
        if (playerExists()) mysql.update("UPDATE bans SET MUTES='$value' WHERE uuid='$this'")
    }

val UUID.webaccountExists: Boolean
    get() {
        val rs = mysql.query("SELECT UUID FROM accounts WHERE UUID='$this'")
        return rs?.next() == true
    }

val UUID.isWebaccountAdmin: Boolean
    get() {
        val rs = mysql.query("SELECT RANK FROM accounts WHERE UUID='$this'")
        return if (rs?.next() == true) rs.getInt("RANK") == 3 else false
    }

val UUID.hasAuthToken: Boolean
    get() {
        val rs = mysql.query("SELECT AUTHCODE FROM accounts WHERE UUID='$this'")
        return rs?.next() == true
    }

val UUID.authCode: String
    get() {
        val rs = mysql.query("SELECT AUTHCODE FROM accounts WHERE UUID='$this'")
        return if (rs?.next() == true) rs.getString("AUTHCODE") else none
    }

val UUID.name: String
    get() {
        val rs = mysql.query("SELECT NAME FROM bans WHERE uuid='$this'")
        return if (rs?.next() == true) rs.getString("NAME") else none
    }

val UUID.firstLogin: String
    get() {
        val rs = mysql.query("SELECT FIRSTLOGIN FROM bans WHERE UUID='$this'")
        return if (rs?.next() == true) rs.getString("FIRSTLOGIN") else never
    }

val UUID.lastLogin: String
    get() {
        val rs = mysql.query("SELECT LASTLOGIN FROM bans WHERE UUID='$this'")
        return if (rs?.next() == true) rs.getString("LASTLOGIN") else never
    }

val UUID.reasonString: String
    get() {
        val rs = mysql.query("SELECT REASON FROM bans WHERE uuid='$this'")
        return if (rs?.next() == true) rs.getString("REASON") else none
    }

val Long.formatTime: String
    get() = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date(this))

val String.reasonTime: Int
    get() {
        val rs = mysql.query("SELECT TIME FROM reasons WHERE REASON='$this'")
        return if (rs?.next() == true) rs.getInt("TIME") else 0
    }

val Int.reasonTime: Int
    get() = reason.reasonTime

val Int.isBanReason: Boolean
    get() {
        val rs = mysql.query("SELECT TYPE FROM reasons WHERE ID='$this'")
        return if (rs?.next() == true) rs.getInt("TYPE") == 0 else false
    }

var Int.reasonBans: Int
    get() {
        val rs = mysql.query("SELECT BANS FROM reasons WHERE ID='$this'")
        return if (rs?.next() == true) rs.getInt("BANS") else 0
    }
    set(value) = mysql.update("UPDATE reasons SET BANS='$value' WHERE ID='$this'")

val Int.extraPermissions: String
    get() {
        val rs = mysql.query("SELECT PERMS FROM reasons WHERE ID='$this'")
        return if (rs?.next() == true) rs.getString("PERMS") else none
    }

val Int.hasExtraPermissions: Boolean
    get() {
        val rs = mysql.query("SELECT PERMS FROM reasons WHERE ID='$this'")
        return rs?.next() == true
    }

val Int.reason: String
    get() {
        val rs = mysql.query("SELECT REASON FROM reasons WHERE ID='$this'")
        return if (rs?.next() == true) rs.getString("REASON") else none
    }

val Int.reportName: String
    get() {
        val rs = mysql.query("SELECT UUID FROM reports WHERE ID='$this'")
        return if (rs?.next() == true) UUID.fromString(rs.getString("UUID")).name else none
    }

val Int.reportReason: String
    get() {
        val rs = mysql.query("SELECT REASON FROM reports WHERE ID='$this'")
        return if (rs?.next() == true) rs.getString("REASON") else none
    }

private val UUID.updateLastLogin get() = mysql.update("""UPDATE bans SET LASTLOGIN = '${System.currentTimeMillis()}' WHERE uuid = '$this'""")

fun Int.setReportDone() = mysql.update("UPDATE reports SET STATUS = 1 WHERE ID = $this")
fun UUID.updateAuthStatus() = mysql.update("UPDATE accounts SET AUTHSTATUS = 1 WHERE UUID = '$this'")
fun UUID.reportTeam(value: Int) = mysql.update("UPDATE reports SET TEAM = '$this' WHERE ID = $value")

fun UUID.createReport(ReporterUUID: String, Reason: String, LogID: String?) {
    mysql.update("""INSERT INTO reports(uuid, REPORTER, TEAM, REASON, LOG, STATUS, CREATED_AT) VALUES ('$this', '$ReporterUUID', 'null'
            |, '$Reason', '$LogID', '0', '${System.currentTimeMillis()}')""".trimMargin())
}

fun UUID.unMute() {
    if (playerExists()) mysql.update("UPDATE bans SET MUTED='0' WHERE uuid='$this'")
}

fun UUID.unBan() {
    if (playerExists()) mysql.update("UPDATE bans SET BANNED='0' WHERE uuid='${this}'")
}

fun CommandSource.sendBanReasonsList() {
    val rs = mysql.query("SELECT ID FROM reasons ORDER BY SORTINDEX ASC")
    while (rs?.next() == true) {
        val id = rs.getInt("ID")
        if (hasPermission(id.extraPermissions))
            msg("§7$id §8| §e${id.reason}" + if (id.isBanReason) "" else " §8(§cMUTE§8)")
    }
}

fun UUID.playerExists(): Boolean {
    val rs = mysql.query("SELECT uuid FROM bans WHERE uuid='${this}'")
    return if (rs?.next() == true) rs.getString("uuid") != null else false
}

fun UUID.updatePlayer(name: String) {
    if (!playerExists()) {
        mysql.update("""INSERT INTO bans(uuid, NAME, BANNED, MUTED, REASON, END, TEAMUUID, BANS, MUTES, FIRSTLOGIN, LASTLOGIN) VALUES 
                |('${this}', '$name', '0', '0', 'null', 'null', 'null', '0', '0', '${System.currentTimeMillis()}', '${System.currentTimeMillis()}')""".trimMargin())
    } else {
        updateName(name)
        updateLastLogin
    }
}

private fun UUID.updateName(newName: String) {
    if (playerExists()) mysql.update("UPDATE bans SET NAME='$newName' WHERE uuid='${this}'")
}

fun UUID.ban(reason: String, teamUUID: String, percentage: Int = increaseValue, increaseBans1: Boolean = increaseBans) {
    val reasonTime = reason.reasonTime
    val end = if (reasonTime == -1) -1 else {
        //Temp Ban
        //Formel: 1.50 * Anzahl an Tagen = Ergebniss (50%)
        val bans = bans
        val current = System.currentTimeMillis()
        val endTime = current + reasonTime * 60000L
        val increaseEnd = current + (percentage / 100).toLong() + 1 * reasonTime.toLong() * bans.toLong() * 60000L //Formel!!!!!
        if (increaseBans1 && bans <= 0) increaseEnd else endTime
    }
    mysql.update("""UPDATE bans SET BANNED='1', REASON='${reason}', END='$end', TEAMUUID='$teamUUID' WHERE uuid='${this}'""")
    bans++
}

fun UUID.mute(reason: String, teamUUID: String) {
    val current = System.currentTimeMillis()
    val reasonTime = reason.reasonTime
    val end = if (reasonTime == -1) -1 else current + reasonTime * 60000L
    mysql.update("""UPDATE bans SET MUTED='1', REASON='${reason}', END='$end', TEAMUUID='$teamUUID' WHERE UUID='$this'""")
    mutes++
}


fun UUID.createWebAccount(rank: RankType, password: String, sender: Player) {
    mysql.update("""INSERT INTO accounts(UUID, USERNAME, PASSWORD, RANK, GOOGLE_AUTH, AUTHCODE) VALUES ('$this', '$name'
        |, '${BCrypt.hashpw(password, BCrypt.gensalt())}', '${rank.id}', 'null', 'initialpassword')""".trimMargin())
    this.createLogEntry(sender.uniqueId.toString(), ActionType.WebAccount("ADD_WEBACCOUNT"))
}

fun UUID.deleteWebAccount() = mysql.update("DELETE FROM accounts WHERE UUID='$this'")

fun ActionType.sendNotify(bannedName: String, senderName: String) {
    val message = prefix + message
            .replace("%banned-name%", bannedName)
            .replace("%sender-name%", senderName)
    for (all in players)
        if (all.hasPermission("${permissionPrefix}notify"))
            all.msg(message)
}

fun countOpenReports(): Int {
    var i = 0
    val rs = mysql.query("SELECT * FROM reports WHERE STATUS = 0")
    while (rs?.next() == true) i++
    return i
}

fun countReasons(): Int {
    var i = 0
    val rs = mysql.query("SELECT * FROM reasons")
    while (rs?.next() == true) i++
    return i
}