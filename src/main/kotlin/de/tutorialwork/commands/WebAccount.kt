package de.tutorialwork.commands

import de.tutorialwork.main.Main
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import java.util.*

class WebAccount(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (sender.hasPermission("professionalbans.webaccount") || sender.hasPermission("professionalbans.*")) {
                if (args.isEmpty() || args.size == 1) {
                    sender.sendMessage(prefix + "/webaccount <erstellen, löschen> <Spieler> [Rang]")
                } else {
                    if (args[0].equals("erstellen", ignoreCase = true)) {
                        if (args.size == 2) {
                            sender.sendMessage(prefix + "Du musst noch ein Rang des Accounts angeben §4Admin§7, §cMod§7, §9Sup")
                            return
                        }
                        val uuid = UUIDFetcher.getUUID(args[1]) ?: return
                        if (BanManager.playerExists(uuid)) {
                            if (!BanManager.webaccountExists(uuid)) {
                                val target = ProxyServer.getInstance().getPlayer(args[1])
                                if (target != null) {
                                    val rowPW = randomString(7)
                                    val hash = BCrypt.hashpw(rowPW, BCrypt.gensalt())
                                    when {
                                        args[2].equals("Admin", ignoreCase = true) -> {
                                            BanManager.createWebAccount(uuid, uuid.name.toString(), 3, hash)
                                            sender.sendMessage(prefix + "Ein §4§lAdmin §7Account für §e§l" + uuid.name + " §7wurde §aerstellt")
                                        }
                                        args[2].equals("Mod", ignoreCase = true) -> {
                                            BanManager.createWebAccount(uuid, uuid.name.toString(), 2, hash)
                                            sender.sendMessage(prefix + "Ein §c§lMod §7Account für §e§l" + uuid.name + " §7wurde §aerstellt")
                                        }
                                        args[2].equals("Sup", ignoreCase = true) -> {
                                            BanManager.createWebAccount(uuid, uuid.name.toString(), 1, hash)
                                            sender.sendMessage(prefix + "Ein §9§lSup §7Account für §e§l" + uuid.name + " §7wurde §aerstellt")
                                        }
                                    }
                                    target.sendMessage(prefix + "§e§l" + sender.name + " §7hat einen Webaccount für dich erstellt")
                                    target.sendMessage(prefix + "Passwort: §c§l" + rowPW)
                                    LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "ADD_WEBACCOUNT", args[2])
                                } else {
                                    sender.sendMessage(prefix + "§e§l" + args[1] + " §7ist derzeit nicht online")
                                }
                            } else {
                                sender.sendMessage(prefix + "§cDieser Spieler hat bereits einen Zugang zum Webinterface")
                            }
                        } else {
                            sender.sendMessage(prefix + "§cDieser Spieler hat den Server noch nie betreten")
                        }
                    } else if (args[0].equals("löschen", ignoreCase = true)) {
                        val uuid = UUIDFetcher.getUUID(args[1]) ?: return
                        if (BanManager.playerExists(uuid)) {
                            if (BanManager.webaccountExists(uuid)) {
                                BanManager.deleteWebAccount(uuid)
                                sender.sendMessage(prefix + "Der Zugang von dem Spieler §e§l" + uuid.name + " §7wurde erfolgreich §agelöscht")
                                LogManager.createEntry(uuid.toString(), sender.uniqueId.toString(), "DEL_WEBACCOUNT", null)
                            } else {
                                sender.sendMessage(prefix + "§cDieser Spieler hat keinen Zugang zum Webinterface")
                            }
                        } else {
                            sender.sendMessage(prefix + "§cDieser Spieler hat den Server noch nie betreten")
                        }
                    } else {
                        sender.sendMessage(prefix + "§cDiese Aktion ist nicht gültig")
                    }
                }
            } else {
                sender.sendMessage(Main.noPerms)
            }
        }
    }

    companion object {
        private val AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz"
        private var rnd = Random()

        private fun randomString(length: Int): String {
            val sb = StringBuilder(length)
            for (i in 0 until length)
                sb.append(AB[rnd.nextInt(AB.length)])
            return sb.toString()
        }
    }
}