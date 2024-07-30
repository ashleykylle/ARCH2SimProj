package src;
public class Binary32Calculator {
    private String binary1;
    private String binary2;
    private String roundingMode;
    private int digitsSupported;

    public Binary32Calculator(String binary1, String binary2, String roundingMode, int digitsSupported) {
        this.binary1 = binary1;
        this.binary2 = binary2;
        this.roundingMode = roundingMode;
        this.digitsSupported = digitsSupported;
    }

    public String performAddition() {
        StringBuilder output = new StringBuilder();
        output.append("Step-by-step operation:\n");

        float number1 = binaryToFloat(binary1);
        float number2 = binaryToFloat(binary2);

        String ieee1 = toIEEE754BinaryString(number1);
        String ieee2 = toIEEE754BinaryString(number2);

        String scientific1 = toScientificNotation(binary1);
        String scientific2 = toScientificNotation(binary2);

        output.append("1. Initial normalization:\n");
        output.append("   Number 1 (IEEE-754): ").append(ieee1).append("\n");
        output.append("   Number 2 (IEEE-754): ").append(ieee2).append("\n");
        output.append("   Number 1 (Normalized form): ").append(scientific1).append("\n");
        output.append("   Number 2 (Normalized form): ").append(scientific2).append("\n");

        int exponent1 = getExponentFromScientific(scientific1);
        int exponent2 = getExponentFromScientific(scientific2);

        String mantissa1 = getMantissaFromScientific(scientific1);
        String mantissa2 = getMantissaFromScientific(scientific2);

        if (exponent1 > exponent2) {
            int shift = exponent1 - exponent2;
            mantissa2 = shiftRight(mantissa2, shift);
            exponent2 = exponent1;
        } else if (exponent2 > exponent1) {
            int shift = exponent2 - exponent1;
            mantissa1 = shiftRight(mantissa1, shift);
            exponent1 = exponent2;
        }

        String normalized1 = mantissa1 + " x 2^" + exponent1;
        String normalized2 = mantissa2 + " x 2^" + exponent2;

        output.append("2. Normalize exponents:\n");
        output.append("   Number 1: ").append(normalized1).append("\n");
        output.append("   Number 2: ").append(normalized2).append("\n");

        if (roundingMode.equalsIgnoreCase("G")) {
            // Perform GRS rounding
            Operand operand1 = new Operand("0", exponent1, mantissa1); // Assume sign is "0"
            Operand operand2 = new Operand("0", exponent2, mantissa2);
        
            operand1 = useGRSRounding(operand1, digitsSupported);
            operand2 = useGRSRounding(operand2, digitsSupported);
        
            String rounded1 = operand1.magnitude + " x 2^" + operand1.exponent;
            String rounded2 = operand2.magnitude + " x 2^" + operand2.exponent;
        
            output.append("3. Normalized exponents with GRS rounding:\n");
            output.append("   Number 1 with GRS Rounding: ").append(rounded1).append("\n");
            output.append("   Number 2 with GRS Rounding: ").append(rounded2).append("\n");
        
            // Implement direct binary addition here
            String sum = addBinary(operand1.magnitude, operand2.magnitude);
            int resultExponent = operand1.exponent; // Assuming exponents are the same after normalization
        
            // Normalize the sum if necessary
            String normalizedSum = sum;
            int leadingZeros = 0;
        
            while (leadingZeros < normalizedSum.length() && normalizedSum.charAt(leadingZeros) == '0') {
                leadingZeros++;
            }
        
            if (leadingZeros < normalizedSum.length()) {
                normalizedSum = normalizedSum.substring(leadingZeros);
                resultExponent -= leadingZeros;
            } else {
                normalizedSum = "0";
            }
        
            output.append("4. Addition operation:\n");
            output.append("   Sum: ").append(normalizedSum).append(" x 2^").append(resultExponent).append("\n");
        
            // Normalize the binary sum
            String[] normalizedResult = normalizeBinary(normalizedSum, resultExponent, digitsSupported);
            String finalNormalizedSum = normalizedResult[0];
            int finalNormalizedExponent = Integer.parseInt(normalizedResult[1]);
        
            output.append("5. Normalized Sum:\n");
            output.append("   Normalized Sum: ").append(finalNormalizedSum).append(" x 2^").append(finalNormalizedExponent).append("\n");
        
            // Convert binary sum to decimal
            double decimalSum = binaryToDecimal(finalNormalizedSum) * Math.pow(2, finalNormalizedExponent);
        
            output.append("6. Decimal Answer:\n");
            output.append("   Decimal Sum: ").append(decimalSum).append("\n");
        
            return output.toString();
        }
        
        // Perform the addition operation
        float sum = binaryToFloat(binary1) + binaryToFloat(binary2);
        if (roundingMode.equalsIgnoreCase("R")) {
            sum = Math.round(sum);
        }
        String scientificSum = toScientificNotation(floatToStandardBinary(sum));

        // Limit the binary result to the user-specified number of digits, excluding the decimal point
        String limitedBinarySum = limitBinaryDigits(scientificSum, digitsSupported);
        System.out.println(scientificSum);
        System.out.println(limitedBinarySum);
        
        // Include the normalized exponent in the binary answer
        String normalizedExponent = " x 2^" + getExponentFromScientific(scientificSum);
        System.out.println(normalizedExponent);
        output.append("4. Addition operation:\n");
        output.append("   Sum (Normalized form): ").append(scientificSum).append("\n");

        output.append("5. Binary Answer:\n");
        output.append("   ").append(limitedBinarySum).append(normalizedExponent).append("\n");

        output.append("6. Decimal Answer:\n");
        output.append("   ").append(sum).append("\n");

        return output.toString();
    }


    private double binaryToDecimal(String binary) {
        double decimal = 0;
        int pointIndex = binary.indexOf('.');
        if (pointIndex == -1) {
            pointIndex = binary.length();
        }
    
        for (int i = 0; i < pointIndex; i++) {
            if (binary.charAt(i) == '1') {
                decimal += Math.pow(2, pointIndex - i - 1);
            }
        }
    
        for (int i = pointIndex + 1; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                decimal += Math.pow(2, pointIndex - i);
            }
        }
    
        return decimal;
    }

    private String[] normalizeBinary(String binary, int exponent, int digitsSupported) {

        int pointIndex = binary.indexOf('.');
    
        String binaryWithoutPoint = binary.replace(".", "");
    
        int firstOneIndex = binaryWithoutPoint.indexOf('1');
    
        int newExponent = exponent + (pointIndex - firstOneIndex - 1);
    
        String normalizedBinary = "1." + binaryWithoutPoint.substring(firstOneIndex + 1);
    
        if (normalizedBinary.length() > digitsSupported + 2) { // +2 for "1."
            normalizedBinary = normalizedBinary.substring(0, digitsSupported + 2);
        }
    
        return new String[]{normalizedBinary, String.valueOf(newExponent)};
    }

    public static String addBinary(String binary1, String binary2) {
        String[] parts1 = binary1.split("\\.");
        String[] parts2 = binary2.split("\\.");

        int maxFractionLength = Math.max(parts1[1].length(), parts2[1].length());
        parts1[1] = padRight(parts1[1], maxFractionLength);
        parts2[1] = padRight(parts2[1], maxFractionLength);

        int maxIntegerLength = Math.max(parts1[0].length(), parts2[0].length());
        parts1[0] = padLeft(parts1[0], maxIntegerLength);
        parts2[0] = padLeft(parts2[0], maxIntegerLength);

        String fractionalSum = addBinaryStrings(parts1[1], parts2[1]);

        String integerSum = addBinaryStrings(parts1[0], parts2[0]);

        if (fractionalSum.length() > maxFractionLength) {
            integerSum = addBinaryStrings(integerSum, "1");
            fractionalSum = fractionalSum.substring(1);
        }

        return integerSum + "." + fractionalSum;
    }

    public static String addBinaryStrings(String binary1, String binary2) {
        StringBuilder result = new StringBuilder();
        int carry = 0;

        for (int i = binary1.length() - 1; i >= 0; i--) {
            int bit1 = binary1.charAt(i) - '0';
            int bit2 = binary2.charAt(i) - '0';
            int sum = bit1 + bit2 + carry;
            result.append(sum % 2);
            carry = sum / 2;
        }

        if (carry != 0) {
            result.append(carry);
        }

        return result.reverse().toString();
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s).replace(' ', '0');
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s).replace(' ', '0');
    }

    private String removeTrailingZeroes(String binary) {
        int endIndex = binary.length() - 1;
        while (endIndex > 4 && binary.charAt(endIndex) == '0') {
            endIndex--;
        }
        return binary.substring(0, endIndex + 1);
    }

    private String limitBinaryDigits(String binary, int limit) {
        int count = 0;
        StringBuilder result = new StringBuilder();
        for (char c : binary.toCharArray()) {
            if (c != '.') {
                if(c == ' '){
                    break;
                }
                count++;
            }
            result.append(c);
            if (count == limit) {
                break;
            }
        }
        return result.toString();
    }

    public Operand useGRSRounding(Operand operand, int digitsSupported) {
        String[] opPartition = operand.magnitude.split("\\.");
        String mantissa = opPartition[1];

        digitsSupported = digitsSupported - 1; // remove LHS of binary point in counting

        StringBuilder binary = new StringBuilder();
        String stickyBit = "0";

        if (mantissa.length() < digitsSupported + 3) {
            binary.append(mantissa).append("0".repeat(digitsSupported + 3 - mantissa.length()));
        } else {
            for (int i = 0; i < mantissa.length(); i++) {
                if (i < digitsSupported + 2) {
                    binary.append(mantissa.charAt(i));
                } else {
                    if (mantissa.charAt(i) == '1') {
                        stickyBit = "1";
                        break;
                    }
                }
            }
            binary.append(stickyBit);
        }

        operand.magnitude = opPartition[0] + "." + binary.toString();

        return operand;
    }

    public String floatToStandardBinary(float number) {
        int integerPart = (int) number;
        String integerBinary = Integer.toBinaryString(integerPart);
        
        float fractionalPart = number - integerPart;
        StringBuilder fractionalBinary = new StringBuilder();
        
        while (fractionalPart > 0) {
            fractionalPart *= 2;
            if (fractionalPart >= 1) {
                fractionalBinary.append("1");
                fractionalPart -= 1;
            } else {
                fractionalBinary.append("0");
            }

            if (fractionalBinary.length() > 32) {
                break;
            }
        }

        if (fractionalBinary.length() > 0) {
            return integerBinary + "." + fractionalBinary.toString();
        } else {
            return integerBinary;
        }
    }

    public String floatToBinary(float number) {
        int bits = Float.floatToIntBits(number);
        StringBuilder binaryString = new StringBuilder(Integer.toBinaryString(bits));
        
        while (binaryString.length() < 32) {
            binaryString.insert(0, '0');
        }
        return binaryString.toString();
    }

    private float binaryToFloat(String binary) {
        int index = binary.indexOf('.');
        float number;
        if (index > -1) {
            String integralPart = binary.substring(0, index);
            String fractionalPart = binary.substring(index + 1);

            String normalizedBinary = integralPart + fractionalPart;
            int exponent = fractionalPart.length();
            exponent = -exponent;

            int binaryInt = Integer.parseInt(normalizedBinary, 2);

            number = (float) binaryInt / (1 << -exponent);
        } else {
            number = Integer.parseInt(binary, 2);
        }
        return number;
    }

    private String toIEEE754BinaryString(float value) {
        int bits = Float.floatToIntBits(value);
        return String.format("%32s", Integer.toBinaryString(bits)).replace(' ', '0');
    }

    private String toScientificNotation(String binary) {
        int index = binary.indexOf('.');
        String normalized;
        int exponent;

        if (index == -1) {
            index = binary.length();
            normalized = binary;
            exponent = index - 1;
        } else {
            normalized = binary.replace(".", "");
            exponent = index - 1;
        }

        int firstOneIndex = normalized.indexOf('1');
        normalized = normalized.substring(firstOneIndex);
        exponent -= firstOneIndex;

        if (normalized.equals("1")) {
            normalized += "000";
        }

        String done = normalized.charAt(0) + "." + normalized.substring(1);

        done = removeTrailingZeroes(done);

        return done + " x 2^" + exponent;
    }

    private int getExponentFromScientific(String scientific) {
        String[] parts = scientific.split(" x 2\\^");
        return Integer.parseInt(parts[1]);
    }

    private String getMantissaFromScientific(String scientific) {
        String[] parts = scientific.split(" x 2\\^");
        return parts[0];
    }

    private String shiftRight(String binary, int shift) {
        int index = binary.indexOf('.');
        if (index == -1) {
            index = binary.length();
        }
        StringBuilder sb = new StringBuilder(binary.replace(".", ""));
        for (int i = 0; i < shift; i++) {
            sb.insert(0, '0');
        }
        if (index + shift == 0) {
            sb.insert(0, '0');
        }
        sb.insert(index, '.');
        return sb.toString();
    }
}
