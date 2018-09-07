package Utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonUtils {
    public static double roundDownNearestCent(double input) {
        return ((int)(input*100)/100.0);
    }

    public static String getNowStr() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static double oneBillion() {
        return 1000000000;
    }
}
