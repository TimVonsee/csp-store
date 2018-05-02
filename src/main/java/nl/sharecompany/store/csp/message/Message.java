package nl.sharecompany.store.csp.message;

public class Message extends BaseMessage {
    public String price;
    public String size;
    public String timestamp;

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
