import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Book {
    private String title;
    private String author;
    private String genre;
    private boolean isBorrowed = false;
    private int borrowedBy;
    private Instant returnDate;

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public Book(String title, String author, String genre, boolean borrowed, int id, Instant returnDate) {
        this.title = title;
        this.author = author;
        this.genre = genre;

        if(borrowed) {
            this.isBorrowed = true;
            this.borrowedBy = id;
            this.returnDate = returnDate;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public int borrowedBy() {
        return borrowedBy;
    }

    public Instant getReturnDate() {
        return returnDate;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(genre, book.genre);
    }

    @Override
    public String toString() {
        if(isBorrowed) {
            ZoneId zoneId = ZoneId.of("GMT");
            ZonedDateTime zdt = ZonedDateTime.ofInstant(returnDate, zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu.MM.dd.HH.mm.ss");
            String output = zdt.format(formatter);

            return "Book{" +
                    "title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", genre='" + genre + '\'' +
                    ", borrowedBy='" + borrowedBy + '\'' +
                    ", returnDate='" + output + " GMT" + '\'' +
                    '}';

        } else {
            return "Book{" +
                    "title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    ", genre='" + genre + '\'' +
                    '}';
        }
    }
}