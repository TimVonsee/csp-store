package nl.sharecompany.store.csp.DfaFactory;

import com.datastax.driver.core.Cluster;
import nl.sharecompany.ctffeed.dfa.ArrayDFA;
import nl.sharecompany.store.csp.command.EndOfMessageCommand;
import nl.sharecompany.store.csp.message.Message;
import nl.sharecompany.store.db.BulkLoader;

public class DfaFactory {
    private static final int THREADS = 10;
    private static final int BATCH_LIMIT = 60;

    public enum DfaType {
        TRADE, BID, ASK
    }

    private static final String INSERT_ASK = "INSERT INTO fix_db.asks_by_day (src_id, symbol, day, uts, ask_ts, ask_price, ask_size) VALUES (?, ?, ?, now(), ?, ?, ?);";
    private static final String INSERT_BID = "INSERT INTO fix_db.bids_by_day (src_id, symbol, day, uts, bid_ts, bid_price, bid_size) VALUES (?, ?, ?, now(), ?, ?, ?);";
    private static final String INSERT_TRADE = "INSERT INTO fix_db.trades_by_day (src_id, symbol, day, uts, trade_ts, trade_price, trade_size) VALUES (?, ?, ?, now(), ?, ?, ?);";

    private Cluster cluster;

    public DfaFactory(Cluster cluster) {
        this.cluster = cluster;
    }

    public ArrayDFA getDfa(DfaType type) {
        switch (type){
            case TRADE:
                return tradeDfa();
            case ASK:
                return askDfa();
            case BID:
                return bidDfa();
            default:
                return null;
        }
    }

    private ArrayDFA askDfa() {
        Message msg = new Message();
        BulkLoader loader = new BulkLoader(THREADS, cluster.newSession(), INSERT_ASK);
        EndOfMessageCommand endMsg = new EndOfMessageCommand(msg, BATCH_LIMIT, loader);

        return new AskDfaFactory(endMsg, msg).create();
    }

    private ArrayDFA bidDfa() {
        Message msg = new Message();
        BulkLoader loader = new BulkLoader(THREADS, cluster.newSession(), INSERT_BID);
        EndOfMessageCommand endMsg = new EndOfMessageCommand(msg, BATCH_LIMIT, loader);

        return new BidDfaFactory(endMsg, msg).create();
    }

    private ArrayDFA tradeDfa() {
        Message msg = new Message();
        BulkLoader loader = new BulkLoader(THREADS, cluster.newSession(), INSERT_TRADE);

        EndOfMessageCommand endMsg = new EndOfMessageCommand(msg, BATCH_LIMIT, loader);
        return new TradeDfaFactory(endMsg, msg).create();
    }
}
