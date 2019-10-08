package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.global.*
import de.tutorialwork.utils.msg
import net.kyori.text.TextComponent
import net.kyori.text.event.ClickEvent
import net.kyori.text.event.HoverEvent

class Support : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (source is Player) {
            if (args.isNotEmpty()) {
                if (args[0].equals("end", ignoreCase = true)) {
                    if (activeChats.containsValue(source) || activeChats.containsKey(source)) {
                        for (key in activeChats.keys) {
                            //Key has started the support chat
                            if (key === source) {
                                activeChats[source]?.msg("$prefix§e§l${source.username} §7hat den Support Chat §cbeeendet")
                                activeChats.remove(key)
                            } else {
                                key.msg("$prefix§e§l${source.username} §7hat den Support Chat §cbeeendet")
                                activeChats.remove(key)
                            }
                        }
                        source.msg("${prefix}§cDu hast den Support Chat beendet")
                        return
                    } else {
                        source.msg("${prefix}§cDu hast derzeit keinen offenen Support Chat")
                        return
                    }
                }
            }
            if (source.hasPermission("professionalbans.supportchat")) {
                //Team Member
                if (args.isNotEmpty()) {
                    for (all in players) {
                        if (all.username == args[0]) {
                            if (openChats.containsKey(all)) {
                                activeChats[all] = source
                                openChats.remove(all)
                                all.msg("""${prefix}§e§l${source.username} §7ist jetzt mit dir im Support Chat""")
                                all.msg("${prefix}§8§oDu kannst in den Support Chat schreiben in dem du einfach eine normale Nachricht schreibst")
                                all.msg("${prefix}§8§oDu kannst den Support Chat mit §7§o/support end §8§obeenden")
                                source.msg("""${prefix}§e§l${all.username} §7ist jetzt im Support Chat mit dir""")
                                source.msg("${prefix}§8§oDu kannst den Support Chat mit §7§o/support end §8§obeenden")
                            } else {
                                source.msg("${prefix}§cDiese Anfrage ist ausgelaufen")
                            }
                        }
                    }
                } else {
                    if (openChats.isNotEmpty()) {
                        source.msg("§8[]===================================[]")
                        var i = 0
                        for (key in openChats.keys) {
                            source.msg("""§e§l$key §8• §9${openChats[key]}""")
                            val tc = TextComponent.builder()
                            tc.content("§aSupport Chat starten")
                            tc.clickEvent(ClickEvent.runCommand("/support $key"))
                            tc.hoverEvent(HoverEvent.showText(TextComponent.of("§7Klicken um den Chat mit §e§l$key §7zu starten")))
                            source.sendMessage(tc.build())
                            i++
                        }
                        source.msg("§8[]===================================[]")
                        source.msg("""${prefix}Es sind derzeit §e§l$i Support Chats §7Anfragen §aoffen""")
                    } else {
                        source.msg("${prefix}§cDerzeit sind keine Support Chats Anfragen offen")
                    }
                }
            } else {
                //Normal Member
                if (args.isEmpty()) {
                    source.msg("${prefix}Wenn du den §e§lSupport Chat §7starten möchtest gebe ein §8§oBetreff §7ein")
                    source.msg("${prefix}Möchtest du eine Anfrage abbrechen? §8§o/support cancel")
                } else {
                    var supporter = 0
                    for (all in players) {
                        if (all.hasPermission("professionalbans.supportchat") || all.hasPermission("professionalbans.*")) {
                            supporter++
                        }
                    }
                    if (!args[0].equals("cancel", ignoreCase = true)) {
                        val subject = args.drop(1).joinToString { " " }
                        if (!openChats.containsKey(source)) {
                            if (supporter > 0) {
                                openChats[source] = subject
                                source.msg("${prefix}Du hast eine Anfrage mit dem Betreff §e§l$subject §7gestartet")
                                for (all in players) {
                                    if (all.hasPermission("professionalbans.supportchat")) {
                                        all.msg("$prefix§e§l${source.username} §7benötigt Support §8(§e§o$subject§8)")
                                        val tc = TextComponent.builder()
                                        tc.content("§aSupport Chat starten")
                                        tc.clickEvent(ClickEvent.runCommand("/support ${source.username}"))
                                        tc.hoverEvent(HoverEvent.showText(TextComponent.of("""§7Klicken um den Chat mit §e§l${source.username} 
                                            |§7zu starten""".trimMargin())))
                                        all.sendMessage(tc.build())
                                    }
                                }
                            } else source.msg("${prefix}§cDerzeit ist kein Supporter online")
                        } else {
                            source.msg(prefix + "Du hast bereits eine §e§lSupport Chat §7Anfrage gestellt")
                            source.msg(prefix + "Möchtest du diese Anfrage §cabbrechen §7benutze §c§l/support cancel")
                        }
                    } else {
                        if (!openChats.containsKey(source)) {
                            openChats.remove(source)
                            source.msg(prefix + "Deine Anfrage wurde erfolgreich §cgelöscht")
                        } else source.msg("${prefix}§cDu hast derzeit keine offene Anfrage")
                    }
                }
            }
        } else console.msg(prefix + "Der §e§lSupport Chat §7ist nur als Spieler verfügbar")
    }
}