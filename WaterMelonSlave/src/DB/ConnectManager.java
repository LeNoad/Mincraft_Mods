package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import DTO.BackDTO;
import DTO.HomeDTO;

public class ConnectManager {
	private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://mmgg.kr/minecraft_mod?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";
	private final String USER_NAME = "root";
	private final String PASSWORD = "as153462";
	private Connection conn;
	private Statement stmt;
	private Statement stmt_save;
	private Statement stmt_update;
	private ResultSet rs;
	private String select_sql;
	private String insert_sql;
	private String delete_sql;
	private String update_sql;

	public ConnectManager() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("[클래스 확인 및 연결 완료]");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public boolean deleteHome(String uuid, String command) throws SQLException {
		stmt = conn.createStatement();
		delete_sql = "delete from home where command='" + command + "' and uuid='" + uuid
				+ "';";
		if(stmt.executeUpdate(delete_sql) == 1) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}

	public HomeDTO goHome(String uuid, String command) {
		HomeDTO home = new HomeDTO();
		try {
			stmt = conn.createStatement();
			home.setUUID(uuid);
			home.setCommand(command);
			select_sql = "select * from home where command='" + command + "' and uuid='" + uuid + "';";
			rs = stmt.executeQuery(select_sql);
			if (rs.next()) {
				home.setX(Double.valueOf(rs.getString("X")));
				home.setY(Double.valueOf(rs.getString("Y")));
				home.setZ(Double.valueOf(rs.getString("Z")));
				home.setWorld(rs.getString("world"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close();
		return home;
	}

	public boolean saveHome(HomeDTO homeDto) throws SQLException {
		stmt = conn.createStatement();
		select_sql = "select * from home where command='" + homeDto.getCommand() + "' and uuid ='" + homeDto.getUUID()
				+ "';";
		rs = stmt.executeQuery(select_sql);
		if (!rs.next()) {
			stmt_save = conn.createStatement();
			insert_sql = "insert into home(UUID, command, X, Y, Z, world) values('" + homeDto.getUUID() + "', '"
					+ homeDto.getCommand() + "', '" + homeDto.getX() + "', '" + homeDto.getY() + "', '" + homeDto.getZ()
					+ "', '" + homeDto.getWorld()
					+ "');";
			if (!stmt_save.execute(insert_sql)) {
				close();
				return true;
			} else {
				close();
				return false;
			}
		} else {
			close();
			return false;
		}
	}
	public boolean saveBack(BackDTO backDto) throws SQLException {
		stmt_update = conn.createStatement();
		update_sql = "update back set x="+backDto.getX()+", y="+backDto.getY()+", z="+backDto.getZ()+","
								+ "world='"+backDto.getWorld()+"' where uuid='"+backDto.getUUID()+"'";
		if(!stmt_update.execute(update_sql)) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}
	public Boolean selectBack(String uuid) {
		Boolean status = true;
		try {
			stmt = conn.createStatement();
			select_sql = "select * from back where uuid='" + uuid + "';";
			rs = stmt.executeQuery(select_sql);
			if(rs.next()) {
				status = false;
			} else {
				status = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
	public Boolean insertBack(BackDTO backDto) throws SQLException {
		stmt_save = conn.createStatement();
		insert_sql = "insert into back (uuid, x, y, z, world) values ('"+backDto.getUUID()+"', "
				+ "'"+backDto.getX()+"', '"+backDto.getY()+"', '"+backDto.getZ()+"', '"+backDto.getWorld()+"');";
		if(!stmt_save.execute(insert_sql)) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}
	public BackDTO goBack(String uuid) {
		BackDTO backDto = null;
		try {
			stmt = conn.createStatement();
			select_sql = "select * from back where uuid='" + uuid + "';";
			rs = stmt.executeQuery(select_sql);
			while (rs.next()) {
				backDto = new BackDTO();
				backDto.setUUID(rs.getString("UUID"));
				backDto.setX(Double.valueOf(rs.getString("X")));
				backDto.setY(Double.valueOf(rs.getString("Y")));
				backDto.setZ(Double.valueOf(rs.getString("Z")));
				backDto.setWorld(rs.getString("world"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close();
		return backDto;
	}

	public List<HomeDTO> homeList(String uuid) {
		List<HomeDTO> list = null;
		HomeDTO home;
		try {
			list = new ArrayList<HomeDTO>();
			stmt = conn.createStatement();
			select_sql = "select * from home where uuid='" + uuid + "';";
			rs = stmt.executeQuery(select_sql);
			while (rs.next()) {
				home = new HomeDTO();
				home.setUUID(rs.getString("UUID"));
				home.setCommand(rs.getString("command"));
				home.setX(Double.valueOf(rs.getString("X")));
				home.setY(Double.valueOf(rs.getString("Y")));
				home.setZ(Double.valueOf(rs.getString("Z")));
				home.setWorld(rs.getString("world"));
				list.add(home);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close();
		return list;
	}

	public void close() {
		
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
}
