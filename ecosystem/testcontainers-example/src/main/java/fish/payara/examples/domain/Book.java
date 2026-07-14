package fish.payara.examples.domain;

import jakarta.persistence.*;
import java.util.Objects;

import java.util.List;
import java.util.UUID;
import jakarta.json.bind.annotation.JsonbTransient;

@NamedQueries({
    @NamedQuery(name = "Book.findByIsbn", query = "SELECT e FROM Book e WHERE e.isbn = :isbn"),
    @NamedQuery(name = "Book.findByTitle", query = "SELECT e FROM Book e WHERE e.title = :title"),
    @NamedQuery(name = "Book.findByAuthor", query = "SELECT e FROM Book e WHERE e.author = :author"),
    @NamedQuery(name = "Book.findByPages", query = "SELECT e FROM Book e WHERE e.pages = :pages")
})
@Entity
public class Book {

    // Plain @Id, no @GeneratedValue, with an isBlank() check in @PrePersist:
    // see Librarian.assignId() for the full explanation - book.xhtml's
    // "Isbn:" field is a plain h:inputText bound directly to isbn, so saving
    // it blank submits "" (not null) for this String property, which needs
    // the same isBlank() guard to avoid persisting a literal empty-string id.
    // Note: this is a generated surrogate key, not a real ISBN - the field
    // name predates this fix.
    @Id
    private String isbn;

    @PrePersist
    private void assignId() {
        if (isbn == null || isbn.isBlank()) {
            isbn = UUID.randomUUID().toString();
        }
    }

    private String title;

    private String author;

    private Integer pages;

    @JsonbTransient
    @OneToMany(mappedBy = "book")
    private List<Loan> loans;


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.isbn);
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
        final Book other = (Book) obj;
        return Objects.equals(this.isbn, other.isbn);
    }

    @Override
    public String toString() {
        return String.valueOf(title);
    }

    // Getters and setters

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

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

}
