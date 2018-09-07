package Model;

import org.json.JSONObject;

public class Instrument extends AbstractModel {
    String url;
    String quote;
    String name;
    String symbol;
    String fundamentals;
    String simple_name;
    Quote quoteObj;
    Fundamentals fundamentalsObj;

    public Instrument(JSONObject j) {
        super(j);
        this.url = parseString("url");
        this.quote = parseString("quote");
        this.name = parseString("name");
        this.symbol = parseString("symbol");
        this.fundamentals = parseString("fundamentals");
        this.simple_name = parseString("simple_name");
    }

    @Override
    public String toString() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getQuote() {
        return quote;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSimple_name() {
        return simple_name;
    }

    public String getFundamentals() {
        return fundamentals;
    }

    public Quote getQuoteObj() {
        return quoteObj;
    }

    public Fundamentals getFundamentalsObj() {
        return fundamentalsObj;
    }

    public void setQuoteObj(Quote quoteObj) {
        this.quoteObj = quoteObj;
    }

    public void setFundamentalsObj(Fundamentals fundamentalsObj) {
        this.fundamentalsObj = fundamentalsObj;
    }

    public double getPercentChangeToday() {
        return quoteObj.getPercentChange();
    }

    public double getPercentFrom52WeekHigh() {
        double high52 = fundamentalsObj.getHigh_52_weeks();
        return ((high52 - quoteObj.getLast_trade_price()) / high52) * -100.0;
    }

    public double getPercentFrom52WeekLow() {
        double low52 = fundamentalsObj.getLow_52_weeks();
        return ((quoteObj.getLast_trade_price() - low52) / low52) * 100.0;
    }

    public double getYield() {
        return fundamentalsObj.getDividend_yield();
    }

    public double getPERatio() {
        return fundamentalsObj.getPe_ratio();
    }

    public double getMarketCap() {
        return fundamentalsObj.getMarket_cap();
    }

    public double getCurrentPrice() {
        return quoteObj.getLast_trade_price();
    }

    public boolean equals(Instrument i) {
        return url.equals(i.getUrl());
    }
}
