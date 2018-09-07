package Model;

import org.json.JSONObject;

public class Account extends AbstractModel {
    String url;
    String account_number;
    double cash_available_for_withdrawal;
    double sma;
    double unsettled_funds;
    double unsettled_debit;
    double buying_power;
    double cash;

    public Account(JSONObject j) {
        super(j);
        url = parseString("url");
        account_number = parseString("account_number");
        cash_available_for_withdrawal = parseStringAsDouble("cash_available_for_withdrawal");
        sma = parseStringAsDouble("sma");
        unsettled_funds = parseStringAsDouble("unsettled_funds");
        unsettled_debit = parseStringAsDouble("unsettled_debit");
        buying_power = parseStringAsDouble("buying_power");
        cash = parseStringAsDouble("cash");
    }

    public double getCash_available_for_withdrawal() {
        return cash_available_for_withdrawal;
    }

    public void setCash_available_for_withdrawal(double cash_available_for_withdrawal) {
        this.cash_available_for_withdrawal = cash_available_for_withdrawal;
    }

    public double getSma() {
        return sma;
    }

    public void setSma(double sma) {
        this.sma = sma;
    }

    public double getUnsettled_funds() {
        return unsettled_funds;
    }

    public void setUnsettled_funds(double unsettled_funds) {
        this.unsettled_funds = unsettled_funds;
    }

    public double getUnsettled_debit() {
        return unsettled_debit;
    }

    public void setUnsettled_debit(double unsettled_debit) {
        this.unsettled_debit = unsettled_debit;
    }

    public double getBuying_power() {
        return buying_power;
    }

    public void setBuying_power(double buying_power) {
        this.buying_power = buying_power;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getBuyingPowerAccurate() {
        return cash_available_for_withdrawal + unsettled_funds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }
}
