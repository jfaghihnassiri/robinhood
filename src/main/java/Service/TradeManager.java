package Service;

import Model.Account;
import Model.Instrument;
import Model.Order;
import Model.Order.Side;
import Model.Order.TimeInForce;
import Model.Order.Trigger;
import Model.Order.Type;
import Model.Position;
import Utilities.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TradeManager {
    Robinhood robinhood;

    public static final int PERCENT_AUTO_INVEST = 50; // invest up to 50% of assets at any given time
    public static final double POSITION_RATIO = 0.09; // invest 9% per position
    public static final int SECONDS_PER_INTERVAL = 5;
    public static final double MIN_MARKET_CAP = 10 * CommonUtils.oneBillion(); // only deal with companies over 10 billion

    public TradeManager() {
        robinhood = Robinhood.getInstance();
    }

    public void startTrading() {
        while(true) {
            try {
                // check for sells
                checkForSells();

                // wait to allow for execution of sells and for market changes
                Thread.sleep(1000 * SECONDS_PER_INTERVAL);

                // check for buys
                checkForBuys();

                // wait to allow for execution of buy and for market changes
                Thread.sleep(1000 * SECONDS_PER_INTERVAL);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Selling algorithm
    public void checkForSells() {
        // find positions
        ArrayList<Position> positions = Robinhood.getInstance().getPositons();
        printPositions(positions, "Here are your positions");
        // check positions for candidates to sell
        for (Position position : positions) {
            int daysInvested = position.getDaysInvested();
            double netChangePercent = position.getNetChangePercent();
            // TODO enhancement - you can use S&P trackers to know what the market is doing overall
            // TODO enhacement check if up or down today in order to make decision (let growers grow, keep fallers from falling?)
            if ( netChangePercent >= 5 ||
             daysInvested <= 1 && netChangePercent >= 4 ||
             daysInvested > 1 && daysInvested <= 3 && netChangePercent >= 3 ||
             daysInvested > 3 && daysInvested <= 5 && netChangePercent >= 2 ||
             daysInvested > 5 && daysInvested <= 7 && netChangePercent >= 0 ||
             daysInvested > 7 && daysInvested <=9 && netChangePercent >= -2 ||
             daysInvested > 9 && daysInvested <=11 && netChangePercent >= -3 ||
             daysInvested > 11 && netChangePercent >= -4 ||
             netChangePercent <= -50) {
                sellPosition(position);
            }
            else {
                System.out.println("Not ready for sale: " + position.getSymbol());
            }
        }
        System.out.println();
    }

    private void sellPosition(Position position) {
        Order order = new Order(position);
        order.setType(Type.MARKET);
        order.setTrigger(Trigger.IMMEDIATE);
        order.setTimeInForce(TimeInForce.GFD);
        order.setSide(Side.SELL);
        confirmOrder(order);
    }

    // if < 50% of assets invested, look to buy
    public void checkForBuys() throws Exception {
        ArrayList<Account> accounts = robinhood.getAccounts();
        if (accounts.size() != 1) {
            throw new Exception("Did not find exactly one account for token - aborting to be safe.");
        }
        Account account = accounts.get(0);
        double buyingPower = account.getBuyingPowerAccurate();
        double sumPositions = getSumPositions();
        double totalValue = buyingPower + sumPositions;
        double percentInvested = (sumPositions / totalValue) * 100;
        System.out.println("You are " + CommonUtils.roundDownNearestCent(percentInvested) + "% invested.");
        if (percentInvested < PERCENT_AUTO_INVEST) {
            System.out.println("Less than the " + PERCENT_AUTO_INVEST + "% threshold, looking for a position to buy.\n");
            final double amountToBuy = POSITION_RATIO * totalValue;
            buy(account, amountToBuy);
        } else {
            System.out.println("More than the " + PERCENT_AUTO_INVEST + "% threshold, no action needed.\n");
        }
    }

    private double getSumPositions() {
        int sumPositions = 0;
        ArrayList<Position> positions = robinhood.getPositons();
        for (Position position : positions) {
            sumPositions += position.getTotalCurrentValue();
        }
        return sumPositions;
    }

    // each buy should be for just under 10% of assets (allows for up to 5 holdings at 50% invested)
    // trigger a buy
    private void buy(Account account, double amountToBuy) {
        // what to buy
        ArrayList<Instrument> topMovers = Robinhood.getInstance().getTopMoverInstruments();
        ArrayList<Position> positions = Robinhood.getInstance().getPositons();
        ArrayList<Instrument> qualifiedTopMovers = getQualifiedTopMovers(topMovers, positions);
        ArrayList<Instrument> qualifiedTopLosers = filterByDirection(qualifiedTopMovers, false);
        List<Instrument> top5Movers = topMovers.subList(0,5);
        printInstruments(top5Movers, "Here are the top 5 losers");
        printInstruments(qualifiedTopMovers, "Here are the top qualified movers");
        printInstruments(qualifiedTopLosers, "Here are the top qualified losers");
        proposeBuyFromList(account, amountToBuy, qualifiedTopLosers);
    }

    private void proposeBuyFromList(Account account, double amountToBuy, ArrayList<Instrument> candidates) {
        for (Instrument instrument : candidates) {
            if (instrument.getMarketCap() > MIN_MARKET_CAP) {
                Position position = new Position();
                position.setInstrumentObj(instrument);
                position.setInstrument(instrument.getUrl());
                position.setAccount(account.getUrl());
                position.setQuantity(amountToBuy / instrument.getCurrentPrice());
                Order order = new Order(position);
                order.setType(Type.MARKET);
                order.setTrigger(Trigger.IMMEDIATE);
                order.setTimeInForce(TimeInForce.GFD);
                order.setSide(Side.BUY);
                if (confirmOrder(order)) {
                    break;
                }
            }
        }
    }

    private ArrayList<Instrument> getQualifiedTopMovers(ArrayList<Instrument> topMovers, ArrayList<Position> positions) {
        ArrayList<Instrument> qualifiedTopMovers = new ArrayList<>();
        for (Instrument instrument : topMovers) {
            if (instrument.getMarketCap() > MIN_MARKET_CAP) {
                boolean owned = false;
                for (Position position : positions) {
                    if (position.getInstrumentObj().equals(instrument)) {
                        owned = true;
                        break;
                    }
                }
                if (!owned) {
                    qualifiedTopMovers.add(instrument);
                }
            }
        }
        return qualifiedTopMovers;
    }

    private ArrayList<Instrument> filterByDirection(ArrayList<Instrument> instruments, boolean winners) {
        ArrayList<Instrument> filtered = new ArrayList<>();
        for (Instrument instrument : instruments) {
            if (winners && instrument.getPercentChangeToday() > 0 || !winners && instrument.getPercentChangeToday() < 0) {
                filtered.add(instrument);
            }
        }
        return filtered;
    }

    private final boolean confirmOrder(Order order) {
        // Ask for confirmation
        String endStatement = order.getSide() == Side.SELL ? CommonUtils.roundDownNearestCent(order.getNetChangePercent()) + "% change" : "";
        System.out.println(
         order.getSide().getText() + " " + order.getQuantity() +
          " shares of " + order.getSymbol() +
          "(" + CommonUtils.roundDownNearestCent(order.getPercentChangeToday()) + "%)" +
          " for " + order.getPrice() + " each, " +
          CommonUtils.roundDownNearestCent(order.getTotalCurrentValue()) + " total. " +
          endStatement + "\nWould you like to continue? y/n\n");

        // Read in confirmation
        Scanner scanner = new Scanner(System.in);
        switch (scanner.next().toLowerCase()) {
            case "y":
            case "yes": {
                // TODO switch the message below to read out from an object being returned
                Robinhood.getInstance().sendOrder(order);
                System.out.println("The order above has been placed.\n");
                return true;
            }
            case "n":
            case "no":
            default: {
                System.out.println("Oh, ok...\n");
                return false;
            }
        }
    }

    public void printPositions(List<Position> positions, String title) {
        System.out.println("\n"+title+" - "+ CommonUtils.getNowStr()); // TODO replace with timestamp from json
        System.out.format(" %1$6s | %2$6s | %3$6s | %4$4s\n",
         "Ticker", "Value", "Change", "Days");
        for (Position p : positions) {
            System.out.format(" %1$6s | %2$6.1f | %3$6.1f | %4$4s\n",
             p.getInstrumentObj().getSymbol(), p.getTotalCurrentValue(), p.getNetChangePercent(), p.getDaysInvested());
        }
        System.out.println();
    }

    public void printInstruments(List<Instrument> instruments, String title) {
        System.out.println(title+" - "+ CommonUtils.getNowStr()); // TODO replace with timestamp from json
        System.out.format(" %1$6s | %2$6s | %3$6s | %4$6s| %5$6s | %7$6s| %8$6s | %6$-6s\n",
         "Ticker", "Change", "52 Low", "52 High", "Yield", "Name", "PERatio", "CapMil");
        for (Instrument i : instruments) {
            System.out.format(" %1$6s | %2$6.1f | %4$6.1f | %5$6.1f | %6$6.2f | %7$6.1f | %8$6.0f | %3$-35s\n",
             i.getSymbol(), i.getPercentChangeToday(), i.getName(), i.getPercentFrom52WeekLow(), i.getPercentFrom52WeekHigh(), i.getYield(), i.getPERatio(), i.getMarketCap()/1000000);
        }
        System.out.println();
    }
}
