package de.tutorialwork.main

import de.tutorialwork.commands.*
import de.tutorialwork.console
import de.tutorialwork.listener.Chat
import de.tutorialwork.listener.Login
import de.tutorialwork.listener.Quit
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.MySQLConnect
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.util.*
import java.util.concurrent.TimeUnit


class Main : Plugin() {

    init {
        instance = this
    }


    override fun onEnable() {
        config()
        mySQL()
        registerCommands()
        registerListeners()
        //Konsolen Nachricht über das Plugin
        proxy.console.apply {
            sendMessage("§8[]===================================[]")
            sendMessage("§e§lProfessionalBans §7§oReloaded §8| §7Version: §c$Version")
            sendMessage("§7Developer: §e§lTutorialwork, LeBaasti")
            sendMessage("§5YT §7Kanal: §cyoutube.com/Tutorialwork")
            sendMessage("§8[]===================================[]")
        }


        //Überprüft auf Bans aus dem Webinterface
        proxy.scheduler.schedule(this, {
            val config = File(Main.instance.dataFolder, "config.yml")
            try {
                val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                for (all in proxy.players) {
                    val uuid = all.uniqueId
                    if (BanManager.isBanned(uuid)) {
                        if (BanManager.getRAWEnd(uuid) == -1L) {
                            all.disconnect(ChatColor.translateAlternateColorCodes('&', configcfg.getString("LAYOUT.BAN")
                                    .replace("%grund%", BanManager.getReasonString(uuid))))
                        } else {
                            var msg = configcfg.getString("LAYOUT.TEMPBAN")
                            msg = msg.replace("%grund%", BanManager.getReasonString(uuid))
                            msg = msg.replace("%dauer%", BanManager.getEnd(uuid))
                            all.disconnect(ChatColor.translateAlternateColorCodes('&', msg))
                        }
                    }
                }
                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }, 5, 5, TimeUnit.SECONDS)
    }

    private fun config() {
        if (!dataFolder.exists()) dataFolder.mkdir()
        val file = File(dataFolder.path, "mysql.yml")
        val config = File(dataFolder.path, "config.yml")
        val blacklistfile = File(dataFolder.path, "blacklist.yml")
        try {
            if (!file.exists()) {
                file.createNewFile()
                val mysql = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
                mysql.set("HOST", "localhost")
                mysql.set("DATENBANK", "Bans")
                mysql.set("USER", "root")
                mysql.set("PASSWORT", "deinpasswort")
                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(mysql, file)
            }
            if (!config.exists()) {
                config.createNewFile()
                val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                configcfg.set("PREFIX", "&e&lBANS &8• &7")
                configcfg.set("LAYOUT.BAN", "&8[]===================================[] \n\n &4&lDu wurdest GEBANNT \n\n &eGrund: §c§l%grund% \n\n&8[]===================================[]")
                configcfg.set("LAYOUT.KICK", "&8[]===================================[] \n\n &e&lDu wurdest GEKICKT \n\n &eGrund: §c§l%grund% \n\n&8[]===================================[]")
                configcfg.set("LAYOUT.TEMPBAN", "&8[]===================================[] \n\n &4&lDu wurdest temporär GEBANNT \n\n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n\n&8[]===================================[]")
                configcfg.set("LAYOUT.MUTE", "&8[]===================================[] \n\n &4&lDu wurdest GEMUTET \n\n &eGrund: §c§l%grund% \n\n&8[]===================================[]")
                configcfg.set("LAYOUT.TEMPMUTE", "&8[]===================================[] \n\n &4&lDu wurdest temporär GEMUTET \n\n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n\n&8[]===================================[]")
                configcfg.set("LAYOUT.IPBAN", "&8[]===================================[] \n\n &4&lDeine IP-Adresse wurde GEBANNT \n\n &eGrund: §c§l%grund% \n\n&8[]===================================[]")
                configcfg.set("LAYOUT.TEMPIPBAN", "&8[]===================================[] \n\n &4&lDeine IP-Adresse wurde temporär GEBANNT \n\n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n\n&8[]===================================[]")
                configcfg.set("VPN.BLOCKED", true)
                configcfg.set("VPN.KICK", true)
                configcfg.set("VPN.KICKMSG", "&7Das benutzen einer &4VPN &7ist auf unserem Netzwerk &cUNTERSAGT")
                configcfg.set("VPN.BAN", false)
                configcfg.set("VPN.BANID", 0)
                ipwhitelist.add("8.8.8.8")
                configcfg.set("VPN.WHITELIST", ipwhitelist)
                configcfg.set("VPN.APIKEY", "Go to https://proxycheck.io/dashboard and register with your email and enter here your API Key")
                configcfg.set("REPORTS.ENABLED", true)
                reportreasons.add("Hacking")
                reportreasons.add("Verhalten")
                reportreasons.add("Teaming")
                reportreasons.add("TPA-Falle")
                reportreasons.add("Werbung")
                configcfg.set("REPORTS.REASONS", reportreasons)
                configcfg.set("REPORTS.OFFLINEREPORTS", false)
                configcfg.set("CHATLOG.ENABLED", true)
                configcfg.set("CHATLOG.URL", "DeinServer.net/BanWebinterface/public/chatlog.php?id=")
                configcfg.set("AUTOMUTE.ENABLED", false)
                configcfg.set("AUTOMUTE.AUTOREPORT", true)
                //configcfg.set("AUTOMUTE.AUTOREPORT.REASON", "Automatischer Report");
                configcfg.set("AUTOMUTE.MUTEID", 0)
                configcfg.set("AUTOMUTE.ADMUTEID", 0)
                configcfg.set("BANTIME-INCREASE.ENABLED", true)
                configcfg.set("BANTIME-INCREASE.PERCENTRATE", 50)
                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
            } else {
                val configcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(config)
                if (configcfg.getBoolean("VPN.KICK") && configcfg.getBoolean("VPN.BAN")) {
                    console.sendMessage("§8[]===================================[]")
                    console.sendMessage("§c§lSINNLOSE EINSTELLUNG ENTDECKT")
                    console.sendMessage("§7Wenn ein Spieler mit einer VPN das Netzwerk betritt kann er nicht gekickt UND gebannt werden.")
                    console.sendMessage("§4§lÜberprüfe die VPN Einstellung in der CONFIG.YML")
                    console.sendMessage("§8[]===================================[]")
                    //Setze VPN Einstellung zurück!
                    configcfg.set("VPN.BLOCKED", true)
                    configcfg.set("VPN.KICK", true)
                    configcfg.set("VPN.KICKMSG", "&7Das benutzen einer &4VPN &7ist auf unserem Netzwerk &cUNTERSAGT")
                    configcfg.set("VPN.BAN", false)
                    configcfg.set("VPN.BANID", 0)
                }
                for (reasons in configcfg.getStringList("REPORTS.REASONS")) {
                    reportreasons.add(reasons.toUpperCase())
                }
                for (ips in configcfg.getStringList("VPN.WHITELIST")) {
                    ipwhitelist.add(ips)
                }
                prefix = configcfg.getString("PREFIX").replace("&", "§")
                increaseBans = configcfg.getBoolean("BANTIME-INCREASE.ENABLED")
                increaseValue = configcfg.getInt("BANTIME-INCREASE.PERCENTRATE")
                if (configcfg.getString("VPN.APIKEY").length == 27) {
                    APIKey = configcfg.getString("VPN.APIKEY")
                }
                ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(configcfg, config)
            }
            if (!blacklistfile.exists()) blacklistfile.createNewFile()
            val blacklistcfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(blacklistfile)
            for (congigstr in blacklistcfg.getStringList("BLACKLIST")) blacklist.add(congigstr)
            for (congigstr in blacklistcfg.getStringList("ADBLACKLIST")) adblacklist.add(congigstr)
            for (congigstr in blacklistcfg.getStringList("ADWHITELIST")) adwhitelist.add(congigstr.toUpperCase())
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun mySQL() {
        try {
            val file = File(dataFolder.path, "mysql.yml")
            val mysql = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
            MySQLConnect.HOST = mysql.getString("HOST")
            MySQLConnect.DATABASE = mysql.getString("DATENBANK")
            MySQLConnect.USER = mysql.getString("USER")
            MySQLConnect.PASSWORD = mysql.getString("PASSWORT")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mysql = MySQLConnect(MySQLConnect.HOST, MySQLConnect.DATABASE, MySQLConnect.USER, MySQLConnect.PASSWORD)
        mysql.update("CREATE TABLE IF NOT EXISTS accounts(UUID varchar(64) UNIQUE, USERNAME varchar(255), PASSWORD varchar(255), RANK int(11), GOOGLE_AUTH varchar(255), AUTHCODE varchar(255));")
        mysql.update("CREATE TABLE IF NOT EXISTS reasons(ID int(11) UNIQUE, REASON varchar(255), TIME int(255), TYPE int(11), ADDED_AT varchar(11), BANS int(11), PERMS varchar(255));")
        mysql.update("CREATE TABLE IF NOT EXISTS bans(UUID varchar(64) UNIQUE, NAME varchar(64), BANNED int(11), MUTED int(11), REASON varchar(64), END long, TEAMUUID varchar(64), BANS int(11), MUTES int(11), FIRSTLOGIN varchar(255), LASTLOGIN varchar(255));")
        mysql.update("CREATE TABLE IF NOT EXISTS ips(IP varchar(64) UNIQUE, USED_BY varchar(64), USED_AT varchar(64), BANNED int(11), REASON varchar(64), END long, TEAMUUID varchar(64), BANS int(11));")
        mysql.update("CREATE TABLE IF NOT EXISTS reports(ID int(11) AUTO_INCREMENT UNIQUE, UUID varchar(64), REPORTER varchar(64), TEAM varchar(64), REASON varchar(64), LOG varchar(64), STATUS int(11), CREATED_AT long);")
        mysql.update("CREATE TABLE IF NOT EXISTS chat(ID int(11) AUTO_INCREMENT UNIQUE, UUID varchar(64), SERVER varchar(64), MESSAGE varchar(2500), SENDDATE varchar(255));")
        mysql.update("CREATE TABLE IF NOT EXISTS chatlog(ID int(11) AUTO_INCREMENT UNIQUE, LOGID varchar(255), UUID varchar(64), CREATOR_UUID varchar(64), SERVER varchar(64), MESSAGE varchar(2500), SENDDATE varchar(255), CREATED_AT varchar(255));")
        mysql.update("CREATE TABLE IF NOT EXISTS log(ID int(11) AUTO_INCREMENT UNIQUE, UUID varchar(255), BYUUID varchar(255), ACTION varchar(255), NOTE varchar(255), DATE varchar(255));")
        mysql.update("CREATE TABLE IF NOT EXISTS unbans(ID int(11) AUTO_INCREMENT UNIQUE, UUID varchar(255), FAIR int(11), MESSAGE varchar(10000), DATE varchar(255), STATUS int(11));")
        mysql.update("CREATE TABLE IF NOT EXISTS apptokens(UUID varchar(36) UNIQUE, TOKEN varchar(555));")
        //SQL Update 2.0
        mysql.update("ALTER TABLE accounts ADD IF NOT EXISTS AUTHSTATUS int(11);")
        //SQL Update 2.2
        mysql.update("ALTER TABLE bans ADD IF NOT EXISTS FIRSTLOGIN varchar(255);")
        mysql.update("ALTER TABLE bans ADD IF NOT EXISTS LASTLOGIN varchar(255);")
        //SQL Update 2.4
        mysql.update("ALTER TABLE reasons ADD COLUMN SORTINDEX int(11)")
    }

    private fun registerCommands() {
        try {
            val file = File(dataFolder.path, "config.yml")
            val cfg = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(file)
            arrayOf(Ban("ban"),
                    Unban("unban"),
                    Kick("kick"),
                    WebAccount("webaccount"),
                    Check("check"),
                    IPBan("ipban"),
                    Blacklist("blacklist"),
                    WebVerify("webverify"),
                    SupportChat("support")
            ).forEach { it.register() }


            if (cfg.getBoolean("REPORTS.ENABLED")) {
                Report("report").register()
                Reports("reports").register()
            }
            if (cfg.getBoolean("CHATLOG.ENABLED")) Chatlog("chatlog").register()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun registerListeners() {
        proxy.pluginManager.registerListener(this, Login())
        proxy.pluginManager.registerListener(this, Chat())
        proxy.pluginManager.registerListener(this, Quit())
    }

    companion object {

        lateinit var instance: Main
        lateinit var mysql: MySQLConnect
        var prefix = "§e§lBANS §8• §7"
        var noPerms = "$prefix§cDu hast keine Berechtigung diesen Befehl zu nutzen"

        var reportreasons = mutableListOf<String>()
        var blacklist = ArrayList<String>()
        var adblacklist = ArrayList<String>()
        var adwhitelist = ArrayList<String>()
        var ipwhitelist = ArrayList<String>()

        var increaseBans = true
        var increaseValue: Int = 50

        var APIKey: String? = null

        //==============================================
        //Plugin Informationen
        var Version = "2.4"

        fun callURL(myURL: String): String {
            val url = URL(myURL)
            val urlConn: URLConnection? = url.openConnection()
            if (urlConn != null) urlConn.readTimeout = 60 * 1000
            val inputStream = urlConn?.getInputStream() ?: return ""
            return inputStream.reader().readText()
        }
    }

    private fun Command.register() = proxy.pluginManager.registerCommand(this@Main, this)

}