package de.tutorialwork.commands

import com.velocitypowered.api.command.Command
import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import de.tutorialwork.global.console
import de.tutorialwork.global.prefix
import de.tutorialwork.utils.*

class WebVerify : Command {
    override fun execute(source: CommandSource, args: Array<out String>) {
        if (source !is Player) {
            console.msg("${prefix}§cDieser Befehl ist nur als Spieler nutzbar")
            return
        }
        if (args.size == 1) {
            source.msg("$prefix/webverify <Token>")
            return
        }
        val uuid = source.uniqueId
        if (!uuid.webaccountExists) {
            source.msg("${prefix}§cDu hast keinen Account im Webinterface")
            return
        }
        if (!uuid.hasAuthToken) {
            source.msg("${prefix}§cEs wurde keine Verifizierungsanfrage von dir gefunden")
            return
        }
        if (args[0].length != 25 || uuid.authCode != args[0]) {
            source.msg("${prefix}§cDer eingegebene Token ist ungültig")
            return
        }
        uuid.updateAuthStatus()
        source.msg("${prefix}§a§lErfolgreich! §7Du kannst jetzt dein Passwort festlegen.")
    }


}