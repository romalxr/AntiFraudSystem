package antifraud.util;

public class LuhnFormula {

    public static boolean validate(String input) {
        char[] chars = convertToArrayOfValidChars(input);
        return getSum(chars) % 10 == 0;
    }

    private static char[] convertToArrayOfValidChars(String input) {
        String sanitized = input.replaceAll("[^\\d]", "");
        return sanitized.toCharArray();
    }

    private static int getSum(char[] chars) {
        int sum = 0;
        for (int i = 0; i < chars.length; i++) {
            int number = getInReverseOrder(chars, i);
            sum += getElementValue(i, number);
        }
        return sum;
    }

    private static int getInReverseOrder(char[] chars, int i) {
        int indexInReverseOrder = chars.length - 1 - i;
        char character = chars[indexInReverseOrder];
        return Character.getNumericValue(character);
    }

    private static int getElementValue(int i, int number) {
        if (i % 2 != 0) {
            return getOddElementValue(number);
        } else {
            return number;
        }
    }

    private static int getOddElementValue(int element) {
        int value = element * 2;
        if (value <= 9) {
            return value;
        }
        return value - 9;
    }
}
