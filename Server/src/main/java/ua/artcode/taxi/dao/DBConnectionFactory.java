package ua.artcode.taxi.dao;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by r.popov on 6/23/2016.
 */
public class DBConnectionFactory {

    private static final String URL = "jdbc:myssql:3306/MyTaxi3";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public DBConnectionFactory() {
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        return connection;
    }
}
