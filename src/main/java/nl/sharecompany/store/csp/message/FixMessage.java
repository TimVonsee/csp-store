package nl.sharecompany.store.csp.message;

import java.util.HashMap;
import java.util.Map;

public class FixMessage extends BaseMessage{
    public Map<String, String> fix = new HashMap<>();

    public void reset() {
        symbol = null;
        ctfSource = null;
        day = null;
        fix.clear();
    }

    public boolean hasData() {
        return symbol != null && ctfSource != null && !fix.isEmpty();
    }
}
