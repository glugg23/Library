import java.sql.Connection;
import java.util.InputMismatchException;
import java.util.Scanner;

class Menu {
    static void basicMenu(User user, Connection connection, Scanner in) {
        System.out.println("Main Menu\n" +
                            "\t1 - Loan a book\n" +
                            "\t2 - Return a book\n" +
                            "\t3 - Search for a book\n" +
                            "\t0 - Exit and logout\n" +
                            "Please enter your choice:\n");

        int choice;

        do {
            System.out.print(user.getUsername() + ": ");
            try {
                choice = in.nextInt();

            } catch(InputMismatchException e) {
                //If the user enters something wrong set choice to a still valid number
                choice = -1;
            }

            switch(choice) {
                case 1:
                    System.out.println("Loan Menu");
                    break;
                case 2:
                    System.out.println("Return Menu");
                    break;
                case 3:
                    System.out.println("Search Menu");
                    break;
                case 0:
                    System.out.println("You have logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
                    //Clear input buffer as this will trigger when the user enters invalid input
                    in.nextLine();
                    break;
            }

        } while(choice != 0);
    }
}