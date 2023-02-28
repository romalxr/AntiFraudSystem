package antifraud.util;

import antifraud.entity.Validity;

import java.util.concurrent.atomic.AtomicLong;

public class LimitsUtil {
    public static AtomicLong MAX_ALLOWED = new AtomicLong(200L);
    public static AtomicLong MAX_MANUAL = new AtomicLong(1500L);

    public static void updateLimits(Validity result, Validity feedback, Long amount) {
        if (feedback.equals(Validity.ALLOWED)) {
            MAX_ALLOWED.set(increasedLimit(MAX_ALLOWED.get(), amount));
            if (result.equals(Validity.PROHIBITED)) {
                MAX_MANUAL.set(increasedLimit(MAX_MANUAL.get(), amount));
            }
        } else if (feedback.equals(Validity.MANUAL_PROCESSING)) {
            if (result.equals(Validity.ALLOWED)) {
                MAX_ALLOWED.set(decreasedLimit(MAX_ALLOWED.get(), amount));
            } else {
                MAX_MANUAL.set(increasedLimit(MAX_MANUAL.get(), amount));
            }
        } else {
            MAX_MANUAL.set(decreasedLimit(MAX_MANUAL.get(), amount));
            if (result.equals(Validity.ALLOWED)) {
                MAX_ALLOWED.set(decreasedLimit(MAX_ALLOWED.get(), amount));
            }
        }
    }

    private static long increasedLimit(long limit, long amount) {
        return (long) Math.ceil(0.8 * limit + 0.2 * amount);
    }

    private static long decreasedLimit(long limit, long amount) {
        return (long) Math.ceil(0.8 * limit - 0.2 * amount);
    }
}
