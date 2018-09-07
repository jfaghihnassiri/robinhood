package Model;

import org.json.JSONObject;

public class Quote extends AbstractModel {
    private double last_trade_price;
    private double previous_close;
    private double percentChange;

    public Quote(JSONObject j) {
        super(j);
        this.last_trade_price = parseStringAsDouble("last_trade_price");
        this.previous_close = parseStringAsDouble("previous_close");
        this.percentChange = (( last_trade_price / previous_close ) - 1.0) * 100.0;
    }

    @Override
    public String toString() {
        return Double.toString(last_trade_price);
    }

    public double getLast_trade_price() {
        return last_trade_price;
    }

    public double getPrevious_close() {
        return previous_close;
    }

    public double getPercentChange() {
        return percentChange;
    }
}
