package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.prefix
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import java.util.*

class SupportChat(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (args.isNotEmpty()) {
                if (args[0].equals("end", ignoreCase = true)) {
                    if (activechats.containsValue(sender) || activechats.containsKey(sender)) {
                        for (key in activechats.keys) {
                            //Key has started the support chat
                            if (key === sender) {
                                activechats[sender]?.sendMessage(prefix + "§e§l" + sender.name + " §7hat den Support Chat §cbeeendet")
                                activechats.remove(key)
                            } else {
                                key.sendMessage(prefix + "§e§l" + sender.name + " §7hat den Support Chat §cbeeendet")
                                activechats.remove(key)
                            }
                        }
                        sender.sendMessage(prefix + "§cDu hast den Support Chat beendet")
                        return
                    } else {
                        sender.sendMessage(prefix + "§cDu hast derzeit keinen offenen Support Chat")
                        return
                    }
                }
            }
            if (sender.hasPermission("professionalbans.supportchat") || sender.hasPermission("professionalbans.*")) {
                //Team Member
                if (args.isNotEmpty()) {
                    for (all in ProxyServer.getInstance().getPlayers()) {
                        if (all.getName() == args[0]) {
                            if (openchats.containsKey(all)) {
                                activechats[all] = sender
                                openchats.remove(all)
                                all.sendMessage(prefix + "§e§l" + sender.name + " §7ist jetzt mit dir im Support Chat")
                                all.sendMessage(prefix + "§8§oDu kannst in den Support Chat schreiben in dem du einfach eine normale Nachricht schreibst")
                                all.sendMessage(prefix + "§8§oDu kannst den Support Chat mit §7§o/support end §8§obeenden")
                                sender.sendMessage(prefix + "§e§l" + all.getName() + " §7ist jetzt im Support Chat mit dir")
                                sender.sendMessage(prefix + "§8§oDu kannst den Support Chat mit §7§o/support end §8§obeenden")
                            } else {
                                sender.sendMessage(prefix + "§cDiese Anfrage ist ausgelaufen")
                            }
                        }
                    }
                } else {
                    if (openchats.size != 0) {
                        sender.sendMessage("§8[]===================================[]")
                        var i = 0
                        for (key in SupportChat.openchats.keys) {
                            sender.sendMessage("§e§l" + key + " §8• §9" + SupportChat.openchats[key])
                            val tc = TextComponent()
                            tc.text = "§aSupport Chat starten"
                            tc.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support $key")
                            tc.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("§7Klicken um den Chat mit §e§l$key §7zu starten").create())
                            sender.sendMessage(tc)
                            i++
                        }
                        sender.sendMessage("§8[]===================================[]")
                        sender.sendMessage(prefix + "Es sind derzeit §e§l" + i + " Support Chats §7Anfragen §aoffen")
                    } else {
                        sender.sendMessage(prefix + "§cDerzeit sind keine Support Chats Anfragen offen")
                    }
                }
            } else {
                //Normal Member
                if (args.isEmpty()) {
                    sender.sendMessage(prefix + "Wenn du den §e§lSupport Chat §7starten möchtest gebe ein §8§oBetreff §7ein")
                    sender.sendMessage(prefix + "Möchtest du eine Anfrage abbrechen? §8§o/support cancel")
                } else {
                    var supporter = 0
                    for (all in ProxyServer.getInstance().getPlayers()) {
                        if (all.hasPermission("professionalbans.supportchat") || all.hasPermission("professionalbans.*")) {
                            supporter++
                        }
                    }
                    if (!args[0].equals("cancel", ignoreCase = true)) {
                        var subject = ""
                        for (i in args.indices) {
                            subject = subject + " " + args[i]
                        }
                        if (!openchats.containsKey(sender)) {
                            if (supporter > 0) {
                                openchats[sender] = subject
                                sender.sendMessage(prefix + "Du hast eine Anfrage mit dem Betreff §e§l" + subject + " §7gestartet")
                                for (all in ProxyServer.getInstance().getPlayers()) {
                                    if (all.hasPermission("professionalbans.supportchat") || all.hasPermission("professionalbans.*")) {
                                        all.sendMessage(prefix + "§e§l" + sender.name + " §7benötigt Support §8(§e§o" + subject + "§8)")
                                        val tc = TextComponent()
                                        tc.text = "§aSupport Chat starten"
                                        tc.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support " + sender.name)
                                        tc.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("§7Klicken um den Chat mit §e§l" + sender.name + " §7zu starten").create())
                                        all.sendMessage(tc)
                                    }
                                }
                            } else {
                                sender.sendMessage(prefix + "§cDerzeit ist kein Supporter online")
                            }
                        } else {
                            sender.sendMessage(prefix + "Du hast bereits eine §e§lSupport Chat §7Anfrage gestellt")
                            sender.sendMessage(prefix + "Möchtest du diese Anfrage §cabbrechen §7benutze §c§l/support cancel")
                        }
                    } else {
                        if (!openchats.containsKey(sender)) {
                            openchats.remove(sender)
                            sender.sendMessage(prefix + "Deine Anfrage wurde erfolgreich §cgelöscht")
                        } else {
                            sender.sendMessage(prefix + "§cDu hast derzeit keine offene Anfrage")
                        }
                    }
                }
            }
        } else {
            console.sendMessage(prefix + "Der §e§lSupport Chat §7ist nur als Spieler verfügbar")
        }
    }

    companion object {

        var openchats = HashMap<ProxiedPlayer, String>()
        var activechats = HashMap<ProxiedPlayer, ProxiedPlayer>()
    }
}