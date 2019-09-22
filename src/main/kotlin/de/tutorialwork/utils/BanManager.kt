package de.tutorialwork.utils

import de.tutorialwork.mysql
import de.tutorialwork.players
import de.tutorialwork.prefix
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

object BanManager {

    val iDsFromOpenReports: List<Int>
        get() {
            return try {
                val rs = mysql.query("SELECT * FROM reports WHERE STATUS = 0")
                val ids = ArrayList<Int>()
                while (rs!!.next()) {
                    ids.add(rs.getInt("ID"))
                }
                ids
            } catch (exc: SQLException) {
                emptyList()
            }
        }

    //DATENBANK Struktur
    //UUID varchar(64) UNIQUE, NAME varchar(64), BANNED int(11), MUTED int(11), REASON varchar(64), END long(255), TEAMUUID varchar(64), BANS int(11), MUTES int(11)

    fun playerExists(uuid: UUID): Boolean {
        val rs = mysql.query("SELECT uuid FROM bans WHERE uuid='$uuid'")
        return if (rs?.next() == true) rs.getString("uuid") != null else false
    }

    fun createPlayer(uuid: UUID, name: String) {
        if (!playerExists(uuid)) {
            mysql.update("INSERT INTO bans(uuid, NAME, BANNED, MUTED, REASON, END, TEAMUUID, BANS, MUTES, FIRSTLOGIN, LASTLOGIN) " +
                    "VALUES ('" + uuid + "', '" + name + "', '0', '0', 'null', 'null', 'null', '0', '0', '" + System.currentTimeMillis() + "', '" + System.currentTimeMillis() + "')")
        } else {
            updateName(uuid, name)
            updateLastLogin(uuid)
        }
    }

    fun getBanReasonsList(p: ProxiedPlayer) {
        val rs = mysql.query("SELECT * FROM reasons ORDER BY SORTINDEX ASC")
        while (rs!!.next()) {
            val id = rs.getInt("ID")
            if (isBanReason(id)) {
                p.sendMessage("§7" + id + " §8| §e" + id.reason)
            } else {
                p.sendMessage("§7" + id + " §8| §e" + id.reason + " §8(§cMUTE§8)")
            }
        }

    }

    private fun updateName(uuid: UUID, newName: String) {
        if (playerExists(uuid)) mysql.update("UPDATE bans SET NAME='$newName' WHERE uuid='$uuid'")
    }

    fun ban(uuid: UUID, GrundID: Int, TeamUUID: String, Prozentsatz: Int, increaseBans: Boolean) {
        if (getReasonTime(GrundID) == -1) {
            //Perma Ban
            mysql.update("UPDATE bans SET BANNED='1', REASON='" + GrundID.reason + "', END='-1', TEAMUUID='" + TeamUUID + "' WHERE uuid='" + uuid + "'")
        } else {
            //Temp Ban
            //Formel: 1.50 * Anzahl an Tagen = Ergebniss (50%)
            val bans = getBans(uuid)
            val defaultmins = getReasonTime(GrundID)!!
            val current = System.currentTimeMillis()
            val end = current + getReasonTime(GrundID)!! * 60000L
            val increaseEnd = current + (Prozentsatz / 100).toLong() + 1 * defaultmins.toLong() * bans.toLong() * 60000L //Formel!!!!!
            if (increaseBans) {
                if (bans > 0) {
                    mysql.update("UPDATE bans SET BANNED='1', REASON='" + GrundID.reason + "', END='" + end + "', TEAMUUID='" + TeamUUID + "' WHERE uuid='" + uuid + "'")
                } else {
                    mysql.update("UPDATE bans SET BANNED='1', REASON='" + GrundID.reason + "', END='" + increaseEnd + "', TEAMUUID='" + TeamUUID + "' WHERE uuid='" + uuid + "'")
                }
            } else {
                mysql.update("UPDATE bans SET BANNED='1', REASON='" + GrundID.reason + "', END='" + end + "', TEAMUUID='" + TeamUUID + "' WHERE uuid='" + uuid + "'")
            }
        }
    }

    fun mute(uuid: UUID, GrundID: Int, TeamUUID: String) {
        val current = System.currentTimeMillis()
        val end = current + getReasonTime(GrundID)!! * 60000L
        if (getReasonTime(GrundID) == -1) {
            //Perma Mute
            mysql.update("UPDATE bans SET MUTED='1', REASON='" + GrundID.reason + "', END='-1', TEAMUUID='" + TeamUUID + "' WHERE UUID='" + uuid + "'")
        } else {
            //Temp Mute
            mysql.update("UPDATE bans SET MUTED='1', REASON='" + GrundID.reason + "', END='" + end + "', TEAMUUID='" + TeamUUID + "' WHERE UUID='" + uuid + "'")
        }
    }

    fun getRAWEnd(uuid: UUID): Long? {
        if (playerExists(uuid)) {
            val rs = mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
            if (rs!!.next()) {
                return rs.getLong("END")
            }


        }
        return null
    }

    fun getEnd(uuid: UUID): String {
        val uhrzeit = System.currentTimeMillis()
        val end = getRAWEnd(uuid)!!

        var millis = end - uhrzeit

        var sekunden = 0L
        var minuten = 0L
        var stunden = 0L
        var tage = 0L
        while (millis > 1000L) {
            millis -= 1000L
            sekunden += 1L
        }
        while (sekunden > 60L) {
            sekunden -= 60L
            minuten += 1L
        }
        while (minuten > 60L) {
            minuten -= 60L
            stunden += 1L
        }
        while (stunden > 24L) {
            stunden -= 24L
            tage += 1L
        }
        return if (tage != 0L) {
            "§a$tage §7Tag(e) §a$stunden §7Stunde(n) §a$minuten §7Minute(n)"
        } else if (tage == 0L && stunden != 0L) {
            "§a$stunden §7Stunde(n) §a$minuten §7Minute(n) §a$sekunden §7Sekunde(n)"
        } else if (tage == 0L && stunden == 0L && minuten != 0L) {
            "§a$minuten §7Minute(n) §a$sekunden §7Sekunde(n)"
        } else if (tage == 0L && stunden == 0L && minuten == 0L && sekunden != 0L) {
            "§a$sekunden §7Sekunde(n)"
        } else {
            "§4Fehler in der Berechnung!"
        }
        //Alter Code
        //return "§a" + tage + " §7Tag(e) §a" + stunden + " §7Stunde(n) §a" + minuten + " §7Minute(n) §a" + sekunden + " §7Sekunde(n)";
    }

    fun isBanned(uuid: UUID): Boolean {
        if (playerExists(uuid)) {
            val rs = mysql.query("SELECT * FROM bans WHERE UUID='$uuid'")
            if (rs!!.next()) {
                return rs.getInt("BANNED") == 1
            }


        }
        return false
    }

    fun isMuted(uuid: UUID): Boolean {
        if (playerExists(uuid)) {
            val rs = mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
            if (rs!!.next()) {
                return rs.getInt("MUTED") == 1
            }


        }
        return false
    }

    fun unban(uuid: UUID) {
        if (playerExists(uuid)) mysql.update("UPDATE bans SET BANNED='0' WHERE uuid='$uuid'")
    }

    fun unmute(uuid: UUID) {
        if (playerExists(uuid)) {
            mysql.update("UPDATE bans SET MUTED='0' WHERE uuid='$uuid'")
        }
    }

    fun getBans(uuid: UUID): Int = if (playerExists(uuid)) {
        val rs = mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
        if (rs?.next() == true) rs.getInt("BANS") else 0
    } else 0

    fun setBans(uuid: UUID, Bans: Int) {
        if (playerExists(uuid)) {
            mysql.update("UPDATE bans SET BANS='$Bans' WHERE uuid='$uuid'")
        }
    }

    fun getMutes(uuid: UUID): Int {
        if (playerExists(uuid)) {
            val rs = mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
            if (rs!!.next()) {
                return rs.getInt("MUTES")
            }


        }
        return 0
    }

    fun setMutes(uuid: UUID, Mutes: Int) {
        if (playerExists(uuid)) {
            mysql.update("UPDATE bans SET MUTES='$Mutes' WHERE uuid='$uuid'")
        }
    }

    fun countReasons(): Int? {
        val rs = mysql.query("SELECT * FROM reasons")
        var i = 0
        while (rs!!.next()) {
            i++
        }
        return i
    }


    fun getReasonTime(ID: Int): Int? {
        val rs = mysql.query("SELECT * FROM reasons WHERE ID='$ID'")
        if (rs!!.next()) {
            return rs.getInt("TIME")
        }
        return null
    }

    fun isBanReason(ID: Int): Boolean {
        val rs = mysql.query("SELECT * FROM reasons WHERE ID='$ID'")
        if (rs!!.next()) {
            return rs.getInt("TYPE") == 0
        }


        return false
    }

    fun getReasonBans(ReasonID: Int): Int? {
        val rs = mysql.query("SELECT * FROM reasons WHERE ID='$ReasonID'")
        if (rs!!.next()) {
            return rs.getInt("BANS")
        }
        return null
    }

    fun setReasonBans(ReasonID: Int, Bans: Int) {
        mysql.update("UPDATE reasons SET BANS='$Bans' WHERE ID='$ReasonID'")
    }

    fun hasExtraPerms(ReasonID: Int): Boolean {
        val rs = mysql.query("SELECT * FROM reasons WHERE ID='$ReasonID'")
        if (rs!!.next()) {
            return rs.getString("PERMS") != "null"
        }
        return false
    }

    fun getExtraPerms(ReasonID: Int): String? {
        val rs = mysql.query("SELECT * FROM reasons WHERE ID='$ReasonID'")
        if (rs!!.next()) {
            return rs.getString("PERMS")
        }
        return null
    }


    fun webaccountExists(uuid: UUID): Boolean {
        val rs = mysql.query("SELECT * FROM accounts WHERE UUID='$uuid'") ?: return false
        if (rs.next()) {
            return rs.getString("UUID") != null
        }
        return false

    }

    fun createWebAccount(uuid: UUID, Name: String, Rank: Int, PasswordHash: String) {
        mysql.update("INSERT INTO accounts(UUID, USERNAME, PASSWORD, RANK, GOOGLE_AUTH, AUTHCODE) " +
                "VALUES ('" + uuid + "', '" + Name + "', '" + PasswordHash + "', '" + Rank + "', 'null', 'initialpassword')")
    }

    fun deleteWebAccount(uuid: UUID) {
        mysql.update("DELETE FROM accounts WHERE UUID='$uuid'")
    }

    fun isWebaccountAdmin(uuid: UUID): Boolean {
        if (webaccountExists(uuid)) {
            val rs = mysql.query("SELECT * FROM accounts WHERE UUID='$uuid'")
            if (rs!!.next()) {
                return rs.getInt("RANK") == 3
            }
        } else {
            return false
        }
        return false
    }

    fun hasAuthToken(uuid: UUID): Boolean {
        if (webaccountExists(uuid)) {
            val rs = mysql.query("SELECT * FROM accounts WHERE UUID='$uuid'")
            if (rs!!.next()) {
                return rs.getString("AUTHCODE") !== "null"
            }


        } else {
            return false
        }
        return false
    }

    fun getAuthCode(uuid: UUID): String? {
        val rs = mysql.query("SELECT * FROM accounts WHERE UUID='$uuid'")
        if (rs!!.next()) {
            return rs.getString("AUTHCODE")
        }


        return null
    }

    fun updateAuthStatus(uuid: UUID) {
        mysql.update("UPDATE accounts SET AUTHSTATUS = 1 WHERE UUID = '$uuid'")
    }

    fun createReport(uuid: UUID, ReporterUUID: String, Reason: String, LogID: String?) {
        mysql.update("INSERT INTO reports(uuid, REPORTER, TEAM, REASON, LOG, STATUS, CREATED_AT) " +
                "VALUES ('" + uuid + "', '" + ReporterUUID + "', 'null', '" + Reason + "', '" + LogID + "', '0', '" + System.currentTimeMillis() + "')")
    }

    fun countOpenReports(): Int {
        val rs = mysql.query("SELECT * FROM reports WHERE STATUS = 0")
        var i = 0
        while (rs!!.next()) {
            i++
        }
        return i

    }

    fun getNameByReportID(ReportID: Int): String? {
        val rs = mysql.query("SELECT * FROM reports WHERE ID='$ReportID'")
        if (rs!!.next()) {
            return UUID.fromString(rs.getString("UUID")).name
        }


        return null
    }

    fun getReasonByReportID(ReportID: Int): String? {
        val rs = mysql.query("SELECT * FROM reports WHERE ID='$ReportID'")
        if (rs!!.next()) {
            return rs.getString("REASON")
        }


        return null
    }

    fun setReportDone(ID: Int) {
        mysql.update("UPDATE reports SET STATUS = 1 WHERE ID = $ID")
    }

    fun setReportTeamUUID(ID: Int, UUID: String) {
        mysql.update("UPDATE reports SET TEAM = '$UUID' WHERE ID = $ID")
    }

    fun isChatlogAvailable(ID: Int): Boolean {
        val rs = mysql.query("SELECT * FROM reports WHERE ID='$ID'")
        if (rs!!.next()) {
            return rs.getString("LOG") !== "null"
        }
        return false
    }

    fun getChatlogbyReportID(ReportID: Int): String? {
        val rs = mysql.query("SELECT * FROM reports WHERE ID='$ReportID'")
        if (rs!!.next()) {
            return rs.getString("LOG")
        }
        return null
    }

    private fun updateLastLogin(uuid: UUID) = mysql.update("UPDATE bans SET LASTLOGIN = '" + System.currentTimeMillis() + "' WHERE uuid = '" + uuid + "'")

    fun getLastLogin(uuid: UUID): String {
        val rs = mysql.query("SELECT * FROM bans WHERE UUID='$uuid'")
        if (rs!!.next()) {
            return rs.getString("LASTLOGIN")
        }
        return "None"
    }

    fun getFirstLogin(uuid: UUID): String {
        val rs = mysql.query("SELECT * FROM bans WHERE UUID='$uuid'") ?: return "none"
        if (rs.next()) {
            return rs.getString("FIRSTLOGIN")
        }
        return "none"
    }

    fun formatTimestamp(timestamp: Long): String = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Date(timestamp))

}

private const val none = "none"

val Int.reason: String
    get() {
        val rs = mysql.query("SELECT REASON FROM reasons WHERE ID='$this'")
        return if (rs?.next() == true) rs.getString("REASON") else none
    }

val UUID.name: String
    get() {
        val rs = mysql.query("SELECT NAME FROM bans WHERE uuid='$this'")
        return if (rs?.next() == true) rs.getString("NAME") else none
    }

val UUID.reasonString: String
    get() {
        val rs = mysql.query("SELECT REASON FROM bans WHERE uuid='$this'")
        return if (rs?.next() == true) rs.getString("REASON") else none
    }

fun ActionType.sendNotify(bannedName: String, senderName: String) {
    val message = prefix + message
            .replace("%banned-name%", bannedName)
            .replace("%sender-name%", senderName)
    for (all in players)
        if (all.hasPermission("professionalbans.notify"))
            all.msg(message)
}