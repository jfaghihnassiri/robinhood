package Model;

import org.json.JSONObject;

public class Fundamentals extends AbstractModel {
    private double open;
    private double high;
    private double low;
    private double volume;
    private double high_52_weeks;
    private double low_52_weeks;
    private double dividend_yield;
    private double market_cap;
    private double pe_ratio;

    public Fundamentals(JSONObject j) {
        super(j);
        this.open = parseStringAsDouble("open");
        this.high = parseStringAsDouble("high");
        this.low = parseStringAsDouble("low");
        this.volume = parseStringAsDouble("volume");
        this.high_52_weeks = parseStringAsDouble("high_52_weeks");
        this.low_52_weeks = parseStringAsDouble("low_52_weeks");
        this.dividend_yield = parseStringAsDouble("dividend_yield");
        this.market_cap = parseStringAsDouble("market_cap");
        this.pe_ratio = parseStringAsDouble("pe_ratio");
    }

    @Override
    public String toString() {
        return Double.toString(open);
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getVolume() {
        return volume;
    }

    public double getHigh_52_weeks() {
        return high_52_weeks;
    }

    public double getLow_52_weeks() {
        return low_52_weeks;
    }

    public double getDividend_yield() {
        return dividend_yield;
    }

    public double getMarket_cap() {
        return market_cap;
    }

    public double getPe_ratio() {
        return pe_ratio;
    }
}
