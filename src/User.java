import java.util.Objects;

public class User {
    private String username;
    private String password;
    private boolean loggedIn = false;
    private Book book = null;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setBorrowedBook(Book book) {
        this.book = book;
    }

    public Book getBook() {
        return book;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void toggleLoggedIn() {
        this.loggedIn = !this.loggedIn;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(password, user.password);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}