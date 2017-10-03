package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import db.MySQLDBUtil;
import entity.Item;
import entity.Item.ItemBuilder;
import external.ExternalAPI;
import external.ExternalAPIFactory;
import external.ExternalAPINames;

public class MySQLConnection implements DBConnection {
	private static MySQLConnection instance;
	
	public static DBConnection getInstance(){
		if(instance == null) {
			instance = new MySQLConnection();
		}
		return instance;
	}
	
	//Wrap the connection in DBConnection
	//private Connection conn = null;
	private MySQLConnectionPool connectionPool;
	
	private MySQLConnection() {
		try {
			connectionPool = MySQLConnectionPool.getPoolInstance();
			// Forcing the class representing the MySQL driver to load and
			// initialize.
			// The newInstance() call is a work around for some broken Java
			// implementations.
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			//conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getFullname(String userId) {
		String query = "SELECT * FROM users WHERE user_id = '" + userId +"';";
		String full_name = null;
		Connection conn = connectionPool.getConnection();
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				full_name = resultSet.getString("first_name") + " " + resultSet.getString("last_name");
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return full_name;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		Connection conn = connectionPool.getConnection();
		try {
			if (conn == null) {
				return false;
			}

			String sql = "SELECT user_id from users WHERE user_id = ? and password = ?;";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return false;
	}

	@Override
	public void close() {
		Connection conn = connectionPool.getConnection();
		
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) { /* ignored */
			}finally {
				try {
					conn.close();
					if(conn != null) {
						conn = null;
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		Connection conn = connectionPool.getConnection();
		String query = "INSERT INTO history (user_id, item_id) VALUES (?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			for (String itemId : itemIds) {
				statement.setString(1, userId);
				statement.setString(2, itemId);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		Connection conn = connectionPool.getConnection();
		String query = "DELETE FROM history WHERE user_id = ? AND item_id = ?;";
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			for (String itemId : itemIds) {
				statement.setString(1, userId);
				statement.setString(2, itemId);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		Connection conn = connectionPool.getConnection();
		Set<String> favoriteItems = new HashSet<>();
		try {
			String sql = "SELECT item_id from history WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItems.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return favoriteItems;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		Connection conn = connectionPool.getConnection();
		Set<String> itemIds = getFavoriteItemIds(userId);
		Set<Item> favoriteItems = new HashSet<>();
		try {

			for (String itemId : itemIds) {
				String sql = "SELECT * from items WHERE item_id = ? ";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, itemId);
				ResultSet rs = statement.executeQuery();
				ItemBuilder builder = new ItemBuilder();

				// Because itemId is unique and given one item id there should
				// have
				// only one result returned.
				if (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setCity(rs.getString("city"));
					builder.setState(rs.getString("state"));
					builder.setCountry(rs.getString("country"));
					builder.setZipcode(rs.getString("zipcode"));
					builder.setRating(rs.getDouble("rating"));
					builder.setAddress(rs.getString("address"));
					builder.setLatitude(rs.getDouble("latitude"));
					builder.setLongitude(rs.getDouble("longitude"));
					builder.setDescription(rs.getString("description"));
					builder.setSnippet(rs.getString("snippet"));
					builder.setSnippetUrl(rs.getString("snippet_url"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
				}

				// Join categories information into builder.
				// But why we do not join in sql? Because it'll be difficult
				// to set it in builder.
				sql = "SELECT * from categories WHERE item_id = ?";
				statement = conn.prepareStatement(sql);
				statement.setString(1, itemId);
				rs = statement.executeQuery();
				Set<String> categories = new HashSet<>();
				while (rs.next()) {
					categories.add(rs.getString("category"));
				}
				builder.setCategories(categories);
				favoriteItems.add(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		Connection conn = connectionPool.getConnection();
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category from categories WHERE item_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				categories.add(rs.getString("category"));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(String userId, double lat, double lon, String term) {
		// Connect to external API
		ExternalAPI api = ExternalAPIFactory.getExternalAPI(ExternalAPINames.TICKETMASTER_API); // moved here
		List<Item> items = api.search(lat, lon, term);
		for (Item item : items) {
			// Save the item into our own db.
			saveItem(item);
		}
		return items;
	}

	@Override
	/**
	 * Save item into database
	 * */
	public void saveItem(Item item) {
		Connection conn = connectionPool.getConnection();
		try {
			//insert into items table
			String sql = "INSERT IGNORE INTO items VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getName());
			statement.setString(3, item.getCity());
			statement.setString(4, item.getState());
			statement.setString(5, item.getCountry());
			statement.setString(6, item.getZipcode());
			statement.setDouble(7, item.getRating());
			statement.setString(8, item.getAddress());
			statement.setDouble(9, item.getLatitude());
			statement.setDouble(10, item.getLongitude());
			statement.setString(11, item.getDescription());
			statement.setString(12, item.getSnippet());
			statement.setString(13, item.getSnippetUrl());
			statement.setString(14, item.getImageUrl());
			statement.setString(15, item.getUrl());
			statement.execute();

			// Second, update categories table for each category.
			sql = "INSERT IGNORE INTO categories VALUES (?,?)";
			for (String category : item.getCategories()) {
				statement = conn.prepareStatement(sql);
				statement.setString(1, item.getItemId());
				statement.setString(2, category);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public boolean register(String userId, String firstName, String lastName, String password) {
		Connection conn = connectionPool.getConnection();
		String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?;";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				int count = rs.getInt(1);
				//if the user name has been registered return false
				if(count != 0) {
					return false;
				}
			}
			
			String sql2 = "INSERT INTO users VALUES(?,?,?,?);";
			statement = conn.prepareStatement(sql2);
			statement.setString(1, userId);
			statement.setString(2, password);
			statement.setString(3, firstName);
			statement.setString(4, lastName);
			statement.execute();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			try {
				conn.close();
				if(conn != null) {
					conn = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return true;
	}

}
