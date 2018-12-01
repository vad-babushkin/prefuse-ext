package prefuse.demos.yamvia;

import java.sql.*;
//import java.text.*;
//import java.io.*;

public class IvipiSql{
	Connection db;
	Statement sql;
	
	private String database="infoviz";
	private String username="ivipi";
	private String password="ivipi";
	
	public IvipiSql () throws ClassNotFoundException, SQLException
	{
		Class.forName("org.postgresql.Driver");
		db = DriverManager.getConnection("jdbc:postgresql:"+database, username, password);
		//System.out.println(db.getClass());
		sql = db.createStatement();
		//ResultSet resultSet = sql.executeQuery("SELECT nb_stations FROM parametres_application"); 
	}
}
