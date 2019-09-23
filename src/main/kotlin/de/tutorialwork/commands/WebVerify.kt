package de.tutorialwork.commands

import de.tutorialwork.global.console
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*
import net.darkdevelopers.darkbedrock.darkness.general.functions.simpleName
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

object WebVerify : Command(simpleName<WebVerify>()) {

    override fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is ProxiedPlayer) {
            console.msg("${prefix}§cDieser Befehl ist nur als Spieler nutzbar")
            return
        }
        if (args.size == 1) {
            sender.msg("$prefix/${name.toLowerCase()} <Token>")
            return
        }
        val uuid = sender.uniqueId
        if (!uuid.webaccountExists) {
            sender.msg("${prefix}§cDu hast keinen Account im Webinterface")
            return
        }
        if (!uuid.hasAuthToken) {
            sender.msg("${prefix}§cEs wurde keine Verifizierungsanfrage von dir gefunden")
            return
        }
        if (args[0].length != 25 || uuid.authCode != args[0]) {
            sender.msg("${prefix}§cDer eingegebene Token ist ungültig")
            return
        }
        uuid.updateAuthStatus()
        sender.msg("${prefix}§a§lErfolgreich! §7Du kannst jetzt dein Passwort festlegen.")
    }

}