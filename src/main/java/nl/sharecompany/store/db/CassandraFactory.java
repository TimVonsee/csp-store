package nl.sharecompany.store.db;

import com.datastax.driver.core.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cluster configuration is managed by this class.
 */
public class CassandraFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraFactory.class);

    /**
     * Returns a cluster with default contact point (127.0.0.1) and port (9042)
     * @return cluster
     */
    public Cluster build(){
        return this.build(new String[]{"127.0.0.1"}, 9042);
    }

    /**
     * Returns a cluster with specified cluster point(s) and port
     * @param contactPoints ip's
     * @param port network port
     * @return
     */
    public Cluster build(String[] contactPoints, int port){
        LOGGER.debug("Creating Cassandra Client {} {}", contactPoints, port);

        return Cluster.builder()
                .addContactPoints(contactPoints)
                .withPort(port)
                .build();
    }
}