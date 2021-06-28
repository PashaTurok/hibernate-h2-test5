
package com.foo.hibernate.h2.test5.adjacencylist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;


public class AdjacencyListJdbcIT {

    private static class Node {

        private String uuid;

        private String parentUuid;

        private String name;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getParentUuid() {
            return parentUuid;
        }

        public void setParentUuid(String parentUuid) {
            this.parentUuid = parentUuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private final static List<Node> nodes= new ArrayList<>();

    private static Node joystickNode;

    private static Node generateNode(String name, Node parent) {
        var node = new Node();
        node.setUuid(UUID.randomUUID().toString());
        node.setName(name);
        if (parent != null) {
            node.setParentUuid(parent.getUuid());
        }
        return node;
    }

    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:db");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        nodes.add(generateNode("Computers", null));
        nodes.add(generateNode("Food", null));
        nodes.add(generateNode("Drinks", null));

        nodes.add(generateNode("Monitors", nodes.get(0)));
        nodes.add(generateNode("System blocks", nodes.get(0)));
        nodes.add(generateNode("Mother boards", nodes.get(0)));
        nodes.add(generateNode("Different", nodes.get(0)));

        nodes.add(generateNode("Keyboards", nodes.get(6)));
        nodes.add(generateNode("Mice", nodes.get(6)));
        joystickNode = generateNode("Joysticks", nodes.get(6));
        nodes.add(joystickNode);

        nodes.add(generateNode("Size 14", nodes.get(3)));
        nodes.add(generateNode("Size 15", nodes.get(3)));
        nodes.add(generateNode("Size 16", nodes.get(3)));
    }

    private static final String query =
        "WITH RECURSIVE parents (uuid, parent_uuid, name, distance) AS (\n" +
        "    SELECT uuid, parent_uuid, name, 0 AS distance\n" +
        "    FROM node\n" +
        "    WHERE uuid = ? \n" +
        "    UNION ALL\n" +
        "\n" +
        "    SELECT a.uuid, a.parent_uuid, a.name, b.distance + 1 as distance\n" +
        "    FROM node a\n" +
        "    INNER JOIN parents b ON b.parent_uuid = a.uuid\n" +
        "    ),\n" +
        "siblings AS (\n" +
        "    SELECT a.uuid, a.parent_uuid, a.name, b.distance as distance\n" +
        "    FROM node a\n" +
        "    INNER JOIN parents b ON b.parent_uuid = a.parent_uuid OR (b.parent_uuid IS NULL AND a.parent_uuid IS NULL)\n" +
        ")\n" +
        "SELECT uuid, parent_uuid, name FROM siblings order by distance desc, name";

    @BeforeEach
    public void beforeEach(TestInfo testInfo) throws SQLException {
        System.out.println("### " + testInfo.getTestMethod().get());
        var query = "DROP ALL OBJECTS";
        var stmt = connection.createStatement();
        stmt.executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS NODE ("
                + "UUID VARCHAR NOT NULL,"
                + "PARENT_UUID VARCHAR NULL,"
                + "NAME VARCHAR NOT NULL,"
                + "PRIMARY KEY (UUID));"
                + "";
        stmt = connection.createStatement();
        stmt.executeUpdate(query);

        //THIS CODE DOESN'T WORK, BUT IT SHOULD????
//        query = "INSERT INTO NODE (UUID, PARENT_UUID, NAME) VALUES(?, ?, ?)";
//        connection.setAutoCommit(false);
//        PreparedStatement ps = connection.prepareStatement(query);
//        for (var node : nodes) {
//            ps.setString(1, node.getUuid());
//            ps.setString(2, node.getParentUuid());
//            ps.setString(3, node.getName());
//            ps.addBatch();
//        }
//        ps.executeUpdate();
//        connection.commit();

        query = "INSERT INTO NODE (UUID, PARENT_UUID, NAME) VALUES(?, ?, ?)";
        for (var node : nodes) {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, node.getUuid());
            ps.setString(2, node.getParentUuid());
            ps.setString(3, node.getName());
            ps.executeUpdate();
        }
    }

    @Test
    public void manualAndPrepared() throws SQLException {
        this.selectWithManualParameter();
        this.selectWithPreparedParameter();
    }

    @Test
    public void preparedAndManual() throws SQLException {
        this.selectWithPreparedParameter();
        this.selectWithManualParameter();
    }

    private void selectWithManualParameter() throws SQLException {
        var queryWithParameter = query.replace("?", "'" + joystickNode.getUuid() + "'");
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(queryWithParameter);
        var count = 0;;
        while(rs.next()){
            count++;
         }
        System.out.println("Result size with manual parameter: " + count);
        rs.close();
        stmt.close();
    }

    private void selectWithPreparedParameter() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, joystickNode.getUuid());
        ResultSet rs = ps.executeQuery();
        var count = 0;;
        while(rs.next()){
            count++;
         }
        System.out.println("Result size with prepared parameter: " + count);
        rs.close();
        ps.close();
    }
}
