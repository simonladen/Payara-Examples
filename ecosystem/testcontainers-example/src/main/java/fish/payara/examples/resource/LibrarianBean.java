package fish.payara.examples.resource;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

import fish.payara.examples.domain.Librarian;
import fish.payara.examples.service.LibrarianService;

@Named("librarianBean")
@ViewScoped
public class LibrarianBean implements Serializable {

    @Inject
    private transient LibrarianService librarianService;

    private Librarian librarian = new Librarian();

    public Librarian getLibrarian() {
        return librarian;
    }

    public List<Librarian> getAllLibrarians() {
        return librarianService.findAll();
    }

    public String create() {
      
        return null;
    }
    public String save() {
        // isBlank(), not just == null: librarian.xhtml's id field is a plain
        // h:inputText, so saving it with that field left blank submits ""
        // rather than null (see Librarian.assignId() for the full story).
        // Without this check, "" reads as "already has an id" and this would
        // call edit()/merge() on a brand new librarian instead of create().
        if (librarian.getLibrarianID() == null || librarian.getLibrarianID().isBlank()) {
             librarianService.create(librarian);
        } else {
             librarianService.edit(librarian);
        }
        librarian = new Librarian(); // reset
        return null;
    }

    public String remove(String librarianID) {
        librarianService.remove(librarianService.find(librarianID));
        return null;
    }

    public String edit(Librarian p) {
        this.librarian = p;
        return null;
    }

}