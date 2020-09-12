package util;

import java.util.Date;

public class BorrowTM {

    private String borrowId;
    private String isbn;
    private String title;
    private String author;

    public BorrowTM() {
    }

    public BorrowTM(String borrowId, String isbn, String title, String author) {
        this.borrowId = borrowId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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
        return "BorrowTM{" +
                "borrowId='" + borrowId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
