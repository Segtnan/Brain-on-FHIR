package io.segtnan.hapi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * The type Db connection.
 */
public class DbConnection {
    /**
     * The Connection.
     */
    public Connection connection;
    /**
     * The Source list.
     */
    public String sourceList;
    /**
     * The Node.
     */
    public String node;
    /**
     * The Leaf.
     */
    public String leaf;

    private HashMap<String,String> result;

    /**
     * Instantiates a new Db connection.
     *
     * @param connection the connection
     * @param sourceList the source list
     * @param node       the node
     * @param leaf       the leaf
     */
    public DbConnection(Connection connection, String sourceList, String node, String leaf){
        this.connection = connection;
        this.sourceList = sourceList;
        this.node = node;
        this.leaf = leaf;
    }

    public HashMap<String,String> getResult() {

        return result;
    }

    public boolean hasResult(){
        return this.result != null;
    }

    public void setResult(HashMap<String,String> result) {
        this.result = result;
    }
}
