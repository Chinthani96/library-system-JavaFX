package util;

public class BooksTM {
    private String ISBN;
    private String title;
    private String author;
    private String edition;

    public BooksTM() {
    }

    public BooksTM(String ISBN, String title, String author, String edition) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.edition = edition;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return ISBN;
    }
}
