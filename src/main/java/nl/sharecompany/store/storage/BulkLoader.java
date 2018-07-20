package nl.sharecompany.store.storage;

import java.util.Iterator;

public interface BulkLoader {
    /**
     * Batch of query parameters that
     * @param batch
     */
    void insert(Iterator<Object[]> batch);
}
