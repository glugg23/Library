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
        }

        try {
            connection = DriverManager.getConnection(login.getProperty("server"), login.getProperty("username"), login.getProperty("password"));

        } catch(Exception e) {
            e.printStackTrace();
        }

        User user;
        Scanner in = new Scanner(System.in);

        do {
            System.out.print("Enter your username: ");
            String username = in.next();
            System.out.print("Enter you password: ");
            String password = in.next();

            user = new User(username, password);

            //Find all users that match this username
            String query = "SELECT * FROM users " +
                           "WHERE username='" + username + "';";
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
                            query = "INSERT INTO users (username, password) " +
                                    "VALUES ('" + username + "','" + password + "');";
                            statement.execute(query);
                            user.toggleLoggedIn();
                            System.out.println("You have successfully logged in.");
                            break;
                        }

                    } while(input != 'n');

                } else {
                    //Otherwise check to see if password matches
                    while(resultSet.next()) {
                        if(user.getPassword().equals(resultSet.getString("password"))) {
                            user.toggleLoggedIn();
                            System.out.println("You have successfully logged in.");
                            break;
                        }
                    }
                    //If no results match the given password
                    if(!user.isLoggedIn()) {
                        System.out.println("Wrong username or password.\n");
                        resultSet.close();
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

        } while(!user.isLoggedIn());
    }
}