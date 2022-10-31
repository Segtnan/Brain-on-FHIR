package ca.uhn.fhir.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * The type Federated db handler.
 */
public class FederatedDbHandler {

    /**
     * The Db map.
     */
    private HashMap<String, DbConnection> dbMap = new HashMap<>();
    /**
     * The Connect.
     */
    public Connection connect;

    /**
     * Instantiates a new Federated db handler.
     */
    public FederatedDbHandler(){
        scanForDbs();

    }

    /**
     * Scan for dbs.
     */
    public void scanForDbs(){
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            this.connect = DriverManager
                    .getConnection("jdbc:derby://18.197.78.189:6414/gaiandb","gaiandb","passw0rd");
            ResultSet rs = connect.createStatement().executeQuery(
                    "select * from new com.ibm.db2j.GaianQuery('select * from source', 'with_provenance') GQ");
            while(rs.next()){
                this.dbMap.put(rs.getString("node")+rs.getString("leaf"),
                        new DbConnection(DriverManager
                                .getConnection(rs.getString("url"),"gaiandb","passw0rd"),
                                rs.getString("sourcelist"),
                                rs.getString("node"),
                                rs.getString("leaf")));
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Get db map hash map.
     *
     * @return the hash map
     */
    public HashMap<String, DbConnection> getDbMap(){
        return this.dbMap;
    }
}
