package com.deweyvm.whatever.server.db

import java.sql._

class DbConnection {
  try{

    Class.forName("org.postgresql.Driver")
  } catch {
    case e:ClassNotFoundException =>
      e.printStackTrace()
  }
  val url = "jdbc:postgresql://dogue.in/testdb"
  val user = "dogue"
  val pass = "wowsuch"
  var con:Connection = null
  var st:Statement = null
  var rs:ResultSet = null

  try {
    con = DriverManager.getConnection(url, user, pass)
    st = con.createStatement()
    rs = st.executeQuery("SELECT VERSION()")
  } catch {
    case ex:SQLException =>
       throw ex
  } finally {
    if (rs != null) {
      rs.close()
    }
    if (st != null) {
      st.close()
    }
    if (con != null) {
      con.close()
    }
  }

}
