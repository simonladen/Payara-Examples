package fish.payara.examples.resource;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

import fish.payara.examples.domain.Book;
import fish.payara.examples.service.BookService;

@Named("bookBean")
@ViewScoped
public class BookBean implements Serializable {

    @Inject
    private transient BookService bookService;

    private Book book = new Book();

    public Book getBook() {
        return book;
    }

    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    public String create() {
      
        return null;
    }
    public String save() {
        // isBlank(), not just == null: see LibrarianBean#save for why a
        // plain h:inputText bound to a String id needs this guard too.
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
             bookService.create(book);
        } else {
             bookService.edit(book);
        }
        book = new Book(); // reset
        return null;
    }

    public String remove(String isbn) {
        bookService.remove(bookService.find(isbn));
        return null;
    }

    public String edit(Book p) {
        this.book = p;
        return null;
    }

}