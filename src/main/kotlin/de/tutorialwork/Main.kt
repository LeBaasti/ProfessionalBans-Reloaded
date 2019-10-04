package de.tutorialwork

import de.tutorialwork.commands.*
import de.tutorialwork.global.*
import de.tutorialwork.listener.Chat
import de.tutorialwork.listener.Login
import de.tutorialwork.listener.Quit
import de.tutorialwork.utils.*
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import java.util.concurrent.TimeUnit


class Main : Plugin() {

	init {
		instance = this
	}

	override fun onEnable() {
		registerConfig()
		registerBlackListConfig()

		registerMySQL()

		registerCommands()
		registerListeners()

		//Konsolen Nachricht über das Plugin
		proxy.console.msg(
				"§8[]===================================[]",
				"§e§lProfessionalBans §7§oReloaded §8| §7Version: §c$version",
				"§7Developer: §e§lLartyHD, LeBaasti (((based by Tutorialwork)))",
				"§8[]===================================[]"
		)

		//Überprüft auf Bans aus dem Webinterface
		proxy.scheduler.schedule(this, {
			proxy.players.filter { it.uniqueId.isBanned }.forEach { it.sendBan() }
		}, 5, 5, TimeUnit.SECONDS)
	}

	private fun registerConfig() {
		if (!dataFolder.exists()) dataFolder.mkdirs()
		if (!configFile.exists()) {
			configFile.createNewFile()
			config.set("PREFIX", "&e&lBANS &8• &7")
			config.set("LAYOUT.BAN", "&8[]===================================[] \n\n &4&lDu wurdest GEBANNT \n\n &eGrund: §c§l%grund% \n\n&8[]===================================[]")
			config.set("LAYOUT.KICK", "&8[]===================================[] \n\n &e&lDu wurdest GEKICKT \n\n &eGrund: §c§l%grund% \n\n&8[]===================================[]")
			config.set("LAYOUT.TEMPBAN", "&8[]===================================[] \n\n &4&lDu wurdest temporär GEBANNT \n\n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n\n&8[]===================================[]")
			config.set("LAYOUT.MUTE", "&8[]===================================[] \n\n &4&lDu wurdest GEMUTET \n\n &eGrund: §c§l%grund% \n\n&8[]===================================[]")
			config.set("LAYOUT.TEMPMUTE", "&8[]===================================[] \n\n &4&lDu wurdest temporär GEMUTET \n\n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n\n&8[]===================================[]")
			config.set("LAYOUT.IPBAN", "&8[]===================================[] \n\n &4&lDeine IP-Adresse wurde GEBANNT \n\n &eGrund: §c§l%grund% \n\n&8[]===================================[]")
			config.set("LAYOUT.TEMPIPBAN", "&8[]===================================[] \n\n &4&lDeine IP-Adresse wurde temporär GEBANNT \n\n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n\n&8[]===================================[]")
			config.set("VPN.BLOCKED", true)
			config.set("VPN.KICK", true)
			config.set("VPN.KICKMSG", "&7Das benutzen einer &4VPN &7ist auf unserem Netzwerk &cUNTERSAGT")
			config.set("VPN.BAN", false)
			config.set("VPN.BANID", 0)
			config.set("VPN.WHITELIST", ipWhiteList + "8.8.8.8")
			config.set("VPN.APIKEY", "Go to https://proxycheck.io/dashboard and register with your email and enter here your API Key")
			config.set("REPORTS.ENABLED", true)
			config.set("REPORTS.REASONS", reportReasons + listOf("Hacking", "Verhalten", "Teaming", "TPA-Falle", "Werbung"))
			config.set("REPORTS.OFFLINEREPORTS", false)
			config.set("CHATLOG.ENABLED", true)
			config.set("CHATLOG.URL", "DeinServer.net/BanWebinterface/public/chatlog.php?id=")
			config.set("AUTOMUTE.ENABLED", false)
			config.set("AUTOMUTE.AUTOREPORT", true)
			//config.set("AUTOMUTE.AUTOREPORT.REASON", "Automatischer Report");
			config.set("AUTOMUTE.MUTEID", 0)
			config.set("AUTOMUTE.ADMUTEID", 0)
			config.set("BANTIME-INCREASE.ENABLED", true)
			config.set("BANTIME-INCREASE.PERCENTRATE", 50)
			saveConfig()
		}
		config.getStringList("REPORTS.REASONS").forEach { reportReasons.add(it.toUpperCase()) }
		config.getStringList("VPN.WHITELIST").forEach { ipWhiteList.add(it) }
		prefix = config.getString("PREFIX").translateColors()
		increaseBans = config.getBoolean("BANTIME-INCREASE.ENABLED")
		increaseValue = config.getInt("BANTIME-INCREASE.PERCENTRATE")
		if (config.getString("VPN.APIKEY").length == 27)
			APIKey = config.getString("VPN.APIKEY")
	}

	private fun registerBlackListConfig() {
		if (!blacklistFile.exists()) blacklistFile.createNewFile()
		blacklistConfig.apply {
			blacklist.addAll(getStringList("BLACKLIST"))
			adBlackList.addAll(getStringList("ADBLACKLIST"))
			adWhiteList.addAll(getStringList("ADWHITELIST").map { it.toUpperCase() })
		}
	}

	private fun registerMySQLConfig() {
		if (!mysqlFile.exists()) {
			mysqlFile.createNewFile()
			mysqlConfig.set("HOST", "localhost")
			mysqlConfig.set("DATENBANK", "Bans")
			mysqlConfig.set("USER", "root")
			mysqlConfig.set("PASSWORT", "deinpasswort")
			saveConfig(mysqlConfig, mysqlFile)
		}

		MySQLConnect.HOST = mysqlConfig.getString("HOST")
		MySQLConnect.DATABASE = mysqlConfig.getString("DATENBANK")
		MySQLConnect.USER = mysqlConfig.getString("USER")
		MySQLConnect.PASSWORD = mysqlConfig.getString("PASSWORT")
	}

	private fun registerMySQL() {
		registerMySQLConfig()
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
		arrayOf(Ban, UnBan, Kick, WebAccount, Check, IPBan, Blacklist, WebVerify, Support).forEach(::register)

		if (config.getBoolean("REPORTS.ENABLED")) {
			register(Report)
			register(Reports)
		}
		if (config.getBoolean("CHATLOG.ENABLED")) register(ChatLog)

	}

	private fun registerListeners() = arrayOf(Login, Chat, Quit).forEach(::register)

	private fun register(command: Command) = proxy.pluginManager.registerCommand(this, command)
	private fun register(listener: Listener) = proxy.pluginManager.registerListener(this, listener)

}