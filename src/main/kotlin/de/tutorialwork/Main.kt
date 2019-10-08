package de.tutorialwork

import com.google.inject.Inject
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import de.tutorialwork.commands.*
import de.tutorialwork.configs.blacklistConfig
import de.tutorialwork.configs.config
import de.tutorialwork.configs.mySQLConfig
import de.tutorialwork.configs.setValue
import de.tutorialwork.global.*
import de.tutorialwork.listener.Chat
import de.tutorialwork.listener.Login
import de.tutorialwork.listener.Quit
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.configs.createConfigs
import java.nio.file.Path
import java.util.concurrent.TimeUnit

@Plugin(
        id = "professionalbansreloaded",
        name = "ProfessionalBans-Reloaded",
        version = "1.0",
        description = "",
        url = "",
        authors = ["LeBaasti"]
)
class Main @Inject constructor(
        val server: ProxyServer,
        private val commandManager: CommandManager,
        private val eventManager: EventManager,
        @DataDirectory
        val path: Path
) {

    init {
        instance = this
    }

    @Subscribe
    fun onProxyInitializeEvent(event: ProxyInitializeEvent) {
        registerConfig()
        registerBlackListConfig()

        registerMySQL()

        registerCommands()
        registerListeners()

        setOf(::config).createConfigs(path.toFile())
        setOf(::blacklistConfig).createConfigs(path.toFile())
        setOf(::mySQLConfig).createConfigs(path.toFile())

        //Konsolen Nachricht über das Plugin
        server.consoleCommandSource.msg(
                "§8[]===================================[]",
                "§e§lProfessionalBans §7§oReloaded §8| §7Version: §c$version",
                "§7Developer: §e§lLartyHD, LeBaasti (((based by Tutorialwork)))",
                "§8[]===================================[]"
        )

        //Überprüft auf Bans aus dem Webinterface
        server.scheduler.buildTask(this) {
            server.allPlayers.filter { it.uniqueId.isBanned }.forEach { it.sendBan() }
        }.repeat(5, TimeUnit.SECONDS)
    }

    private fun registerConfig() {
        config.reportReasons.forEach { reportReasons.add(it.toUpperCase()) }
        config.vpnWhitelist.forEach { ipWhiteList.add(it) }
        prefix = config.prefix.translateColors()
        increaseBans = config.bantimeIncreaseENABLED
        increaseValue = config.bantimeIncreasePercentage
        if (config.vpnAPIKey.length == 27)
            APIKey = config.vpnAPIKey
    }

    private fun registerBlackListConfig() {
        if (!blacklistFile.exists()) blacklistFile.createNewFile()
        blacklistConfig.apply {
            blacklist.addAll(blackList)
            adBlackList.addAll(adblackList)
            adWhiteList.addAll(adwhiteList.map { it.toUpperCase() })
        }
    }

    private fun registerMySQLConfig() {
        if (!mysqlFile.exists()) {
            mysqlFile.createNewFile()
            setValue(mySQLConfig::host, " ")
            setValue(mySQLConfig::datenbank, " ")
            setValue(mySQLConfig::user, " ")
            setValue(mySQLConfig::password, " ")
        }
        mySQLConfig.apply {
            MySQLConnect.HOST = host
            MySQLConnect.DATABASE = datenbank
            MySQLConnect.USER = user
            MySQLConnect.PASSWORD = password
        }
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
        commandManager.apply {
            register(Ban(), "ban")
            register(UnBan(), "unban")
            register(Kick(), "kick")
            register(WebAccount(), "webaccount")
            register(Check(), "check")
            register(IPBan(), "ipban")
            register(Blacklist(), "blacklist")
            register(WebVerify(), "webverify")
            register(Support(), "support")
        }

        if (config.reportsEnabled) {
            commandManager.register(Report(), "report")
            commandManager.register(Reports(), "reports")
        }
        if (config.chatlogEnabled) commandManager.register(ChatLog(), "chatlog")

    }

    private fun registerListeners() {
        eventManager.register(this, Chat)
        eventManager.register(this, Login())
        eventManager.register(this, Quit())
    }


    companion object {
        lateinit var instance: Main
    }

}