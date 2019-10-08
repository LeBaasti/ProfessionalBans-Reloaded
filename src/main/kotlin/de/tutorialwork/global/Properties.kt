package de.tutorialwork.global

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer


val version: String get() = instance.server.version.version
val proxyServer: ProxyServer get() = instance.server
val console: CommandSource get() = proxyServer.consoleCommandSource
val consoleName: String get() = "console"
val players: Collection<Player> get() = proxyServer.allPlayers

val CommandSource.executor get() = if (this is Player) uniqueId.toString() else consoleName
