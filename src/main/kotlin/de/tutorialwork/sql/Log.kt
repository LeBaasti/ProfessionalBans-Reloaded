package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 18:54.
 * @author Lars Artmann | LartyHD
 */
object Log : Table() {

	val id = integer("id").autoIncrement().primaryKey()
	val uuid = varchar("uuid", 36)
	val byUuid = varchar("by_uuid", 36)
	val action = varchar("action", 255)
	val note = varchar("note", 255)
	val date = long("date")

}