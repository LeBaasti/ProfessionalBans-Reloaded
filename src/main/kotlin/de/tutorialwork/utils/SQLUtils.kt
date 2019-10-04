package de.tutorialwork.utils

import de.tutorialwork.sql.Log
import org.jetbrains.exposed.sql.insert
import java.util.*

/*
 * Created on 04.10.2019 18:54.
 * @author Lars Artmann | LartyHD
 */

fun UUID?.createLogEntry(byUUID: String?, type: ActionType) {
	Log.insert {
		it[uuid] = this@createLogEntry.toString()
		it[this.byUuid] = byUUID.toString()
		it[action] = type.reason
		it[note] = type.node.toString()
		it[date] = System.currentTimeMillis()
	}
}
