package de.tutorialwork.global

import java.io.File

val dataFolder: File = instance.path.toFile()
val mysqlFile = File(dataFolder, "mysql.yml")
val configFile = File(dataFolder, "config.yml")
val blacklistFile = File(dataFolder, "blacklist.yml")

/*
val configProvider: Configu by lazy { ConfigurationProvider.getProvider(YamlConfiguration::class.java) }

val config: Configuration by lazy { configProvider.load(configFile) }
val mysqlConfig: Configuration by lazy { configProvider.load(mysqlFile) }
val blacklistConfig: Configuration by lazy { configProvider.load(blacklistFile) }*/
