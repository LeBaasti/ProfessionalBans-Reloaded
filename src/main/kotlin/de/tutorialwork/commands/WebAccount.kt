package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.global.permissionPrefix
import de.tutorialwork.global.prefix
import de.tutorialwork.global.proxyServer
import de.tutorialwork.utils.*

class WebAccount : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (source is Player) {
            if (args.size <= 1) source.msg("$prefix/webaccount <create, delete> <Player> [Rank]")
            else {
                when (args[0].toLowerCase()) {
                    "create" -> {
                        if (args.size == 2) {
                            source.msg("${prefix}Du musst noch ein Rang des Accounts angeben ${RankType.values().joinToString { "${it.color}${it.name}§7, " }}")
                            return
                        }
                        val uuid = UUIDFetcher.getUUID(args[1]) ?: return
                        if (uuid.playerExists()) {
                            if (!uuid.webaccountExists) {
                                val targetPlayer = proxyServer.getPlayer(args[1])
                                val target = targetPlayer.get()
                                if (targetPlayer.isPresent) {
                                    val password = 7.randomString()
                                    val rankType = RankType.values().find { it.name == args[2].toUpperCase() } ?: return
                                    uuid.createWebAccount(rankType, password, source)
                                    source.msg("${prefix}Ein ${rankType.color}§l${rankType.name.toLowerCase().capitalize()} §7Account für §e§l${uuid.name} §7wurde §aerstellt")
                                    target.msg(
                                            "${prefix}§e§l${source.username} §7hat einen WebAccount für dich erstellt",
                                            "${prefix}Passwort: §c§l$password"
                                    )
                                } else source.msg("$prefix§e§l${args[1]} §7ist derzeit nicht online")
                            } else source.msg("${prefix}§cDieser Spieler hat bereits einen Zugang zum Webinterface")
                        } else source.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
                    }
                    "delete" -> {
                        val uuid = UUIDFetcher.getUUID(args[1]) ?: return
                        if (uuid.playerExists()) {
                            if (uuid.webaccountExists) {
                                uuid.deleteWebAccount()
                                source.msg("${prefix}Der Zugang von dem Spieler §e§l${uuid.name} §7wurde erfolgreich §agelöscht")
                                uuid.createLogEntry(source.uniqueId.toString(), ActionType.WebAccount("DEL_WEBACCOUNT"))
                            } else source.msg("${prefix}§cDieser Spieler hat keinen Zugang zum Webinterface")
                        } else source.msg("${prefix}§cDieser Spieler hat den Server noch nie betreten")
                    }
                    else -> source.msg("$prefix/webaccount <erstellen, löschen> <Spieler> [Rang]")
                }
            }
        } else source.msg("${prefix}Nur Spieler können §e§lWebAccounts §7erstellen/löschen")
    }

    override fun hasPermission(source: CommandSource, args: Array<out String>): Boolean = source.hasPermission("$permissionPrefix.commands.webaccount")


}