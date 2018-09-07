package Model;

import org.json.JSONObject;

import java.util.Date;

public class Position extends AbstractModel {
    private double quantity;
    private double average_buy_price;
    private String instrument;
    private String account;
    private Instrument instrumentObj;
    private Date created_at;
    private Date updated_at;

    public Position(JSONObject j) {
        super(j);
        this.quantity = parseStringAsDouble("quantity");
        this.average_buy_price = parseStringAsDouble( "average_buy_price");
        this.instrument = parseString("instrument");
        this.account = parseString("account");
        this.created_at = parseStringAsDate("created_at");
        this.updated_at = parseStringAsDate("updated_at");
    }

    public Position() {
        super();
    }

    @Override
    public String toString() {
        return instrumentObj.toString();
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getAverage_buy_price() {
        return average_buy_price;
    }

    public void setAverage_buy_price(double average_buy_price) {
        this.average_buy_price = average_buy_price;
    }

    public double getTotalBuyValue() {
        return quantity * average_buy_price;
    }

    public double getCurrentPrice() {
        return instrumentObj.getCurrentPrice();
    }

    public double getTotalCurrentValue() {
        return quantity * getCurrentPrice();
    }

    public double getNetChangeValue() {
        return getTotalCurrentValue() - getTotalBuyValue();
    }

    public double getNetChangeRatio() {
        return (getTotalCurrentValue() / getTotalBuyValue()) - 1;
    }

    public double getNetChangePercent() {
        return getNetChangeRatio() * 100;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public void setInstrumentObj(Instrument instrumentObj) {
        this.instrumentObj = instrumentObj;
    }

    public Instrument getInstrumentObj() {
        return instrumentObj;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSymbol() {
        return instrumentObj.getSymbol();
    }

    public int getDaysInvested() {
        long timeDiff = new Date().getTime() - updated_at.getTime();
        return (int)(timeDiff / 86400000); // miliseconds to days rounded down
    }

    public double getPercentChangeToday() {
        return instrumentObj.getPercentChangeToday();
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}
