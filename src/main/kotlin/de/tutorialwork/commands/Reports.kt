package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.main.Main
import de.tutorialwork.utils.BanManager
import de.tutorialwork.utils.LogManager
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class Reports(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.reports") || sender.hasPermission("professionalbans.*")) {
                if (args.isEmpty()) {
                    if (BanManager.countOpenReports() != 0) {
                        sender.sendMessage("§8[]===================================[]")
                        sender.sendMessage("§e§loffene Reports §7(§8" + BanManager.countOpenReports() + "§7)")
                        var offline = 0
                        for (element in BanManager.iDsFromOpenReports) {
                            val target = ProxyServer.getInstance().getPlayer(BanManager.getNameByReportID(element))
                            if (target != null) {
                                val tc = TextComponent()
                                tc.text = "§e§l" + target.name + " §7gemeldet wegen §c§l " +
                                        BanManager.getReasonByReportID(element) + " §8| §7Online auf §e§l" + target.server.info.name
                                tc.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports jump " + target.name + " " + element)
                                tc.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        ComponentBuilder("§7Klicken um §e§l" + target.name + " §7nachzuspringen").create())
                                sender.sendMessage(tc)
                            } else {
                                offline++
                            }
                        }
                        if (offline != 0) {
                            sender.sendMessage("§4§o$offline Reports §7§ovon Spieler die offline sind ausgeblendet")
                        }
                        sender.sendMessage("§8[]===================================[]")
                    } else {
                        sender.sendMessage(Main.prefix + "§cEs sind derzeit keine Reports offen")
                    }
                } else if (args[0].equals("jump", ignoreCase = true)) {
                    val target = ProxyServer.getInstance().getPlayer(args[1])
                    if (target != null) {
                        sender.connect(target.server.info)
                        val id = Integer.parseInt(args[2])
                        BanManager.setReportDone(id)
                        BanManager.setReportTeamUUID(id, sender.uniqueId.toString())
                        sender.sendMessage(Main.prefix + "Du hast den Report von §e§l" + BanManager.getNameByReportID(id) + " §7wegen §c§l" + BanManager.getReasonByReportID(id) + " §aangenommen")
                        LogManager.createEntry(sender.uniqueId.toString(), null, "REPORT_ACCEPT", id.toString())
                    } else {
                        sender.sendMessage(Main.prefix + "§cDieser Spieler ist nicht mehr online")
                    }
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        } else {
            console.sendMessage(Main.prefix + "§e§lReports §7sind nur als Spieler verfügbar")
        }
    }
}