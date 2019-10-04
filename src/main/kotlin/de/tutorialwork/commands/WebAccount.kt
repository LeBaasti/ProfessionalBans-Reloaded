package de.tutorialwork.commands

import de.tutorialwork.global.prefix
import de.tutorialwork.global.proxyServer
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

object WebAccount : Command(simpleName<WebAccount>()) {

    override fun execute(sender: CommandSender, args: Array<String>) = sender.hasPermission(name) {
        if (sender is ProxiedPlayer) {
            if (args.size <= 1) sender.msg("$prefix/webaccount <create, delete> <Player> [Rank]")
            else {
                when (args[0].toLowerCase()) {
                    "create" -> {
                        if (args.size == 2) {
                            sender.msg("${prefix}Du musst noch ein Rang des Accounts angeben ${RankType.values().joinToString { "${it.color}${it.name}§7, " }}")
                            return
                        }
                        val uuid = UUIDFetcher.getUUID(args[1]) ?: return
                        if (uuid.playerExists()) {
                            if (!uuid.webaccountExists) {
                                val target = proxyServer.getPlayer(args[1])
                                if (target != null) {
                                    val password = 7.randomString()
                                    val rankType = RankType.values().find { it.name == args[2].toUpperCase() } ?: return
                                    uuid.createWebAccount(rankType, password, sender)
                                    sender.msg("${prefix}Ein ${rankType.color}§l${rankType.name.toLowerCase().capitalize()} §7Account für §e§l${uuid.name} §7wurde §aerstellt")
                                    target.msg(
                                            "${prefix}§e§l${sender.name} §7hat einen WebAccount für dich erstellt",
                                            "${prefix}Passwort: §c§l$password"
                                    )
                                } else sender.msg("$prefix§e§l${args[1]} §7ist derzeit nicht online")
                            } else sender.msg("${prefix}§cDieser Spieler hat bereits einen Zugang zum Webinterface")
                        } else sender.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
                    }
                    "delete" -> {
                        val uuid = UUIDFetcher.getUUID(args[1]) ?: return
                        if (uuid.playerExists()) {
                            if (uuid.webaccountExists) {
                                uuid.deleteWebAccount()
                                sender.msg("${prefix}Der Zugang von dem Spieler §e§l${uuid.name} §7wurde erfolgreich §agelöscht")
                                LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), ActionType.WebAccount("DEL_WEBACCOUNT"))
                            } else sender.msg("${prefix}§cDieser Spieler hat keinen Zugang zum Webinterface")
                        } else sender.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
                    }
                    else -> sender.msg("$prefix/webaccount <erstellen, löschen> <Spieler> [Rang]")
                }
            }
        } else sender.msg("${prefix}Nur Spieler können §e§lWebAccounts §7erstellen/löschen")
    }

}