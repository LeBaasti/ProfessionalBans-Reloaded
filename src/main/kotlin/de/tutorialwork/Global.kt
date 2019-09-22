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

val configFile = File(Main.instance.dataFolder, "config.yml")
val configProvider: ConfigurationProvider by lazy { ConfigurationProvider.getProvider(YamlConfiguration::class.java) }
val config: Configuration by lazy { configProvider.load(configFile) }

lateinit var mysql: MySQLConnect

val console: CommandSender get() = ProxyServer.getInstance().console
val consoleName: String get() = console.name
val proxyServer: ProxyServer get() = ProxyServer.getInstance()
val players: Collection<ProxiedPlayer> get() = proxyServer.players