package nl.sharecompany.store.db;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BulkLoader {
    private final int threads;
    private final Cluster cluster;

    public BulkLoader(int threads, Cluster cluster) {
        this.threads = threads;
        this.cluster = cluster;
    }

    //callback class
    public static class IngestCallback implements FutureCallback<ResultSet> {

        @Override
        public void onSuccess(ResultSet result) {
            //placeholder: put any logging or on success logic here.
        }

        @Override
        public void onFailure(Throwable t) {
            //go ahead and wrap in a runtime exception for this case, but you can do logging or start counting errors.
            throw new RuntimeException(t);
        }
    }

    public void ingest(Iterator<Object[]> boundItemsIterator, String insertCQL) throws InterruptedException {

        Session session = cluster.newSession();

        // fixed thread pool that closes on app exit
        ExecutorService executor = MoreExecutors.getExitingExecutorService(
                (ThreadPoolExecutor) Executors.newFixedThreadPool(threads));

        final PreparedStatement statement = session.prepare(insertCQL);
        while (boundItemsIterator.hasNext()) {
            BoundStatement boundStatement = statement
                    .bind(boundItemsIterator.next());
            ResultSetFuture future = session.executeAsync(boundStatement);
            Futures.addCallback(future, new IngestCallback(), executor);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            session.close();
            cluster.close();
        }
    }
}
