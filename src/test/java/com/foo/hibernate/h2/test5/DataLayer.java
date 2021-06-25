package com.foo.hibernate.h2.test5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.h2.tools.Server;

/**
 *
 * 
 */
public class DataLayer {

    private static Connection connection;

    private static Server server;

    private static EntityManagerFactory factory;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:db");
            server =  Server.createTcpServer().start();
            factory = Persistence.createEntityManagerFactory("test");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static EntityManagerFactory getFactory() {
        return factory;
    }



}
