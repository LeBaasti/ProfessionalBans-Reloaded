package de.tutorialwork.utils

import de.tutorialwork.global.console
import de.tutorialwork.global.prefix
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

class MySQLConnect(host: String, database: String, user: String, password: String) {

	private var con: Connection? = null

	init {
		HOST = host
		DATABASE = database
		USER = user
		PASSWORD = password

		connect()
	}

	private fun connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://$HOST:3306/$DATABASE?autoReconnect=true", USER, PASSWORD)
			console.msg("${prefix}§aDie Verbindung mit der MySQL Datenbank wurde erfolgreich hergestellt")
		} catch (e: SQLException) {
			console.msg(prefix + "§cDie Verbindung mit der MySQL Datenbank ist fehlgeschlagen: §4" + e.message)
		}
	}

	fun close() {
		con?.close()
	}

	fun update(qry: String) {
		try {
			val st = con!!.createStatement()
			st.executeUpdate(qry)
			st.close()
		} catch (e: SQLException) {
			connect()
			System.err.println(e)
		}
	}

	fun query(qry: String): ResultSet? {
		var rs: ResultSet? = null

		try {
			val st = con!!.createStatement()
			rs = st.executeQuery(qry)
		} catch (e: SQLException) {
			connect()
			System.err.println(e)
		}

		return rs
	}

	companion object {

		lateinit var HOST: String
		lateinit var DATABASE: String
		lateinit var USER: String
		lateinit var PASSWORD: String
	}
}