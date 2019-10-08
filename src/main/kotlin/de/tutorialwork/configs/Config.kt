package de.tutorialwork.configs

import net.darkdevelopers.darkbedrock.darkness.general.configs.Undercover
import net.darkdevelopers.darkbedrock.darkness.general.configs.default
import net.darkdevelopers.darkbedrock.darkness.general.configs.getValue

class Config(@get:Undercover val values: Map<String, Any?>) {

    //val count by values.default { 10 }
    val prefix by values.default { "&e&lBANS &8• &7" }

    val offlineReports by values.default { false }
    val layoutKick by values.default { "&8[]===================================[] \n &e&lDu wurdest GEKICKT \n &eGrund: §c§l%grund% \n&8[]===================================[]" }
    val layoutTempIpBan by values.default { "&8[]===================================[] \n &4&lDeine IP-Adresse wurde GEBANNT \n &eGrund: §c§l%grund% \n&8[]===================================[]" }
    val layoutIpBan by values.default { "&8[]===================================[] \n &4&lDeine IP-Adresse wurde temporär GEBANNT \n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n&8[]===================================[]" }
    val layoutBan by values.default { "&8[]===================================[] \n &4&lDu wurdest GEBANNT \n &eGrund: §c§l%grund% \n&8[]===================================[]" }
    val layoutTempBan by values.default { "&8[]===================================[] \n &4&lDu wurdest temporär GEBANNT \n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n&8[]===================================[]" }
    val layoutMute by values.default { "&8[]===================================[] \n &4&lDu wurdest GEMUTET \n &eGrund: §c§l%grund% \n&8[]===================================[]" }
    val layoutTempMute by values.default { "&8[]===================================[] \n &4&lDu wurdest temporär GEMUTET \n &eGrund: §c§l%grund% \n &eRestzeit: &c&l%dauer% \n&8[]===================================[]" }
    val autoMuteEnabled by values.default { false }
    val autoMuteID by values.default { 0 }
    val autoMuteAdID by values.default { 0 }
    val autoMuteAutoReport by values.default { false }
    val vpnBlocked by values.default { false }
    val vpnBan by values.default { false }
    val vpnBanID by values.default { 0 }
    val vpnKick by values.default { false }
    val vpnKickMessage by values.default { "&7Das benutzen einer &4VPN &7ist auf unserem Netzwerk &cUNTERSAGT" }
    val vpnWhitelist by values.default { listOf(" ") }
    val vpnAPIKey by values.default { "21087l-51m583-p0f79x-w78953" }
    val reportReasons by values.default { listOf(" ") }
    val bantimeIncreaseENABLED by values.default { true }
    val bantimeIncreasePercentage by values.default { 50 }
    val reportsEnabled by values.default { true }
    val chatlogEnabled by values.default { true }
    val chatlogUrl by values.default { "ComsicMC.de/BanWebinterface/public/chatlog.php?id=" }
}
