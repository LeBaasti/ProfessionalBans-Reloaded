package de.tutorialwork.listener

import de.tutorialwork.global.activeChats
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.msg
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

object Quit : Listener {

	@EventHandler
	fun onPlayerDisconnectEvent(event: PlayerDisconnectEvent) {
		val player = event.player ?: return
		if (!activeChats.containsKey(player) && !activeChats.containsValue(player)) return
		val messages = "${prefix}§event§l${player.name} §7hat den Support Chat §cbeeendet"
		activeChats[player]?.msg(messages)
		activeChats.remove(player)
		val find = activeChats.filter { it.value === player }
		find.keys.forEach { it.msg(messages) }
		find.forEach { activeChats.remove(it.key) }
	}

}