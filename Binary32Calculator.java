public class Binary32Calculator {
    private String binary1;
    private String binary2;
    private String roundingMode;

    public Binary32Calculator(String binary1, String binary2, String roundingMode) {
        this.binary1 = binary1;
        this.binary2 = binary2;
        this.roundingMode = roundingMode;
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

        float sum = number1 + number2;
        String scientificSum = toScientificNotation(floatToStandardBinary(sum));

        output.append("3. Addition operation:\n");
        output.append("   Sum (Normalized form): ").append(scientificSum).append("\n");

        output.append("4. Rounding:\n");
        sum = roundFloat(sum, roundingMode);
        output.append("   Rounded Sum: ").append(sum).append("\n\n");

        output.append("Binary Answer: ").append(scientificSum).append("\n");
        output.append("Decimal Answer: ").append(sum).append("\n");

        return output.toString();
    }

    public String floatToStandardBinary(float number) {
    // Handle the integer part
    int integerPart = (int) number;
    String integerBinary = Integer.toBinaryString(integerPart);
    
    // Handle the fractional part
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

        // Limit the length of the fractional part to prevent infinite loops for non-terminating binary fractions
        if (fractionalBinary.length() > 32) {
            break;
        }
    }

    // Combine the integer and fractional parts
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
            // Handle non-fractional binaries
            index = binary.length(); // Treat as if the decimal point is at the end
            normalized = binary;
            exponent = index - 1;
        } else {
            // Handle fractional binaries
            normalized = binary.replace(".", "");
            exponent = index - 1;
        }

        // Normalize the mantissa
        int firstOneIndex = normalized.indexOf('1');
        normalized = normalized.substring(firstOneIndex);
        exponent -= firstOneIndex;

        // If the mantissa is "1", pad with three zeros
        if (normalized.equals("1")) {
            normalized += "000";
        }

        return normalized.charAt(0) + "." + normalized.substring(1) + " x 2^" + exponent;
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
            sb.insert(0, '0'); // Insert '0' at the beginning
        }
        if (index + shift == 0) {
            sb.insert(0, '0'); // Insert '0' at the beginning if decimal point is at the start
        }
        sb.insert(index, '.'); // Insert new decimal point at the correct position
        return sb.toString();
    }

    private float roundFloat(float value, String mode) {
        // Implement rounding based on the mode
        // For simplicity, we'll use Math.round for now
        return Math.round(value);
    }

private String normalizeToExponentZero(String scientificNotation) {
    int exponentIndex = scientificNotation.indexOf(" x 2^");
    String mantissa = scientificNotation.substring(0, exponentIndex);
    int exponent = Integer.parseInt(scientificNotation.substring(exponentIndex + 5));

    // Shift the mantissa and adjust the exponent
    while (exponent != 0) {
        if (exponent > 0) {
            mantissa = mantissa.replace(".", "");
            mantissa = mantissa.charAt(0) + "." + mantissa.substring(1);
            exponent--;
        } else {
            if (mantissa.charAt(0) != '0') {
                mantissa = "0." + mantissa.replace(".", "");
            } else {
                mantissa = mantissa.charAt(0) + "." + mantissa.substring(1);
            }
            exponent++;
        }
    }

    return mantissa + " x 2^0";
}


}
