package pianosystem;

public class Duration {
    private int numerator, denominator;

    public Duration(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public Duration() {
        numerator = denominator = 0;
    }

    public void changeDuration(int num, int denom) {
        numerator = num;
        denominator = denom;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

    public boolean isQuarter() {
        return denominator == 4;
    }

    public long toMilis() {
        return denominator == 4 ? 300 : 150;
    }

    public static boolean equals(Duration d1, Duration d2) {
        return ((d1.numerator == d2.numerator) && (d1.denominator == d2.denominator));
    }
}
