
package com.foo.hibernate.h2.test5.adjacencylist;

import com.foo.hibernate.h2.test5.adjacencylist.Node;
import com.foo.hibernate.h2.test5.DataLayer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdjacencyListIT {

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

    static {
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

    @BeforeEach
    public void beforeEach(TestInfo testInfo) throws SQLException {
        System.out.println("###" + testInfo.getTestMethod().get());
        var query = "DROP ALL OBJECTS";
        var stmt = DataLayer.getConnection().createStatement();
        stmt.executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS NODE ("
                + "UUID VARCHAR NOT NULL,"
                + "PARENT_UUID VARCHAR NULL,"
                + "NAME VARCHAR NOT NULL,"
                + "PRIMARY KEY (UUID));"
                + "";
        stmt = DataLayer.getConnection().createStatement();
        stmt.executeUpdate(query);

        //creating
        var manager = DataLayer.getFactory().createEntityManager();
        manager.getTransaction().begin();
        for (var node : nodes) {
            manager.persist(node);
        }
        manager.getTransaction().commit();
        manager.close();
    }

    private final String query =
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

    @Test
    public void manualJpa() {
        this.selectWithManualParameter();
        this.selectWithJpaParameter();
    }

    @Test
    public void jpaManual() {
        this.selectWithJpaParameter();
        this.selectWithManualParameter();
    }

    private void selectWithManualParameter() {
        var manager = DataLayer.getFactory().createEntityManager();
        var queryWithParameter = query.replace("?", "'" + joystickNode.getUuid() + "'");
        var q = manager.createNativeQuery(queryWithParameter);
        var arrays = q.getResultList();
        System.out.println("# Array size with manual parameter: " + arrays.size());
        manager.close();
    }

    private void selectWithJpaParameter() {
        var manager = DataLayer.getFactory().createEntityManager();
        var q = manager.createNativeQuery(query);
        q.setParameter(1, joystickNode.getUuid());
        var arrays = q.getResultList();
        System.out.println("# Array size jpa parameter: " + arrays.size());
        manager.close();
    }
}

