package db;

public class MySQLDBUtil {
	public static final String MYSQL = "mysql";
	private static final String HOST_NAME = "localhost";
	private static final String PORT_NUMBER = "3306";
	private static final String DB_NAME = "titanone";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	public static final String URL = "jdbc:mysql://" + HOST_NAME + ":" + PORT_NUMBER + "/" + DB_NAME + "?user=" + USERNAME + "&password="+PASSWORD + "&autoreconnect=true";
}
