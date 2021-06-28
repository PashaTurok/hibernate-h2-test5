
package com.foo.hibernate.h2.test5.adjacencylist;

import com.foo.hibernate.h2.test5.DataLayer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;


public class AdjacencyListJdbcIT extends AbstractAdjacencyListIT {

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

        //THIS CODE DOESN'T WORK, BUT IT SHOULD????
//        query = "INSERT INTO NODE (UUID, PARENT_UUID, NAME) VALUES(?, ?, ?)";
//        DataLayer.getConnection().setAutoCommit(false);
//        PreparedStatement ps = DataLayer.getConnection().prepareStatement(query);
//        for (var node : nodes) {
//            ps.setString(1, node.getUuid());
//            ps.setString(2, node.getParentUuid());
//            ps.setString(3, node.getName());
//            ps.addBatch();
//        }
//        ps.executeUpdate();
//        DataLayer.getConnection().commit();

        query = "INSERT INTO NODE (UUID, PARENT_UUID, NAME) VALUES(?, ?, ?)";
        for (var node : nodes) {
            PreparedStatement ps = DataLayer.getConnection().prepareStatement(query);
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
        Statement stmt = DataLayer.getConnection().createStatement();
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
        PreparedStatement ps = DataLayer.getConnection().prepareStatement(query);
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
