package pianosystem;

public class Duration {
    private int numerator, denominator;

    public Duration(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public String toString() {
        return numerator +"/"+denominator;
    }

    public boolean isQuarter() {
        return denominator == 4;
    }
}
