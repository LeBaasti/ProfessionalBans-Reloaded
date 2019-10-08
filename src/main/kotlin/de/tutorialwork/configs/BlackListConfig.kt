package de.tutorialwork.configs

import net.darkdevelopers.darkbedrock.darkness.general.configs.Undercover
import net.darkdevelopers.darkbedrock.darkness.general.configs.default

class BlackListConfig(@get:Undercover val values: Map<String, Any?>) {
    val blackList by values.default { mutableListOf("cock") }
    val adblackList by values.default { mutableListOf("skypotion.eu") }
    val adwhiteList by values.default { mutableListOf("cosmicmc.de") }
}