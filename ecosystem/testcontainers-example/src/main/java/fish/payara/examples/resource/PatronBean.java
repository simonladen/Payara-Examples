package fish.payara.examples.resource;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

import fish.payara.examples.domain.Patron;
import fish.payara.examples.service.PatronService;

@Named("patronBean")
@ViewScoped
public class PatronBean implements Serializable {

    @Inject
    private transient PatronService patronService;

    private Patron patron = new Patron();

    public Patron getPatron() {
        return patron;
    }

    public List<Patron> getAllPatrons() {
        return patronService.findAll();
    }

    public String create() {
      
        return null;
    }
    public String save() {
        // isBlank(), not just == null: see LibrarianBean#save for why a
        // plain h:inputText bound to a String id needs this guard too.
        if (patron.getPatronID() == null || patron.getPatronID().isBlank()) {
             patronService.create(patron);
        } else {
             patronService.edit(patron);
        }
        patron = new Patron(); // reset
        return null;
    }

    public String remove(String patronID) {
        patronService.remove(patronService.find(patronID));
        return null;
    }

    public String edit(Patron p) {
        this.patron = p;
        return null;
    }

}