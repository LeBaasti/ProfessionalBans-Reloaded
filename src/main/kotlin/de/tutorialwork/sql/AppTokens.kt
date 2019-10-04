package de.tutorialwork.sql

import org.jetbrains.exposed.sql.Table

/**
 * Created on 04.10.2019 19:48.
 * @author Lars Artmann | LartyHD
 */
object AppTokens : Table() {

	var uuid = varchar("uuid", 36).primaryKey()
	var token = varchar("uuid", 555)

}