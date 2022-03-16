package ru.broom.telegram_notifier.service.`trait`

import java.sql.{Connection, DriverManager, ResultSet, Statement}


trait DerbyConnection {
  protected var connection: Connection = _
  protected var statement: Statement = _
  protected var resultSet: ResultSet = _

  protected def createConnectionIfClosed(): Unit = {
    if (connection == null || connection.isClosed) connection = DriverManager.getConnection("jdbc:derby:derby;create=true")
  }

  protected def closeAll(): Unit = {
    try {
      if (resultSet != null) resultSet.close()
      if (statement != null) statement.close()
      if (connection != null) connection.close()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}
