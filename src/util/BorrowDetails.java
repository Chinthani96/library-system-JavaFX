package util;

public class BorrowDetails {
    private String borrowId;
    private String memberId;
    private String isbn;
    private String date;

    public BorrowDetails() {
    }

    public BorrowDetails(String borrowId, String memberId, String isbn, String date) {
        this.borrowId = borrowId;
        this.memberId = memberId;
        this.isbn = isbn;
        this.date = date;
    }

    public String getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(String borrowId) {
        this.borrowId = borrowId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "BorrowDetails{" +
                "borrowId='" + borrowId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
