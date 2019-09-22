package de.tutorialwork.utils

import de.tutorialwork.main.Main
import de.tutorialwork.mysql
import java.sql.SQLException
import java.util.*

object IPManager {

    //ips(IP varchar(64) UNIQUE, USED_BY varchar(64), USED_AT varchar(64), BANNED int(11), REASON varchar(64), END long, TEAMUUID varchar(64), BANS int(11));

    fun ipExists(IP: String): Boolean {
        try {

            val rs = mysql.query("SELECT * FROM ips WHERE IP='$IP'")
            if (rs!!.next()) {
                return rs.getString("IP") != null
            }

        } catch (exc: SQLException) {

        }

        return false

    }

    fun insertIP(IP: String, uuid: UUID) {
        if (!ipExists(IP)) {
            mysql.update("INSERT INTO ips(IP, USED_BY, USED_AT, BANNED, REASON, END, TEAMUUID, BANS) " +
                    "VALUES ('" + IP + "', '" + uuid + "', '" + System.currentTimeMillis() + "', '0', 'null', 'null', 'null', '0')")
        } else {
            updateIPInfos(IP, uuid)
        }
    }

    private fun updateIPInfos(IP: String, newUUID: UUID) {
        if (ipExists(IP)) {
            mysql.update("UPDATE ips SET USED_BY = '" + newUUID + "', USED_AT='" + System.currentTimeMillis() + "' WHERE IP='" + IP + "'")
        }
    }

    fun ban(IP: String, GrundID: Int, TeamUUID: String) {
        val current = System.currentTimeMillis()
        val end = current + BanManager.getReasonTime(GrundID)!! * 60000L
        if (BanManager.getReasonTime(GrundID) == -1) {
            //Perma Ban
            mysql.update("UPDATE ips SET BANNED='1', REASON='" + GrundID.reason + "', END='-1', TEAMUUID='" + TeamUUID + "' WHERE IP='" + IP + "'")
        } else {
            //Temp Ban
            mysql.update("UPDATE ips SET BANNED='1', REASON='" + GrundID.reason + "', END='" + end + "', TEAMUUID='" + TeamUUID + "' WHERE IP='" + IP + "'")
        }
    }


    fun isBanned(IP: String): Boolean {
        if (ipExists(IP)) {
            try {
                val rs = mysql.query("SELECT * FROM ips WHERE IP='$IP'")
                if (rs!!.next()) {
                    return rs.getInt("BANNED") == 1
                }
            } catch (exc: SQLException) {

            }

        }
        return false
    }

    fun getReasonString(IP: String): String? {
        if (ipExists(IP)) {
            try {
                val rs = mysql.query("SELECT * FROM ips WHERE IP='$IP'")
                if (rs!!.next()) {
                    return rs.getString("REASON")
                }
            } catch (exc: SQLException) {

            }

        }
        return null
    }

    fun getRAWEnd(IP: String): Long? {
        if (ipExists(IP)) {
            try {
                val rs = mysql.query("SELECT * FROM ips WHERE IP='$IP'")
                if (rs!!.next()) {
                    return rs.getLong("END")
                }
            } catch (exc: SQLException) {

            }

        }
        return null
    }

    fun getEnd(UUID: String): String {
        val uhrzeit = System.currentTimeMillis()
        val end = getRAWEnd(UUID)!!

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

    fun unban(IP: String) {
        if (ipExists(IP)) {
            mysql.update("UPDATE ips SET BANNED='0' WHERE IP='$IP'")
        }
    }

    fun isVPN(IP: String): Boolean {
        if (IP != "127.0.0.1") {
            if (Main.APIKey != null) {
                var json = Main.callURL("http://proxycheck.io/v2/" + IP + "?key=" + Main.APIKey)
                json = json.replace("{\n" +
                        "    \"status\": \"ok\",\n" +
                        "    \"" + IP + "\": {\n" +
                        "        \"proxy\": \"", "")
                json = json.replace("\"\n" +
                        "    }\n" +
                        "}", "")
                return json == "yes"
            } else {
                var json = Main.callURL("http://proxycheck.io/v2/$IP?key=318n07-0o7054-y9y82a-75o3hr")
                json = json.replace("{\n" +
                        "    \"status\": \"ok\",\n" +
                        "    \"" + IP + "\": {\n" +
                        "        \"proxy\": \"", "")
                json = json.replace("\"\n" +
                        "    }\n" +
                        "}", "")
                return json == "yes"
            }
        } else {
            return false
        }
    }

    fun getIPFromPlayer(uuid: UUID): String {
        try {
            val rs = mysql.query("SELECT * FROM ips WHERE USED_BY='$uuid'") ?: return ""
            if (rs.next()) {
                return rs.getString("IP")
            }
        } catch (exc: SQLException) {

        }
        return ""
    }

    fun getPlayerFromIP(IP: String): UUID? {
        try {
            val rs = mysql.query("SELECT * FROM ips WHERE IP='$IP'")
            if (rs!!.next()) {
                return UUID.fromString(rs.getString("USED_BY"))
            }
        } catch (exc: SQLException) {

        }

        return null
    }

    fun getBans(IP: String): Int? {
        if (ipExists(IP)) {
            try {
                val rs = mysql.query("SELECT * FROM ips WHERE IP='$IP'")
                if (rs!!.next()) {
                    return rs.getInt("BANS")
                }
            } catch (exc: SQLException) {

            }

        }
        return null
    }

    private fun setBans(IP: String, Bans: Int) {
        if (ipExists(IP)) {
            mysql.update("UPDATE ips SET BANS='$Bans' WHERE IP='$IP'")
        }
    }

    fun addBan(IP: String) {
        setBans(IP, getBans(IP)!! + 1)
    }

    fun getLastUseLong(IP: String): Long {
        if (ipExists(IP)) {
            try {
                val rs = mysql.query("SELECT * FROM ips WHERE IP='$IP'")
                if (rs!!.next()) {
                    return java.lang.Long.valueOf(rs.getString("USED_AT"))
                }
            } catch (exc: SQLException) {

            }

        }
        return 0
    }

}