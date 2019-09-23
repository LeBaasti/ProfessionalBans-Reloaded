package de.tutorialwork.listener

import de.tutorialwork.global.activechats
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.msg
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

object Quit : Listener {

    @EventHandler
    fun onPlayerDisconnectEvent(event: PlayerDisconnectEvent) {
        val player = event.player
        if (!activechats.containsKey(player) && !activechats.containsValue(player)) return
        for (key in activechats.keys) {
            //Key has started the support chat
            if (key === player) activechats[player]?.msg("${prefix}§event§l${player.name} §7hat den Support hat §cbeeendet")
            else key.msg("${prefix}§event§l${player.name} §7hat den Support Chat §cbeeendet")
            activechats.remove(key)
        }
    }

}