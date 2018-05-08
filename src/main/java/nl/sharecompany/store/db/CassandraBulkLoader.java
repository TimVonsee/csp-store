package nl.sharecompany.store.db;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// https://medium.com/@foundev/cassandra-batch-loading-without-the-batch-the-nuanced-edition-dd78d61e9885
public class CassandraBulkLoader implements BulkLoader{
    private final Session session;
    private final ExecutorService executor;
    private final PreparedStatement statement;

    public CassandraBulkLoader(int threads, Session session, String insertCQL) {
        this.session = session;
        this.executor = MoreExecutors.getExitingExecutorService(
                (ThreadPoolExecutor) Executors.newFixedThreadPool(threads));
        this.statement = session.prepare(insertCQL);
    }

    //callback class
    public static class InsertCallback implements FutureCallback<ResultSet> {
        private final Logger LOGGER = LoggerFactory.getLogger(CassandraBulkLoader.class);

        @Override
        public void onSuccess(ResultSet result) {
            //LOGGER.debug("Persist");
        }

        @Override
        public void onFailure(Throwable t) {
            LOGGER.error("Insertion error: " + t.getMessage());
            //throw new RuntimeException(t);
        }
    }

    @Override
    public void insert(Iterator<Object[]> batch) {
        while (batch.hasNext()) {
            BoundStatement boundStatement = statement.bind(batch.next());
            ResultSetFuture future = session.executeAsync(boundStatement);
            Futures.addCallback(future, new InsertCallback(), executor);
        }
    }

    public void close() {
        try {
            this.executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
