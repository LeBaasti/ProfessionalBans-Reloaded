package de.tutorialwork.commands

import de.tutorialwork.noPerms
import de.tutorialwork.prefix
import de.tutorialwork.proxyServer
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import java.util.*

class WebAccount(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.webaccount")) {
                if (args.isEmpty() || args.size == 1) {
                    sender.msg("$prefix/webaccount <erstellen, löschen> <Spieler> [Rang]")
                } else {
                    if (args[0].equals("erstellen", ignoreCase = true)) {
                        if (args.size == 2) {
                            sender.msg(prefix + "Du musst noch ein Rang des Accounts angeben §4Admin§7, §cMod§7, §9Sup")
                            return
                        }
                        val uuid = UUIDFetcher.getUUID(args[1]) ?: return
                        if (uuid.playerExists()) {
                            if (!uuid.webaccountExists) {
                                val target = proxyServer.getPlayer(args[1])
                                if (target != null) {
                                    val rowPW = 7.randomString()
                                    val hash = BCrypt.hashpw(rowPW, BCrypt.gensalt())
                                    when {
                                        args[2].equals("Admin", ignoreCase = true) -> {
                                            uuid.createWebAccount(uuid.name, 3, hash)
                                            sender.msg(prefix + "Ein §4§lAdmin §7Account für §e§l" + uuid.name + " §7wurde §aerstellt")
                                        }
                                        args[2].equals("Mod", ignoreCase = true) -> {
                                            uuid.createWebAccount(uuid.name, 2, hash)
                                            sender.msg(prefix + "Ein §c§lMod §7Account für §e§l" + uuid.name + " §7wurde §aerstellt")
                                        }
                                        args[2].equals("Sup", ignoreCase = true) -> {
                                            uuid.createWebAccount(uuid.name, 1, hash)
                                            sender.msg(prefix + "Ein §9§lSup §7Account für §e§l" + uuid.name + " §7wurde §aerstellt")
                                        }
                                    }
                                    target.msg(prefix + "§e§l" + sender.name + " §7hat einen Webaccount für dich erstellt")
                                    target.msg(prefix + "Passwort: §c§l" + rowPW)
                                    LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), ActionType.Webaccount("ADD_WEBACCOUNT"))
                                } else {
                                    sender.msg(prefix + "§e§l" + args[1] + " §7ist derzeit nicht online")
                                }
                            } else {
                                sender.msg("$prefix§cDieser Spieler hat bereits einen Zugang zum Webinterface")
                            }
                        } else {
                            sender.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
                        }
                    } else if (args[0].equals("löschen", ignoreCase = true)) {
                        val uuid = UUIDFetcher.getUUID(args[1]) ?: return
                        if (uuid.playerExists()) {
                            if (uuid.webaccountExists) {
                                uuid.deleteWebAccount()
                                sender.msg(prefix + "Der Zugang von dem Spieler §e§l" + uuid.name + " §7wurde erfolgreich §agelöscht")
                                LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), ActionType.Webaccount("DEL_WEBACCOUNT"))
                            } else {
                                sender.msg("$prefix§cDieser Spieler hat keinen Zugang zum Webinterface")
                            }
                        } else {
                            sender.msg("$prefix§cDieser Spieler hat den Server noch nie betreten")
                        }
                    } else {
                        sender.msg("$prefix§cDiese Aktion ist nicht gültig")
                    }
                }
            } else {
                sender.msg(noPerms)
            }
        }
    }

    companion object {
        private val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz"
        private var rnd = Random()

        private fun Int.randomString(): String {
            val sb = StringBuilder(this)
            for (i in 0 until this)
                sb.append(AB[rnd.nextInt(AB.length)])
            return sb.toString()
        }
    }
}