import java.sql.Connection;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Scanner;

class Menu {
    static void basicMenu(User user, Connection connection, Scanner in) {
        boolean menuOpenedAgain = false;

        System.out.println("Main Menu\n" +
                            "\t1 - Loan a book\n" +
                            "\t2 - Return a book\n" +
                            "\t3 - Search for a book\n" +
                            "\t0 - Exit and logout\n" +
                            "Please enter your choice:\n");

        int choice;

        do {
            if(menuOpenedAgain) {
                System.out.println("Main Menu\n" +
                            "\t1 - Loan a book\n" +
                            "\t2 - Return a book\n" +
                            "\t3 - Search for a book\n" +
                            "\t0 - Exit and logout\n" +
                            "Please enter your choice:\n");
            }

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
                    menuOpenedAgain = true;
                    break;
                case 2:
                    returnMenu(user, connection, in);
                    menuOpenedAgain = true;
                    break;
                case 3:
                    System.out.println("Search Menu");
                    menuOpenedAgain = true;
                    break;
                case 0:
                    System.out.println("You have logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
                    //Clear input buffer as this will trigger when the user enters invalid input
                    in.nextLine();
                    menuOpenedAgain = true;
                    break;
            }

        } while(choice != 0);
    }

    static private void returnMenu(User user, Connection connection, Scanner in) {
        System.out.println("Returns Menu");
        if(user.getBook() != null) {
            System.out.println("You have borrowed " + user.getBook().getTitle() + " by " + user.getBook().getAuthor());
            System.out.println("\t1 - Return this book\n" +
                               "\t0 - Go back to main menu\n");

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
                        String queryBooks = String.format("UPDATE books SET isBorrowed=0, borrowedBy=null, returnDate=null WHERE title='%s';", user.getBook().getTitle());

                        String queryUsers = String.format("UPDATE users SET book=null WHERE username='%s';", user.getUsername());

                        try {
                            Statement statement = connection.createStatement();
                            statement.execute(queryBooks);
                            statement.execute(queryUsers);

                        } catch(Exception e) {
                            System.out.println("There was an error, please try again.");
                            e.printStackTrace();
                        }

                        System.out.println("Success!");
                        return;
                    case 0:
                        break;
                    default:
                        System.out.println("Invalid option.");
                        //Clear input buffer as this will trigger when the user enters invalid input
                        in.nextLine();
                        break;
                }

            } while(choice != 0);

        } else {
            System.out.println("You have not borrowed a book.\n");
        }
    }
}