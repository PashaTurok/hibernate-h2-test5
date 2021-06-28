
package com.foo.hibernate.h2.test5.adjacencylist;

import com.foo.hibernate.h2.test5.DataLayer;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Disabled;


@Disabled
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdjacencyListJpaIT extends AbstractAdjacencyListIT {

    @BeforeEach
    public void beforeEach(TestInfo testInfo) throws SQLException {
        System.out.println("### " + testInfo.getTestMethod().get());
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
        System.out.println("Array size with manual parameter: " + arrays.size());
        manager.close();
    }

    private void selectWithJpaParameter() {
        var manager = DataLayer.getFactory().createEntityManager();
        var q = manager.createNativeQuery(query);
        q.setParameter(1, joystickNode.getUuid());
        var arrays = q.getResultList();
        System.out.println("Array size jpa parameter: " + arrays.size());
        manager.close();
    }
}

