package io.segtnan.hapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The type Federated db handler.
 */
public class FederatedDbHandler {

    public enum ResourceType {
        PATIENT,
        SEGTNAN_QUESTIONNAIRE_RESPONSE
    }

    /**
     * The Db map.
     */
    private HashMap<String, DbConnection> dbMap = new HashMap<>();
    private HashMap<String, DbConnection> sourceMap = new HashMap<>();
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

    public ArrayList<DbConnection> findConnection(String uuid, ResourceType resourceType) {
        return findConnection(uuid, resourceType, "all");
    }

    public ArrayList<DbConnection> findConnection(String uuid, ResourceType resourceType, String tenantID){
        return findConnection(uuid,resourceType,tenantID,false);
    }


    public ArrayList<DbConnection> findConnection(String uuid, ResourceType resourceType, String tenantID, boolean saveResult){
        String table = "";
        switch (resourceType){
            case PATIENT:
                table = "patient";
                break;
            case SEGTNAN_QUESTIONNAIRE_RESPONSE:
                table = "segtnan";
                break;
        }
        try {
            ResultSet rs = this.connect.createStatement().executeQuery(
                    String.format("select * from new com.ibm.db2j.GaianTable('%s', 'with_provenance') T where uuid='%s'",
                            table,
                            uuid));
            ArrayList<DbConnection> arr = new ArrayList<DbConnection>();
            while (rs.next()) {
                // GaianTable appends the table name as well, split to remove
                DbConnection dbConnection = this.dbMap.get((rs.getString("gdb_node") + rs.getString("gdb_leaf")).split("::")[0]);
                if(dbConnection.sourceList.equals(tenantID) || tenantID.equals("all")){
                    if(saveResult){
                        HashMap<String,String> result = new HashMap<>();
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            result.put(rs.getMetaData().getColumnLabel(i),rs.getString(i));
                        }
                        dbConnection.setResult(result);
                    }
                    arr.add(dbConnection);
                }
            }
            return arr;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


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
                DbConnection dbConn = new DbConnection(DriverManager
                        .getConnection(rs.getString("url"),"gaiandb","passw0rd"),
                        rs.getString("sourcelist"),
                        rs.getString("node"),
                        rs.getString("leaf"));
                this.dbMap.put(rs.getString("node")+rs.getString("leaf"), dbConn);
                this.sourceMap.put(dbConn.sourceList, dbConn);
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

    public HashMap<String, DbConnection> getSourceMap(){
        return this.sourceMap;
    }
}
