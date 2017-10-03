package db.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class MySQLConnectionPool {
	private static MySQLConnectionPool poolInstance;
	private static DataSource dataSource;
	
	private MySQLConnectionPool() {
		PoolProperties properties = new PoolProperties();
		properties.setUrl("jdbc:mysql://localhost:3306/titanone");
		properties.setDriverClassName("com.mysql.jdbc.Driver");
		properties.setUsername("root");
		properties.setPassword("root");
		properties.setJmxEnabled(true);
		properties.setTestWhileIdle(false);
		properties.setTestOnBorrow(false);
		properties.setValidationInterval(30000);
		properties.setTimeBetweenEvictionRunsMillis(30000);
		properties.setMaxActive(50);
		properties.setInitialSize(10);
		properties.setMaxWait(10000);
		properties.setRemoveAbandonedTimeout(60);
		properties.setMinEvictableIdleTimeMillis(30000);
		properties.setMinIdle(10);
		properties.setLogAbandoned(true);
		properties.setRemoveAbandoned(true);
		
		dataSource = new DataSource(properties);
	}
	
	public static MySQLConnectionPool getPoolInstance() {
		if(poolInstance == null) {
			poolInstance = new MySQLConnectionPool();
		}
		
		return poolInstance;
	}
	
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
