package de.tutorialwork.global

import de.tutorialwork.Main
import de.tutorialwork.utils.MySQLConnect
import net.md_5.bungee.api.connection.ProxiedPlayer

var prefix = "§e§lBANS §8• §7"
val noPerms get() = "${prefix}§cDu hast keine Berechtigung diesen Befehl zu nutzen"

lateinit var instance: Main
lateinit var mysql: MySQLConnect
var APIKey: String? = null

var openchats = mutableMapOf<ProxiedPlayer, String>()
var activechats = mutableMapOf<ProxiedPlayer, ProxiedPlayer>()
var reportreasons = mutableListOf<String>()
var blacklist = mutableListOf<String>()
var adblacklist = mutableListOf<String>()
var adwhitelist = mutableListOf<String>()
var ipwhitelist = mutableListOf<String>()
var increaseBans = true

var increaseValue: Int = 50