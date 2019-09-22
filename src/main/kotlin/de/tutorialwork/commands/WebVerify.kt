package de.tutorialwork.commands

import de.tutorialwork.console
import de.tutorialwork.prefix
import de.tutorialwork.utils.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class WebVerify(name: String) : Command(name) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender is ProxiedPlayer) {
            if (args.isEmpty()) {
                sender.msg("$prefix/webverify <Token>")
            } else {
                val uuid = sender.uniqueId
                if (uuid.webaccountExists) {
                    if (uuid.hasAuthToken) {
                        if (args[0].length == 25) {
                            if (uuid.authCode == args[0]) {
                                uuid.updateAuthStatus()
                                sender.msg("$prefix§a§lErfolgreich! §7Du kannst jetzt dein Passwort festlegen.")
                            } else {
                                sender.msg("$prefix§cDer eingegebene Token ist ungültig")
                            }
                        } else {
                            sender.msg("$prefix§cDer eingegebene Token ist ungültig")
                        }
                    } else {
                        sender.msg("$prefix§cEs wurde keine Verifizierungsanfrage von dir gefunden")
                    }
                } else {
                    sender.msg("$prefix§cDu hast keinen Account im Webinterface")
                }
            }
        } else {
            console.msg("$prefix§cDieser Befehl ist nur als Spieler nutzbar")
        }
    }
}