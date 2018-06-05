import java.sql.*;
import java.util.Properties;
import java.io.FileReader;
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
            String username = in.nextLine();
            System.out.print("Enter you password: ");
            String password = in.nextLine();

            user = new User(username, password);

            String query = "SELECT * FROM users " +
                           "WHERE username='" + username + "' AND password='" + password + "';";
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);

                if (!resultSet.isBeforeFirst() ) {
                    System.out.println("No data");
                }

            } catch(Exception e) {
                e.printStackTrace();
            }


        } while(!user.isLoggedIn());
    }
}