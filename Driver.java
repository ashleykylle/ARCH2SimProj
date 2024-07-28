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

        System.out.print("Enter rounding mode (N for nearest, Z for toward zero, P for positive infinity, F for negative infinity): ");
        char roundingMode = scanner.next().charAt(0);

        System.out.print("Enter the number of digits supported: ");
        int digitsSupported = scanner.nextInt();

        Binary32Calculator calculator = new Binary32Calculator(binary1, binary2, roundingMode, digitsSupported);

        String output = calculator.performAddition();

        System.out.println(output);

        System.out.print("Do you want to save the output to a text file? (y/n): ");
        char saveToFile = scanner.next().charAt(0);
        if (saveToFile == 'y') {
            try (FileWriter writer = new FileWriter("output.txt")) {
                writer.write(output);
                System.out.println("Output saved to output.txt");
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}
