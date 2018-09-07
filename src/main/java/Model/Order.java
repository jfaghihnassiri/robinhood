package Model;

import Utilities.CommonUtils;
import org.json.JSONObject;

public class Order extends AbstractModel {

    public enum Type {
        MARKET("market"),
        LIMIT("limit");

        private String text;

        Type(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static Type fromString(String text) {
            for (Type t : Type.values()) {
                if (t.text.equalsIgnoreCase(text)) {
                    return t;
                }
            }
            return null;
        }
    }

    public enum TimeInForce {
        GFD("gfd"), // good for trading day
        GTC("gtc"), // good till canceled
        IOC("ioc"), // immediate or cancel
        OPG("opg"); // something to do with opening?

        private String text;

        TimeInForce(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static TimeInForce fromString(String text) {
            for (TimeInForce t : TimeInForce.values()) {
                if (t.text.equalsIgnoreCase(text)) {
                    return t;
                }
            }
            return null;
        }
    }

    public enum Trigger {
        IMMEDIATE("immediate"),
        STOP("stop");

        private String text;

        Trigger(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static Trigger fromString(String text) {
            for (Trigger t : Trigger.values()) {
                if (t.text.equalsIgnoreCase(text)) {
                    return t;
                }
            }
            return null;
        }
    }

    public enum Side {
        BUY("buy"),
        SELL("sell");

        private String text;

        Side(String text) {
            this.text = text;
        }

        public String getText() {
            return this.text;
        }

        public static Side fromString(String text) {
            for (Side t : Side.values()) {
                if (t.text.equalsIgnoreCase(text)) {
                    return t;
                }
            }
            return null;
        }
    }

    private Position position;
    private Type type;
    private TimeInForce timeInForce;
    private Trigger trigger;
    private double price;
    private double stopPrice; // Only when trigger equals stop
    private int quantity;
    private Side side;

    public Order(Position position) {
        this.position = position;
        price = CommonUtils.roundDownNearestCent(position.getCurrentPrice());
        quantity = (int)position.getQuantity();
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TimeInForce getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(TimeInForce timeInForce) {
        this.timeInForce = timeInForce;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getStopPrice() {
        return stopPrice;
    }

    public void setStopPrice(double stopPrice) {
        this.stopPrice = stopPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public String getSymbol() {
        if (position != null) {
            return position.getInstrumentObj().getSymbol();
        }
        return null;
    }

    public double getNetChangePercent() {
        return position.getNetChangePercent();
    }

    public double getNetChange() {
        return position.getNetChangeValue();
    }

    public double getTotalCurrentValue() {
        return position.getTotalCurrentValue();
    }

    public JSONObject toJSON() {
        JSONObject j = new JSONObject();
        j.put("account", position.getAccount());
        j.put("instrument", position.getInstrument());
        j.put("symbol", getSymbol());
        j.put("type", type.getText());
        j.put("time_in_force", timeInForce.getText());
        j.put("trigger", trigger.getText());
        j.put("price", price);
        if (trigger.equals(Trigger.STOP)) j.put("stop_price", stopPrice);
        j.put("quantity", quantity);
        j.put("side", side.getText());
        return j;
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    public double getPercentChangeToday() {
        return position.getPercentChangeToday();
    }
}
