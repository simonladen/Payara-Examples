package fish.payara.examples.domain;

import jakarta.persistence.*;
import java.util.Objects;

import java.time.LocalDateTime;

@NamedQueries({
    @NamedQuery(name = "Loan.findByLoanID", query = "SELECT e FROM Loan e WHERE e.loanID = :loanID"),
    @NamedQuery(name = "Loan.findByLoanDate", query = "SELECT e FROM Loan e WHERE e.loanDate = :loanDate"),
    @NamedQuery(name = "Loan.findByReturnDate", query = "SELECT e FROM Loan e WHERE e.returnDate = :returnDate")
})
@Entity
public class Loan {

    // GenerationType.TABLE (not AUTO or IDENTITY): EclipseLink's AUTO resolution for a
    // numeric @Id on H2 still picks the platform's native IDENTITY column generation,
    // which produces "INTEGER IDENTITY NOT NULL" DDL that this H2 version's parser
    // rejects (a real schema-creation failure, not a test issue - see the container
    // logs: CREATE TABLE LOAN fails, so the table never exists). TABLE forces the same
    // EclipseLink default table-based generator (the SEQUENCE table) that Book/Patron/
    // Librarian's AUTO-strategy String ids already use successfully.
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer loanID;

    private LocalDateTime loanDate;

    private LocalDateTime returnDate;

    @ManyToOne
    @JoinColumn(name = "librarian_id")
    private Librarian librarian;

    @ManyToOne
    @JoinColumn(name = "patron_id")
    private Patron patron;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.loanID);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Loan other = (Loan) obj;
        return Objects.equals(this.loanID, other.loanID);
    }

    @Override
    public String toString() {
        return String.valueOf(loanID);
    }

    // Getters and setters

    public Integer getLoanID() {
        return loanID;
    }

    public void setLoanID(Integer loanID) {
        this.loanID = loanID;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public Patron getPatron() {
        return patron;
    }

    public void setPatron(Patron patron) {
        this.patron = patron;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

}
