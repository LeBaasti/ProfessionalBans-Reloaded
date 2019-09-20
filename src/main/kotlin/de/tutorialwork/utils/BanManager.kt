package de.tutorialwork.utils

import de.tutorialwork.main.Main
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

object BanManager {

    val iDsFromOpenReports: List<Int>
        get() {
            return try {
                val rs = Main.mysql.query("SELECT * FROM reports WHERE STATUS = 0")
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
        val rs = Main.mysql.query("SELECT * FROM bans WHERE uuid='$uuid'") ?: return false
        return if (rs.next()) rs.getString("uuid") != null
        else false
    }

    fun createPlayer(uuid: UUID, name: String) {
        if (!playerExists(uuid)) {
            Main.mysql.update("INSERT INTO bans(uuid, NAME, BANNED, MUTED, REASON, END, TEAMUUID, BANS, MUTES, FIRSTLOGIN, LASTLOGIN) " +
                    "VALUES ('" + uuid + "', '" + name + "', '0', '0', 'null', 'null', 'null', '0', '0', '" + System.currentTimeMillis() + "', '" + System.currentTimeMillis() + "')")
        } else {
            updateName(uuid, name)
            updateLastLogin(uuid)
        }
    }

    fun getBanReasonsList(p: ProxiedPlayer) {
        try {
            val rs = Main.mysql.query("SELECT * FROM reasons ORDER BY SORTINDEX ASC")
            while (rs!!.next()) {
                val id = rs.getInt("ID")
                if (isBanReason(id)) {
                    p.sendMessage("§7" + id + " §8| §e" + getReasonByID(id))
                } else {
                    p.sendMessage("§7" + id + " §8| §e" + getReasonByID(id) + " §8(§cMUTE§8)")
                }
            }
        } catch (exc: SQLException) {
        }

    }

    fun getNameByUUID(uuid: UUID): String? {
        if (playerExists(uuid)) {
            try {
                val rs = Main.mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
                if (rs!!.next()) {
                    return rs.getString("NAME")
                }
            } catch (exc: SQLException) {

            }

        }
        return null
    }

    fun getUUIDByName(name: String): String? {
        try {
            val rs = Main.mysql.query("SELECT * FROM bans WHERE NAME='$name'")
            if (rs!!.next()) {
                return rs.getString("UUID")
            }
        } catch (exc: SQLException) {

        }

        return null
    }

    private fun updateName(uuid: UUID, newName: String) {
        if (playerExists(uuid)) Main.mysql.update("UPDATE bans SET NAME='$newName' WHERE uuid='$uuid'")
    }

    fun ban(uuid: UUID, GrundID: Int, TeamUUID: String, Prozentsatz: Int, increaseBans: Boolean) {
        if (getReasonTime(GrundID) == -1) {
            //Perma Ban
            Main.mysql.update("UPDATE bans SET BANNED='1', REASON='" + getReasonByID(GrundID) + "', END='-1', TEAMUUID='" + TeamUUID + "' WHERE uuid='" + uuid + "'")
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
                    Main.mysql.update("UPDATE bans SET BANNED='1', REASON='" + getReasonByID(GrundID) + "', END='" + end + "', TEAMUUID='" + TeamUUID + "' WHERE uuid='" + uuid + "'")
                } else {
                    Main.mysql.update("UPDATE bans SET BANNED='1', REASON='" + getReasonByID(GrundID) + "', END='" + increaseEnd + "', TEAMUUID='" + TeamUUID + "' WHERE uuid='" + uuid + "'")
                }
            } else {
                Main.mysql.update("UPDATE bans SET BANNED='1', REASON='" + getReasonByID(GrundID) + "', END='" + end + "', TEAMUUID='" + TeamUUID + "' WHERE uuid='" + uuid + "'")
            }
        }
    }

    fun mute(uuid: UUID, GrundID: Int, TeamUUID: String) {
        val current = System.currentTimeMillis()
        val end = current + getReasonTime(GrundID)!! * 60000L
        if (getReasonTime(GrundID) == -1) {
            //Perma Mute
            Main.mysql.update("UPDATE bans SET MUTED='1', REASON='" + getReasonByID(GrundID) + "', END='-1', TEAMUUID='" + TeamUUID + "' WHERE UUID='" + uuid + "'")
        } else {
            //Temp Mute
            Main.mysql.update("UPDATE bans SET MUTED='1', REASON='" + getReasonByID(GrundID) + "', END='" + end + "', TEAMUUID='" + TeamUUID + "' WHERE UUID='" + uuid + "'")
        }
    }

    fun getRAWEnd(uuid: UUID): Long? {
        if (playerExists(uuid)) {
            try {
                val rs = Main.mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
                if (rs!!.next()) {
                    return rs.getLong("END")
                }
            } catch (exc: SQLException) {

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
            try {
                val rs = Main.mysql.query("SELECT * FROM bans WHERE UUID='$uuid'")
                if (rs!!.next()) {
                    return rs.getInt("BANNED") == 1
                }
            } catch (exc: SQLException) {

            }

        }
        return false
    }

    fun isMuted(uuid: UUID): Boolean {
        if (playerExists(uuid)) {
            try {
                val rs = Main.mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
                if (rs!!.next()) {
                    return rs.getInt("MUTED") == 1
                }
            } catch (exc: SQLException) {

            }

        }
        return false
    }

    fun unban(uuid: UUID) {
        if (playerExists(uuid)) Main.mysql.update("UPDATE bans SET BANNED='0' WHERE uuid='$uuid'")
    }

    fun unmute(uuid: UUID) {
        if (playerExists(uuid)) {
            Main.mysql.update("UPDATE bans SET MUTED='0' WHERE uuid='$uuid'")
        }
    }

    fun getReasonString(uuid: UUID): String {
        if (playerExists(uuid)) {
            try {
                val rs = Main.mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
                if (rs!!.next()) {
                    return rs.getString("REASON")
                }
            } catch (exc: SQLException) {

            }

        }
        return "Nothing"
    }

    fun sendNotify(Type: String, BannedName: String, TeamName: String, Grund: String?) {
        if (Type.toUpperCase() == "BAN") {
            for (all in ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("professionalbans.notify")) {
                    all.sendMessage(Main.prefix + "§e§l" + BannedName + " §7wurde von §c§l" + TeamName + " §cgebannt §7wegen §a" + Grund)
                }
            }
        }
        if (Type.toUpperCase() == "IPBAN") {
            for (all in ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("professionalbans.notify")) {
                    all.sendMessage(Main.prefix + "§7Die IP §e§l" + BannedName + " §7wurde von §c§l" + TeamName + " §cgebannt §7wegen §a" + Grund)
                }
            }
        }
        if (Type.toUpperCase() == "MUTE") {
            for (all in ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("professionalbans.notify") || all.hasPermission("professionalbans.*")) {
                    all.sendMessage(Main.prefix + "§e§l" + BannedName + " §7wurde von §c§l" + TeamName + " §cgemutet §7wegen §a" + Grund)
                }
            }
        }
        if (Type.toUpperCase() == "KICK") {
            for (all in ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("professionalbans.notify") || all.hasPermission("professionalbans.*")) {
                    all.sendMessage(Main.prefix + "§e§l" + BannedName + " §7wurde von §c§l" + TeamName + " §cgekickt §7wegen §a" + Grund)
                }
            }
        }
        if (Type.toUpperCase() == "UNBAN") {
            for (all in ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("professionalbans.notify") || all.hasPermission("professionalbans.*")) {
                    all.sendMessage(Main.prefix + "§c§l" + TeamName + " §7hat §e§l" + BannedName + " §aentbannt")
                }
            }
        }
        if (Type.toUpperCase() == "UNBANIP") {
            for (all in ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("professionalbans.notify") || all.hasPermission("professionalbans.*")) {
                    all.sendMessage(Main.prefix + "§c§l" + TeamName + " §7hat die IP-Adresse §e§l" + BannedName + " §aentbannt")
                }
            }
        }
        if (Type.toUpperCase() == "UNMUTE") {
            for (all in ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("professionalbans.notify") || all.hasPermission("professionalbans.*")) {
                    all.sendMessage(Main.prefix + "§c§l" + TeamName + " §7hat §e§l" + BannedName + " §aentmutet")
                }
            }
        }
        if (Type.toUpperCase() == "REPORT") {
            for (all in ProxyServer.getInstance().getPlayers()) {
                if (all.hasPermission("professionalbans.notify") || all.hasPermission("professionalbans.*")) {
                    all.sendMessage(Main.prefix + "§c§l" + TeamName + " §7hat §e§l" + BannedName + " §7wegen §a" + Grund + " §7gemeldet")
                }
            }
        }
    }

    fun getBans(uuid: UUID): Int {
        if (playerExists(uuid)) {
            try {
                val rs = Main.mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
                if (rs!!.next()) {
                    return rs.getInt("BANS")
                }
            } catch (exc: SQLException) {

            }

        }
        return 0
    }

    fun setBans(uuid: UUID, Bans: Int) {
        if (playerExists(uuid)) {
            Main.mysql.update("UPDATE bans SET BANS='$Bans' WHERE uuid='$uuid'")
        }
    }

    fun getMutes(uuid: UUID): Int {
        if (playerExists(uuid)) {
            try {
                val rs = Main.mysql.query("SELECT * FROM bans WHERE uuid='$uuid'")
                if (rs!!.next()) {
                    return rs.getInt("MUTES")
                }
            } catch (exc: SQLException) {

            }

        }
        return 0
    }

    fun setMutes(uuid: UUID, Mutes: Int) {
        if (playerExists(uuid)) {
            Main.mysql.update("UPDATE bans SET MUTES='$Mutes' WHERE uuid='$uuid'")
        }
    }

    fun countReasons(): Int? {
        try {
            val rs = Main.mysql.query("SELECT * FROM reasons")
            var i = 0
            while (rs!!.next()) {
                i++
            }
            return i
        } catch (exc: SQLException) {

        }

        return null
    }

    fun getReasonByID(Reason: Int): String {
        try {
            val rs = Main.mysql.query("SELECT * FROM reasons WHERE ID='$Reason'") ?: return "Nothing"
            if (rs.next()) {
                return rs.getString("REASON")
            }
        } catch (exc: SQLException) {

        }

        return "Nothing"
    }

    fun getReasonTime(ID: Int): Int? {
        try {
            val rs = Main.mysql.query("SELECT * FROM reasons WHERE ID='$ID'")
            if (rs!!.next()) {
                return rs.getInt("TIME")
            }
        } catch (exc: SQLException) {

        }

        return null
    }

    fun isBanReason(ID: Int): Boolean {
        try {
            val rs = Main.mysql.query("SELECT * FROM reasons WHERE ID='$ID'")
            if (rs!!.next()) {
                return rs.getInt("TYPE") == 0
            }
        } catch (exc: SQLException) {

        }

        return false
    }

    fun getReasonBans(ReasonID: Int): Int? {
        try {
            val rs = Main.mysql.query("SELECT * FROM reasons WHERE ID='$ReasonID'")
            if (rs!!.next()) {
                return rs.getInt("BANS")
            }
        } catch (exc: SQLException) {

        }

        return null
    }

    fun setReasonBans(ReasonID: Int, Bans: Int) {
        Main.mysql.update("UPDATE reasons SET BANS='$Bans' WHERE ID='$ReasonID'")
    }

    fun hasExtraPerms(ReasonID: Int): Boolean {
        try {
            val rs = Main.mysql.query("SELECT * FROM reasons WHERE ID='$ReasonID'")
            if (rs!!.next()) {
                return rs.getString("PERMS") != "null"
            }
        } catch (exc: SQLException) {

        }

        return false
    }

    fun getExtraPerms(ReasonID: Int): String? {
        try {
            val rs = Main.mysql.query("SELECT * FROM reasons WHERE ID='$ReasonID'")
            if (rs!!.next()) {
                return rs.getString("PERMS")
            }
        } catch (exc: SQLException) {

        }

        return null
    }


    fun webaccountExists(uuid: UUID): Boolean {
        try {
            val rs = Main.mysql.query("SELECT * FROM accounts WHERE UUID='$uuid'") ?: return false
            if (rs.next()) {
                return rs.getString("UUID") != null
            }

        } catch (exc: SQLException) {

        }

        return false

    }

    fun createWebAccount(uuid: UUID, Name: String, Rank: Int, PasswordHash: String) {
        Main.mysql.update("INSERT INTO accounts(UUID, USERNAME, PASSWORD, RANK, GOOGLE_AUTH, AUTHCODE) " +
                "VALUES ('" + uuid + "', '" + Name + "', '" + PasswordHash + "', '" + Rank + "', 'null', 'initialpassword')")
    }

    fun deleteWebAccount(uuid: UUID) {
        Main.mysql.update("DELETE FROM accounts WHERE UUID='$uuid'")
    }

    fun isWebaccountAdmin(uuid: UUID): Boolean {
        if (webaccountExists(uuid)) {
            try {
                val rs = Main.mysql.query("SELECT * FROM accounts WHERE UUID='$uuid'")
                if (rs!!.next()) {
                    return rs.getInt("RANK") == 3
                }
            } catch (exc: SQLException) {

            }

        } else {
            return false
        }
        return false
    }

    fun hasAuthToken(uuid: UUID): Boolean {
        if (webaccountExists(uuid)) {
            try {
                val rs = Main.mysql.query("SELECT * FROM accounts WHERE UUID='$uuid'")
                if (rs!!.next()) {
                    return rs.getString("AUTHCODE") !== "null"
                }
            } catch (exc: SQLException) {

            }

        } else {
            return false
        }
        return false
    }

    fun getAuthCode(uuid: UUID): String? {
        try {
            val rs = Main.mysql.query("SELECT * FROM accounts WHERE UUID='$uuid'")
            if (rs!!.next()) {
                return rs.getString("AUTHCODE")
            }
        } catch (exc: SQLException) {

        }

        return null
    }

    fun updateAuthStatus(uuid: UUID) {
        Main.mysql.update("UPDATE accounts SET AUTHSTATUS = 1 WHERE UUID = '$uuid'")
    }

    fun createReport(uuid: UUID, ReporterUUID: String, Reason: String, LogID: String?) {
        Main.mysql.update("INSERT INTO reports(uuid, REPORTER, TEAM, REASON, LOG, STATUS, CREATED_AT) " +
                "VALUES ('" + uuid + "', '" + ReporterUUID + "', 'null', '" + Reason + "', '" + LogID + "', '0', '" + System.currentTimeMillis() + "')")
    }

    fun countOpenReports(): Int {
        try {
            val rs = Main.mysql.query("SELECT * FROM reports WHERE STATUS = 0")
            var i = 0
            while (rs!!.next()) {
                i++
            }
            return i
        } catch (exc: SQLException) {

        }

        return 0
    }

    fun getNameByReportID(ReportID: Int): String? {
        try {
            val rs = Main.mysql.query("SELECT * FROM reports WHERE ID='$ReportID'")
            if (rs!!.next()) {
                return getNameByUUID(UUID.fromString(rs.getString("UUID")))
            }
        } catch (exc: SQLException) {

        }

        return null
    }

    fun getReasonByReportID(ReportID: Int): String? {
        try {
            val rs = Main.mysql.query("SELECT * FROM reports WHERE ID='$ReportID'")
            if (rs!!.next()) {
                return rs.getString("REASON")
            }
        } catch (exc: SQLException) {

        }

        return null
    }

    fun setReportDone(ID: Int) {
        Main.mysql.update("UPDATE reports SET STATUS = 1 WHERE ID = $ID")
    }

    fun setReportTeamUUID(ID: Int, UUID: String) {
        Main.mysql.update("UPDATE reports SET TEAM = '$UUID' WHERE ID = $ID")
    }

    fun isChatlogAvailable(ID: Int): Boolean {
        try {
            val rs = Main.mysql.query("SELECT * FROM reports WHERE ID='$ID'")
            if (rs!!.next()) {
                return rs.getString("LOG") !== "null"
            }
        } catch (exc: SQLException) {

        }

        return false
    }

    fun getChatlogbyReportID(ReportID: Int): String? {
        try {
            val rs = Main.mysql.query("SELECT * FROM reports WHERE ID='$ReportID'")
            if (rs!!.next()) {
                return rs.getString("LOG")
            }
        } catch (exc: SQLException) {

        }

        return null
    }

    private fun updateLastLogin(uuid: UUID) = Main.mysql.update("UPDATE bans SET LASTLOGIN = '" + System.currentTimeMillis() + "' WHERE uuid = '" + uuid + "'")

    fun getLastLogin(uuid: UUID): String {
        try {
            val rs = Main.mysql.query("SELECT * FROM bans WHERE UUID='$uuid'")
            if (rs!!.next()) {
                return rs.getString("LASTLOGIN")
            }
        } catch (exc: SQLException) {

        }

        return "None"
    }

    fun getFirstLogin(uuid: UUID): String {
        try {
            val rs = Main.mysql.query("SELECT * FROM bans WHERE UUID='$uuid'") ?: return "none"
            if (rs.next()) {
                return rs.getString("FIRSTLOGIN")
            }
        } catch (exc: SQLException) {

        }

        return "none"
    }

    fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val jdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return jdf.format(date)
    }

}