package de.tutorialwork

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer

val console: CommandSender get() = ProxyServer.getInstance().console

