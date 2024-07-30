import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the first binary number: ");
        String binary1 = scanner.nextLine();
        
        System.out.print("Enter the second binary number: ");
        String binary2 = scanner.nextLine();

        System.out.println("Choice of Rounding");
        System.out.println("[R] Rounding");
        System.out.println("[G] GRS");

        System.out.print("Enter rounding mode (R for Rounding, G for GRS Rounding): ");
        String roundingMode = scanner.nextLine().trim();

        int digitsSupported = 7;
        if(roundingMode.equalsIgnoreCase("G")){
            System.out.print("Enter the number of digits supported: ");
            digitsSupported = Integer.parseInt(scanner.nextLine());
        }

        if (!roundingMode.equalsIgnoreCase("R") && !roundingMode.equalsIgnoreCase("G")) {
            System.out.println("Invalid input. Please enter 'R' or 'G'.");
            return;
        }

        Binary32Calculator calculator = new Binary32Calculator(binary1, binary2, roundingMode, digitsSupported);
        String result = calculator.performAddition();

        System.out.println(result);

        System.out.print("Do you want to save the results to a text file? (y/n): ");
        char saveToFile = scanner.next().charAt(0);
        if (saveToFile == 'y') {
            try (FileWriter writer = new FileWriter("output.txt")) {
                writer.write(result);
                System.out.println("Output saved to output.txt");
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        }

        scanner.close();

    }
}
