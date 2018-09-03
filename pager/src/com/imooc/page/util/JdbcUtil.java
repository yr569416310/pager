package com.imooc.page.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JdbcUtil {

	private static String USERNAME;

	private static String PASSWORD;
	// 数据库的驱动信息
	private static String DRIVER;
	// 访问数据库的地址
	private static String URL;

	private Connection connection;
	// sql语句的执行对象
	private PreparedStatement pstmt;
	// 返回的结果集合
	private ResultSet resultSet;

	static{
		 //加载数据库配置信息，并给相关的属性赋值
		loadConfig();
	}
	/**
	 * 加载数据库配置信息，并给相关属性赋值
	 */
	public static void loadConfig(){
		try{
			InputStream inStream = JdbcUtil.class.getResourceAsStream("/jdbc.properties"); 
			Properties prop = new Properties();
			prop.load(inStream);
			USERNAME = prop.getProperty("jdbc.username");
			PASSWORD = prop.getProperty("jdbc.password");
			DRIVER = prop.getProperty("jdbc.driver");
			URL = prop.getProperty("jdbc.url");
		}catch(Exception e){
			throw new RuntimeException("读取数据库配置文件异常!",e);
		}
		
	}
	public JdbcUtil() {
		
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	public Connection getConnection() {
		try {
			Class.forName(DRIVER);// 注册驱动
			connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			throw new RuntimeException("get connection error!");
		}
		return connection;
	}

	/**
	 * 
	 * @param sql语句
	 * @param params
	 *            执行参数
	 * @return
	 * @throws SQLException
	 */
	public boolean updateByPreparedStatement(String sql, List<?> params) throws SQLException {
		boolean flag = false;
		int result = -1;// 表示用户执行添加删除和修改的时候所影响数据库的行数
		pstmt = connection.prepareStatement(sql);
		int index = 1;
		// 填充sql语句中的占位符
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		result = pstmt.executeUpdate();
		flag = result > 0 ? true : false;
		return flag;
	}
	 /**
     * 执行查询操作
     * 
     * @param sql
     *            sql语句
     * @param params
     *            执行参数
     * @return
     * @throws SQLException
     */
	public List<Map<String, Object>> findResult(String sql, List<?> params) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if (params != null && !params.isEmpty()) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();
		// getMetaData()获取结果集的所有字段的描述
		ResultSetMetaData metaData = resultSet.getMetaData();
		// 得到数据集的列数
		int cols_len = metaData.getColumnCount();
		while (resultSet.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < cols_len; i++) {
				String cols_name = metaData.getColumnName(i + 1);
				Object cols_value = resultSet.getObject(cols_name);
				if (cols_value == null) {
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
			list.add(map);
		}
		return list;
	}
	/**
     * 释放资源
     */
    public void releaseConn() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
 
    public static void main(String[] args) {
        JdbcUtil jdbcUtil = new JdbcUtil();
        jdbcUtil.getConnection();
        try {
            List<Map<String, Object>> result = jdbcUtil.findResult(
                    "select * from t_student", null);
            for (Map<String, Object> m : result) {
                System.out.println(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            jdbcUtil.releaseConn();
        }
    }
}
