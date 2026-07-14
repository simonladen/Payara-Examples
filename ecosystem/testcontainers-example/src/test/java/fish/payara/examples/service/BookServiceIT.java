package fish.payara.examples.service;

import fish.payara.examples.domain.Book;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookServiceIT extends AbstractServiceIT {

    @Override
    protected String resourcePath() {
        return "resources/books";
    }

    @Test
    void testCreateAndRetrieveBook() {
        // Create a new book
        Book book = new Book();
        book.setTitle("Integration Test Book");
        book.setAuthor("Test Author");
        book.setPages(300);

        // POST the book (retry if deployment not ready)
        Response createResponse = null;
        String respBody = null;
        for (int i = 0; i < 20; i++) {
            createResponse = baseTarget
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(book, MediaType.APPLICATION_JSON));
            if (createResponse.getStatus() != 404) break;
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

                if (createResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
                        try { respBody = createResponse.readEntity(String.class); } catch (Exception e) { respBody = "<no body>"; }
                }

                if (createResponse.getStatus() != Response.Status.CREATED.getStatusCode()) {
                        String logs = containerLogs();
                        String msg = String.format("POST failed, status=%d, body=%s, container-logs-start:\n%s\n:container-logs-end", createResponse.getStatus(), respBody, logs);
                        assertEquals(Response.Status.CREATED.getStatusCode(), createResponse.getStatus(), msg);
                }

        // Get the created book's ISBN from the Location header
        String location = createResponse.getHeaderString("Location");
        assertNotNull(location, "Location header missing; POST response body=" + respBody);

        // GET the book and verify its contents
        Response getResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        Book retrievedBook = getResponse.readEntity(Book.class);

        assertNotNull(retrievedBook);
        assertEquals("Integration Test Book", retrievedBook.getTitle());
        assertEquals("Test Author", retrievedBook.getAuthor());
        assertEquals(300, retrievedBook.getPages());
    }

    @Test
    void testFindAllBooks() {
        // First create some books
        Book book1 = new Book();
        book1.setTitle("Book 1");
        book1.setAuthor("Author 1");
        book1.setPages(200);

        Book book2 = new Book();
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setPages(300);

                // POST the books (retry if necessary)
                for (int i = 0; i < 5; i++) {
                        Response r1 = baseTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(book1, MediaType.APPLICATION_JSON));
                        Response r2 = baseTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(book2, MediaType.APPLICATION_JSON));
                        if (r1.getStatus() == Response.Status.CREATED.getStatusCode() && r2.getStatus() == Response.Status.CREATED.getStatusCode()) break;
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                }

                // GET all books with retries
                Response response = null;
                String respBody = null;
                for (int i = 0; i < 20; i++) {
                        response = baseTarget.request(MediaType.APPLICATION_JSON).get();
                        if (response.getStatus() != 404) break;
                        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                }

                        if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                                try { respBody = response.readEntity(String.class); } catch (Exception e) { respBody = "<no body>"; }
                                String logs = containerLogs();
                                String msg = String.format("GET all failed, status=%d, body=%s, container-logs-start:\n%s\n:container-logs-end", response.getStatus(), respBody, logs);
                                assertEquals(Response.Status.OK.getStatusCode(), response.getStatus(), msg);
                        }

                List<Book> books = response.readEntity(new GenericType<List<Book>>() {});
                assertNotNull(books);
                assertTrue(books.size() >= 2);
    }

    @Test
    void testUpdateBook() {
        // Create a book
        Book book = new Book();
        book.setTitle("Original Title");
        book.setAuthor("Original Author");
        book.setPages(200);

        // POST the book
        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");
        assertNotNull(location, "Location header missing after create");
        Book createdBook = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(Book.class);

        // Update the book
        createdBook.setTitle("Updated Title");

        Response updateResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(createdBook, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), updateResponse.getStatus());

        // Verify the update
        Book updatedBook = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(Book.class);

        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals("Original Author", updatedBook.getAuthor());
    }

    @Test
    void testDeleteBook() {
        // Create a book
        Book book = new Book();
        book.setTitle("Book to Delete");
        book.setAuthor("Delete Author");
        book.setPages(100);

        // POST the book
        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(book, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");
        assertNotNull(location, "Location header missing after create");

        // DELETE the book
        Response deleteResponse = client.target(location)
                .request()
                .delete();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());

        // Verify the book is deleted
        Response getResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), getResponse.getStatus());
    }
}
