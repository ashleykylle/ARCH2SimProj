package src;

public class Driver {
    public static void main(String[] args) {

        String binary1 = args[0];
        String binary2 = args[1];
        String roundingMode = args[2];
        int digitsSupported = 32; // defaults to 32

        if (roundingMode.equals("G") && args.length == 4) {
            digitsSupported = Integer.parseInt(args[3]);
        }

        Binary32Calculator calculator = new Binary32Calculator(binary1, binary2, roundingMode, digitsSupported);
        String result = calculator.performAddition();

        System.out.println(result);

        try (java.io.FileWriter writer = new java.io.FileWriter("output.txt")) {
            writer.write(result);
        } catch (java.io.IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

}