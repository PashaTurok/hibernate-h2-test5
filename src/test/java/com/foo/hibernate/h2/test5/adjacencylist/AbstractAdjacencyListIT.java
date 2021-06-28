
package com.foo.hibernate.h2.test5.adjacencylist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public abstract class AbstractAdjacencyListIT {

    protected final static List<Node> nodes= new ArrayList<>();

    protected static Node joystickNode;

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

    protected final String query =
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

}
