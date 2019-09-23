package de.tutorialwork.global

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer

val version: String get() = instance.description.version
val proxyServer: ProxyServer get() = ProxyServer.getInstance()
val console: CommandSender get() = proxyServer.console
val consoleName: String get() = console.name
val players: Collection<ProxiedPlayer> get() = proxyServer.players

val CommandSender.executor get() = if (this is ProxiedPlayer) uniqueId.toString() else consoleName
