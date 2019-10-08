package de.tutorialwork.configs

import net.darkdevelopers.darkbedrock.darkness.general.configs.Undercover
import net.darkdevelopers.darkbedrock.darkness.general.configs.default

class MySQLConfig(@get:Undercover val values: Map<String, Any?>) {
    val host by values.default { " " }
    val datenbank by values.default { " " }
    val user by values.default { " " }
    val password by values.default { " " }
}