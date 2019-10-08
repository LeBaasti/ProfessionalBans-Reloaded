package de.tutorialwork.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import de.tutorialwork.global.activeChats
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.msg

class Quit {

    @Subscribe
    fun onDisconnectEvent(event: DisconnectEvent) {
        val player = event.player ?: return
        if (!activeChats.containsKey(player) && !activeChats.containsValue(player)) return
        val messages = "${prefix}§event§l${player.username} §7hat den Support Chat §cbeeendet"
        activeChats[player]?.msg(messages)
        activeChats.remove(player)
        val find = activeChats.filter { it.value === player }
        find.keys.forEach { it.msg(messages) }
        find.forEach { activeChats.remove(it.key) }
    }

}