package nl.sharecompany.writer.storage;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
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
        return this.build(new String[]{"127.0.0.1"}, 9042, 2, 5);
    }

    /**
     * Returns a cluster with specified cluster point(s) and port
     * @param contactPoints ip's
     * @param port network port
     * @return cluster
     */
    public Cluster build(String[] contactPoints, int port, int connections, int maxConnections){
        LOGGER.debug("Creating Cassandra Client {} {}", contactPoints, port);

        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions
                .setMaxQueueSize(512)
                .setConnectionsPerHost(HostDistance.LOCAL,  connections, maxConnections);

        return Cluster.builder()
                .addContactPoints(contactPoints)
                .withPoolingOptions(poolingOptions)
                .withPort(port)
                .build();
    }
}
