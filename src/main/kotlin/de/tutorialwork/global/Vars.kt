package de.tutorialwork.global

import de.tutorialwork.utils.MySQLConnect
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Plugin

var prefix = "§e§lBANS §8• §7"
val noPerms get() = "${prefix}§cDu hast keine Berechtigung diesen Befehl zu nutzen"

lateinit var instance: Plugin
lateinit var mysql: MySQLConnect
var APIKey: String? = null

var openChats = mutableMapOf<ProxiedPlayer, String>()
var activeChats = mutableMapOf<ProxiedPlayer, ProxiedPlayer>()
var reportReasons = mutableListOf<String>()

var blacklist = mutableListOf<String>()
var adBlackList = mutableListOf<String>()
var adWhiteList = mutableListOf<String>()

var ipWhiteList = mutableListOf<String>()
var increaseBans = true

var increaseValue: Int = 50