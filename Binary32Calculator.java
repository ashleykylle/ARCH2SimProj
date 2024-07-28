
public class Binary32Calculator {
    private String binary1;
    private String binary2;
    private char roundingMode;
    private int digitsSupported;

    public Binary32Calculator(String binary1, String binary2, char roundingMode, int digitsSupported) {
        this.binary1 = binary1;
        this.binary2 = binary2;
        this.roundingMode = roundingMode;
        this.digitsSupported = digitsSupported;
    }

    public String performAddition() {
        StringBuilder output = new StringBuilder();
        output.append("Step-by-step operation:\n");

        int decimal1 = Integer.parseInt(binary1, 2);
        int decimal2 = Integer.parseInt(binary2, 2);

        float number1 = decimalToFloat(decimal1);
        float number2 = decimalToFloat(decimal2);
        
        String ieee1 = toIEEE754BinaryString(number1);
        String ieee2 = toIEEE754BinaryString(number2);

        String scientific1 = toScientificNotation(binary1);
        String scientific2 = toScientificNotation(binary2);

        output.append("1. Initial normalization:\n");
        output.append("   Number 1 (IEEE-754): ").append(ieee1).append("\n");
        output.append("   Number 1 (Normalized form): ").append(scientific1).append("\n");
        output.append("   Number 2 (IEEE-754): ").append(ieee2).append("\n");
        output.append("   Number 2 (Normalized form): ").append(scientific2).append("\n");

        int exponent1 = binary1.length() - 1;
        int exponent2 = binary2.length() - 1;

        if (exponent1 > exponent2) {
            int shift = exponent1 - exponent2;
            binary2 = shiftRight(binary2, shift);
            exponent2 = exponent1;
        } else if (exponent2 > exponent1) {
            int shift = exponent2 - exponent1;
            binary1 = shiftRight(binary1, shift);
            exponent1 = exponent2;
        }

        scientific1 = toScientificNotation(binary1);
        scientific2 = toScientificNotation(binary2);

        output.append("2. Normalize exponents:\n");
        output.append("   Number 1 (Normalized form): ").append(scientific1).append("\n");
        output.append("   Number 2 (Normalized form): ").append(scientific2).append("\n");

        int mantissa1 = Integer.parseInt(binary1, 2);
        int mantissa2 = Integer.parseInt(binary2, 2);
        int mantissaSum = mantissa1 + mantissa2;
        String mantissaSumBinary = Integer.toBinaryString(mantissaSum);
        String scientificSum = toScientificNotation(mantissaSumBinary, exponent1);

        output.append("3. Addition operation:\n");
        output.append("   Sum (Normalized form): ").append(scientificSum).append("\n");

        float sum = mantissaSum / (float) Math.pow(2, mantissaSumBinary.length() - 1);
        sum *= Math.pow(2, exponent1);

        output.append("4. Post-operation normalization:\n");
        output.append("   Normalized Sum: ").append(sum).append("\n");

        output.append("5. Rounding:\n");
        sum = roundFloat(sum, roundingMode);
        output.append("   Rounded Sum: ").append(sum).append("\n");

        output.append("Final Answer: ").append(sum).append("\n");

        return output.toString();
    }

    private float decimalToFloat(int decimal) {
        return (float) decimal;
    }

    private String toIEEE754BinaryString(float value) {
        int intBits = Float.floatToIntBits(value);
        return String.format("%32s", Integer.toBinaryString(intBits)).replace(' ', '0');
    }

    private String toScientificNotation(String binary) {
        int exponent = binary.length() - 1;
        String mantissa = binary.charAt(0) + "." + binary.substring(1);

        while (mantissa.length() < 5) {
            mantissa += "0";
        }

        return mantissa + " x 2^" + exponent;
    }

    private String toScientificNotation(String binary, int exponent) {
        String mantissa = binary.charAt(0) + "." + binary.substring(1);

        while (mantissa.length() < 5) {
            mantissa += "0";
        }

        return mantissa + " x 2^" + exponent;
    }

    private String shiftRight(String binary, int positions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < positions; i++) {
            sb.append("0");
        }
        return sb.append(binary).toString();
    }

    private float roundFloat(float value, char roundingMode) {
        switch (roundingMode) {
            case 'N': // Round to nearest
                return Math.round(value);
            case 'Z': // Round toward zero
                return (float) ((int) value);
            case 'P': // Round toward positive infinity
                return (float) Math.ceil(value);
            case 'F': // Round toward negative infinity
                return (float) Math.floor(value);
            default:
                return value;
        }
    }
}
