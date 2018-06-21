import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.InputMismatchException;
import java.util.Scanner;

class Menu {
    static void basicMenu(User user, Connection connection, Scanner in) {
        int choice;

        do {
            System.out.println("Main Menu\n" +
                    "\t1 - Loan a book\n" +
                    "\t2 - Return a book\n" +
                    "\t3 - Search for a book\n" +
                    "\t0 - Exit and logout\n" +
                    "Please enter your choice:\n");

            System.out.print(user.getUsername() + ": ");
            try {
                choice = in.nextInt();

            } catch(InputMismatchException e) {
                //If the user enters something wrong set choice to a still valid number
                choice = -1;
            }

            switch(choice) {
                case 1:
                    loanMenu(user, connection, in);
                    break;
                case 2:
                    returnMenu(user, connection, in);
                    break;
                case 3:
                    searchMenu(user, connection, in);
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

    static private void loanMenu(User user, Connection connection, Scanner in) {
        System.out.println("Loan Menu\n" +
                "\t1 - Enter a book to take out\n" +
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
                    System.out.println("1 - Enter title of book\n" +
                                       "2 - Enter id of book\n");
                    System.out.print(user.getUsername() + ": ");

                    int nestedChoice;

                    try {
                        nestedChoice = in.nextInt();

                    } catch(InputMismatchException e) {
                        System.out.println("Invalid option.");
                        //Clear input buffer as this will trigger when the user enters invalid input
                        in.nextLine();
                        return;
                    }

                    //Another case where the choice is invalid
                    if(nestedChoice > 2) {
                        System.out.println("Invalid option.");
                        //Clear input buffer as this will trigger when the user enters invalid input
                        in.nextLine();
                        return;
                    }

                    //Clear input
                    in.nextLine();

                    if(nestedChoice == 1) {
                        titleBorrow(user, connection, in);

                    } else if(nestedChoice == 2) {
                        idBorrow(user, connection, in);
                    }

                    System.out.println("Success!\n");
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
    }

    static private void titleBorrow(User user, Connection connection, Scanner in) {
        System.out.print("Enter title: ");
        String title = in.nextLine();

        String query = String.format("SELECT * FROM books WHERE title='%s';", title);

        //Need to find date here so that it remains in scope longer
        Instant date = Instant.now().plus(7, ChronoUnit.DAYS);

        try {
            Statement statement = connection.createStatement();

            //Find id of chosen book
            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Book book = new Book(
                    rs.getInt("id"),
                    title,
                    rs.getString("author"),
                    rs.getString("genre"),
                    true,
                    user.getId(),
                    date
            );

            //Set user object to have borrowed this book
            user.setBorrowedBook(book);

        } catch(Exception e) {
            System.out.println("There was an error, please try again.");
            e.printStackTrace();
            return;
        }

        String queryBooks = String.format("UPDATE books SET isBorrowed=1, borrowedBy=%d, returnDate='%s' WHERE title='%s';", user.getId(), Timestamp.from(date), title);
        String queryUsers = String.format("UPDATE users SET book=%d WHERE username='%s';", user.getBook().getId(), user.getUsername());

        try {
            //Then update the database
            Statement statement = connection.createStatement();
            statement.execute(queryBooks);
            statement.execute(queryUsers);

        } catch(Exception e) {
            System.out.println("There was an error, please try again.");
            e.printStackTrace();
        }
    }

    static private void idBorrow(User user, Connection connection, Scanner in) {
        System.out.print("Enter id: ");
        int id;
        try {
            id = in.nextInt();

        } catch(InputMismatchException e) {
            System.out.println("Invalid input.");
            //Clear input buffer as this will trigger when the user enters invalid input
            in.nextLine();
            return;
        }

        String query = String.format("SELECT * FROM books WHERE id=%d", id);

        //Need to find date here so that it remains in scope longer
        Instant date = Instant.now().plus(7, ChronoUnit.DAYS);

        try {
            Statement statement = connection.createStatement();

            //Find title, etc of book
            ResultSet rs = statement.executeQuery(query);
            rs.next();

            Book book = new Book(
                    id,
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    true,
                    user.getId(),
                    date
            );

            //Set user object to have borrowed this book
            user.setBorrowedBook(book);

        } catch(Exception e) {
            System.out.println("There was an error, please try again.");
            e.printStackTrace();
            return;
        }

        String queryBooks = String.format("UPDATE books SET isBorrowed=1, borrowedBy=%d, returnDate='%s' WHERE id=%d;", user.getId(), Timestamp.from(date), id);
        String queryUsers = String.format("UPDATE users SET book=%d WHERE username='%s';", id, user.getUsername());

        try {
            //Then update the database
            Statement statement = connection.createStatement();
            statement.execute(queryBooks);
            statement.execute(queryUsers);

        } catch(Exception e) {
            System.out.println("There was an error, please try again.");
            e.printStackTrace();
        }
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

    static private void searchMenu(User user, Connection connection, Scanner in) {
        System.out.println("Search Menu\n" +
                "\t1 - Search by title\n" +
                "\t2 - Search by author\n" +
                "\t3 - Search by genre\n" +
                "\t4 - Show all books\n" +
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
                    searchTitle(connection, in);
                    break;
                case 2:
                    searchAuthor(connection, in);
                    break;
                case 3:
                    break;
                case 4:
                    searchAll(connection);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option.");
                    //Clear input buffer as this will trigger when the user enters invalid input
                    in.nextLine();
                    break;
            }

        } while(choice != 0);
    }

    static private void searchTitle(Connection connection, Scanner in) {
        //Clear input buffer
        in.nextLine();

        System.out.print("Enter title: ");
        String title = in.nextLine();

        String query = "SELECT * FROM books WHERE title LIKE '%" + title + "%';";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                if(rs.getBoolean("isBorrowed")) {
                    Book book = new Book(rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"),
                            rs.getBoolean("isBorrowed"),
                            rs.getInt("borrowedBy"),
                            rs.getTimestamp("returnDate").toInstant());

                    System.out.println(book.toString());

                } else {
                    Book book = new Book(rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"));

                    System.out.println(book.toString());
                }
            }

        } catch(Exception e) {
            System.out.println("There was an error, please try again.");
            e.printStackTrace();
        }
    }

    static private void searchAuthor(Connection connection, Scanner in) {
        //Clear input buffer
        in.nextLine();

        System.out.print("Enter author: ");
        String author = in.nextLine();

        String query = "SELECT * FROM books WHERE author LIKE '%" + author + "%';";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                if(rs.getBoolean("isBorrowed")) {
                    Book book = new Book(rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"),
                            rs.getBoolean("isBorrowed"),
                            rs.getInt("borrowedBy"),
                            rs.getTimestamp("returnDate").toInstant());

                    System.out.println(book.toString());

                } else {
                    Book book = new Book(rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"));

                    System.out.println(book.toString());
                }
            }

        } catch(Exception e) {
            System.out.println("There was an error, please try again.");
            e.printStackTrace();
        }
    }

    static private void searchAll(Connection connection) {
        String query = "SELECT * FROM books;";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()) {
                if(rs.getBoolean("isBorrowed")) {
                    Book book = new Book(rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"),
                            rs.getBoolean("isBorrowed"),
                            rs.getInt("borrowedBy"),
                            rs.getTimestamp("returnDate").toInstant());

                    System.out.println(book.toString());

                } else {
                    Book book = new Book(rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"));

                    System.out.println(book.toString());
                }
            }

        } catch(Exception e) {
            System.out.println("There was an error, please try again.");
            e.printStackTrace();
        }
    }
}