package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.prefix
import de.tutorialwork.utils.BanManager
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class WebVerify(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (args.isEmpty()) {
                sender.sendMessage(prefix + "/webverify <Token>")
            } else {
                val uuid = sender.uniqueId
                if (BanManager.webaccountExists(uuid)) {
                    if (BanManager.hasAuthToken(uuid)) {
                        if (args[0].length == 25) {
                            if (BanManager.getAuthCode(uuid) == args[0]) {
                                BanManager.updateAuthStatus(uuid)
                                sender.sendMessage(prefix + "§a§lErfolgreich! §7Du kannst jetzt dein Passwort festlegen.")
                            } else {
                                sender.sendMessage(prefix + "§cDer eingegebene Token ist ungültig")
                            }
                        } else {
                            sender.sendMessage(prefix + "§cDer eingegebene Token ist ungültig")
                        }
                    } else {
                        sender.sendMessage(prefix + "§cEs wurde keine Verifizierungsanfrage von dir gefunden")
                    }
                } else {
                    sender.sendMessage(prefix + "§cDu hast keinen Account im Webinterface")
                }
            }
        } else {
            console.sendMessage(prefix + "§cDieser Befehl ist nur als Spieler nutzbar")
        }
    }
}