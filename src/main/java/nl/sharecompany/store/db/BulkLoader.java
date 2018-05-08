package nl.sharecompany.store.db;

import java.util.Iterator;

public interface BulkLoader {
    void insert(Iterator<Object[]> batch);
}
