package antifraud.util;

public class LogUtil {

    private static long counter = 0;

    public static long getCounter() {
        return counter;
    }

    public static void increase() {
        counter++;
    }
}
