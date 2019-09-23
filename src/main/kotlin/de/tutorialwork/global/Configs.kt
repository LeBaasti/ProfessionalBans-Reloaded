package de.tutorialwork.global

import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File

val dataFolder: File = instance.dataFolder
val mysqlFile = File(dataFolder, "mysql.yml")
val configFile = File(dataFolder, "config.yml")
val blacklistFile = File(dataFolder, "blacklist.yml")

val configProvider: ConfigurationProvider by lazy { ConfigurationProvider.getProvider(YamlConfiguration::class.java) }

val config: Configuration by lazy { configProvider.load(configFile) }
val mysqlConfig: Configuration by lazy { configProvider.load(mysqlFile) }
val blacklistConfig: Configuration by lazy { configProvider.load(blacklistFile) }