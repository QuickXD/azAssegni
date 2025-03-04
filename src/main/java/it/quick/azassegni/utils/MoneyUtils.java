package it.quick.azassegni.utils;

public class MoneyUtils {
    public static String format(double amount) {
        if (amount >= 1_000_000) {
            return (amount / 1_000_000) + "MLN";
        } else if (amount >= 1_000) {
            return (amount / 1_000) + "k";
        } else {
            return String.valueOf((int) amount);
        }
    }

    public static double parse(String formatted) {
        formatted = formatted.toUpperCase();
        if (formatted.endsWith("MLN")) {
            return Double.parseDouble(formatted.replace("MLN", "")) * 1_000_000;
        } else if (formatted.endsWith("K")) {
            return Double.parseDouble(formatted.replace("K", "")) * 1_000;
        } else {
            return Double.parseDouble(formatted);
        }
    }
}
