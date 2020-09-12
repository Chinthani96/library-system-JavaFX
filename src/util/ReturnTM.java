package util;

public class ReturnTM {

    private String borrowId;
    private String isbn;
    private String title;

    public ReturnTM() {
    }

    public ReturnTM(String borrowId, String isbn, String title) {
        this.borrowId = borrowId;
        this.isbn = isbn;
        this.title = title;
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

    @Override
    public String toString() {
        return "ReturnTM{" +
                "borrowId='" + borrowId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}

