package ca.uhn.fhir.example;

import java.sql.Connection;

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

}
