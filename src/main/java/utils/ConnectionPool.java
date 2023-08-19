package utils;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static final BasicDataSource dataSource;
    private static final String DATABASE =
            "jdbc:sqlite:D:\\Программирование\\JavaLessons\\exchange\\src\\main\\resources\\exchange.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        dataSource = new BasicDataSource();
        dataSource.setUrl(DATABASE);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(20);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
