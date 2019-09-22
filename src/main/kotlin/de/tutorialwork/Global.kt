package de.tutorialwork

import de.tutorialwork.main.Main
import de.tutorialwork.utils.MySQLConnect
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File

var prefix = "§e§lBANS §8• §7"
val noPerms get() = "$prefix§cDu hast keine Berechtigung diesen Befehl zu nutzen"

var openchats = mutableMapOf<ProxiedPlayer, String>()
var activechats = mutableMapOf<ProxiedPlayer, ProxiedPlayer>()

val dataFolder: File = instance.dataFolder
val mysqlFile = File(dataFolder, "mysql.yml")
val configFile = File(dataFolder, "config.yml")
val blacklistFile = File(dataFolder, "blacklist.yml")

val configProvider: ConfigurationProvider by lazy { ConfigurationProvider.getProvider(YamlConfiguration::class.java) }

val config: Configuration by lazy { configProvider.load(configFile) }
val mysqlConfig: Configuration by lazy { configProvider.load(mysqlFile) }
val blacklistConfig: Configuration by lazy { configProvider.load(blacklistFile) }

val version: String = instance.description.version

var APIKey: String? = null
lateinit var mysql: MySQLConnect

val proxyServer: ProxyServer get() = ProxyServer.getInstance()
val console: CommandSender get() = proxyServer.console
val consoleName: String get() = console.name
val players: Collection<ProxiedPlayer> get() = proxyServer.players

lateinit var instance: Main

var reportreasons = mutableListOf<String>()
var blacklist = mutableListOf<String>()
var adblacklist = mutableListOf<String>()
var adwhitelist = mutableListOf<String>()
var ipwhitelist = mutableListOf<String>()
var increaseBans = true

var increaseValue: Int = 50