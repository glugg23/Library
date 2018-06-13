import org.apache.commons.codec.digest.Crypt;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Connection connection = null;
        Properties login = new Properties();

        try(FileReader in = new FileReader("login.properties")) {
            login.load(in);

        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            connection = DriverManager.getConnection(login.getProperty("server"), login.getProperty("username"), login.getProperty("password"));

        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        User user = null;
        Scanner in = new Scanner(System.in);

        do {
            System.out.print("Enter your username: ");
            String username = in.nextLine();
            System.out.print("Enter you password: ");
            String password = in.nextLine();

            //Find all users that match this username
            //You can probably get an SQL injection from this but oh well...
            String query = String.format("SELECT * FROM users WHERE username='%s';", username);

            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                //If username isn't found, give option to create new user
                if(!resultSet.isBeforeFirst()) {
                    System.out.println("This user does not exist.");
                    char input;

                    do {
                        System.out.print("Would you like to create a new user? (y/n): ");
                        input = in.next().charAt(0);

                        if(input == 'y') {
                            query = String.format("INSERT INTO users(username, password) VALUES('%s', '%s');", username, Crypt.crypt(password));

                            statement.execute(query);

                            query = String.format("SELECT * FROM users WHERE username='%s';", username);
                            ResultSet rs = statement.executeQuery(query);
                            rs.next();

                            user = new User(rs.getInt("id"), username, Crypt.crypt(password));

                            user.toggleLoggedIn();
                            System.out.println("You have successfully logged in.\n");
                            rs.close();
                            resultSet.close();
                            break;
                        }

                        in.nextLine();
                    } while(input != 'n');

                } else {
                    //Otherwise check to see if password matches
                    while(resultSet.next()) {
                        if(resultSet.getString("password").equals(Crypt.crypt(password, resultSet.getString("password")))) {

                            user = new User(resultSet.getInt("id"), username, Crypt.crypt(password));

                            //If there is a borrowed book get data for that book
                            if(resultSet.getInt("book") != 0) {
                                query = String.format("SELECT * FROM books WHERE id=%d;", resultSet.getInt("book"));

                                ResultSet rs = statement.executeQuery(query);
                                rs.next();

                                Book book = new Book(rs.getInt("id"),
                                        rs.getString("title"),
                                        rs.getString("author"),
                                        rs.getString("genre"),
                                        true,
                                        rs.getInt("borrowedBy"),
                                        rs.getTimestamp("returnDate").toInstant());

                                user.setBorrowedBook(book);
                            }

                            user.toggleLoggedIn();
                            System.out.println("You have successfully logged in.\n");
                            resultSet.close();
                            break;
                        }
                    }
                    //If no results match the given password
                    if(user == null || !user.isLoggedIn()) {
                        System.out.println("Wrong username or password.\n");
                        resultSet.close();
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

        } while(user == null || !user.isLoggedIn());

        //Pass scanner in to avoid creating a new one
        Menu.basicMenu(user, connection, in);

        in.close();
    }
}