package nl.sharecompany.writer.csp.message;

import java.util.Date;

public class Message extends BaseMessage {
    public String price;
    public String size;
    public Date timestamp;

    public boolean hasData() {
        return symbol != null && ctfSource != null && day != null && price != null;
    }

    public void reset() {
        symbol = null;
        ctfSource = null;
        day = null;
        timestamp = null;
        price = null;
        size = null;
    }

    @Override
    public String toString() {
        return "ctfSource='" + ctfSource + '\'' +
                ", symbol='" + symbol + '\'' +
                ", day='" + day + '\'' +
                ", price='" + price + '\'' +
                ", size='" + size + '\'' +
                ", timestamp='" + timestamp + '\'';
    }
}
