package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 19:47.
 * @author Lars Artmann | LartyHD
 */
object UnBans : Table() {

	val id = integer("id").autoIncrement().primaryKey()
	val uuid = varchar("uuid", 36)
	val fair = integer("fair")
	val message = varchar("message", 10000)
	val date = long("date")
	val status = integer("status")

}