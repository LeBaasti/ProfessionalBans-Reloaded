package de.tutorialwork.listener

import de.tutorialwork.commands.SupportChat
import de.tutorialwork.prefix
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

object Quit : Listener {

    @EventHandler
    fun onPlayerDisconnectEvent(event: PlayerDisconnectEvent) {
        val player = event.player
        if (SupportChat.activechats.containsKey(player) || SupportChat.activechats.containsValue(player)) {
            for (key in SupportChat.activechats.keys) {
                //Key has started the support chat
                if (key === player) {
                    SupportChat.activechats[player]?.sendMessage(prefix + "§event§l" + player.name + " §7hat den Support hat §cbeeendet")
                    SupportChat.activechats.remove(player)
                } else {
                    key.sendMessage(prefix + "§event§l" + player.name + " §7hat den Support Chat §cbeeendet")
                    SupportChat.activechats.remove(key)
                }
            }
        }
    }

}