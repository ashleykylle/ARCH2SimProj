package src;

public class Operand {
    String sign;
    int exponent;
    String magnitude;

    public Operand(String sign, int exponent, String magnitude) {
        this.sign = sign;
        this.exponent = exponent;
        this.magnitude = magnitude;
    }

    @Override
    public String toString() {
        return "Operand{" +
                "sign='" + sign + '\'' +
                ", exponent=" + exponent +
                ", magnitude='" + magnitude + '\'' +
                '}';
    }
}
