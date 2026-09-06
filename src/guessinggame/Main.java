package guessinggame;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        int secret = random.nextInt(100) + 1;
        int attempts = 0;
        int guess = 0;

        System.out.println("=== Number Guessing Game ===");
        System.out.println("Guess a number between 1 and 100.");

        while (guess != secret) {
            System.out.print("Your guess: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter a whole number.");
                scanner.next();
                continue;
            }

            guess = scanner.nextInt();
            attempts++;

            if (guess < secret) {
                System.out.println("Too low!");
            } else if (guess > secret) {
                System.out.println("Too high!");
            } else {
                System.out.println("Correct! Attempts: " + attempts);
            }
        }

        scanner.close();
    }
}
