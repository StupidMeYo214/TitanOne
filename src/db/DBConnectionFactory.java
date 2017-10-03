package db;

import db.mysql.MySQLConnection;

public class DBConnectionFactory {
	private static final String MYSQL = "mysql";
	
	//create a DBConnection based on given db type
	public static DBConnection getDBConnection(String db) {
		switch (db) {
		case MYSQL:
			return MySQLConnection.getInstance();
		default:
			throw new IllegalArgumentException("Invalid db" + db);
		}
	}
	
	public static DBConnection getDBConnection(){
		return getDBConnection(MYSQL);
	}
}
