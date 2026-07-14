package fish.payara.examples.domain;

import jakarta.persistence.*;
import java.util.Objects;

import java.util.List;
import java.util.UUID;
import jakarta.json.bind.annotation.JsonbTransient;

@NamedQueries({
    @NamedQuery(name = "Patron.findByPatronID", query = "SELECT e FROM Patron e WHERE e.patronID = :patronID"),
    @NamedQuery(name = "Patron.findByName", query = "SELECT e FROM Patron e WHERE e.name = :name"),
    @NamedQuery(name = "Patron.findByAddress", query = "SELECT e FROM Patron e WHERE e.address = :address"),
    @NamedQuery(name = "Patron.findByEmail", query = "SELECT e FROM Patron e WHERE e.email = :email")
})
@Entity
public class Patron {

    // Plain @Id, no @GeneratedValue, with an isBlank() check in @PrePersist:
    // see Librarian.assignId() for the full explanation - patron.xhtml's
    // "Patron ID:" field is a plain h:inputText bound directly to patronID,
    // so saving it blank submits "" (not null) for this String property,
    // which needs the same isBlank() guard to avoid persisting a literal
    // empty-string id.
    @Id
    private String patronID;

    @PrePersist
    private void assignId() {
        if (patronID == null || patronID.isBlank()) {
            patronID = UUID.randomUUID().toString();
        }
    }

    private String name;

    private String address;

    private String email;

    @JsonbTransient
    @OneToMany(mappedBy = "patron")
    private List<Loan> loans;


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.patronID);
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
        final Patron other = (Patron) obj;
        return Objects.equals(this.patronID, other.patronID);
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    // Getters and setters

    public String getPatronID() {
        return patronID;
    }

    public void setPatronID(String patronID) {
        this.patronID = patronID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void setLoans(List<Loan> loans) {
        this.loans = loans;
    }

}
