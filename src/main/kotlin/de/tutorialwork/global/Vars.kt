package de.tutorialwork.global

import com.velocitypowered.api.proxy.Player
import de.tutorialwork.Main
import de.tutorialwork.utils.MySQLConnect

var prefix = "§e§lBANS §8• §7"
val noPerms get() = "${prefix}§cDu hast keine Berechtigung diesen Befehl zu nutzen"

val instance = Main.instance
lateinit var mysql: MySQLConnect
var APIKey: String? = null

var openChats = mutableMapOf<Player, String>()
var activeChats = mutableMapOf<Player, Player>()
var reportReasons = mutableListOf<String>()

var blacklist = mutableListOf<String>()
var adBlackList = mutableListOf<String>()
var adWhiteList = mutableListOf<String>()

var ipWhiteList = mutableListOf<String>()
var increaseBans = true

var increaseValue: Int = 50