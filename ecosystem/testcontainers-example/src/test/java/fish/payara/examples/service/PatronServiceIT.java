package fish.payara.examples.service;

import fish.payara.examples.domain.Patron;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PatronServiceIT extends AbstractServiceIT {

    @Override
    protected String resourcePath() {
        return "resources/patrons";
    }

    @Test
    @Order(1)
    void testCreateAndRetrievePatron() {
        Patron patron = new Patron();
        patron.setName("John Doe");
        patron.setEmail("john@example.com");

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(patron, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.CREATED.getStatusCode(), createResponse.getStatus());
        String location = createResponse.getHeaderString("Location");
        assertNotNull(location);

        Patron retrieved = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .get(Patron.class);

        assertNotNull(retrieved);
        assertEquals("John Doe", retrieved.getName());
    }

    @Test
    @Order(2)
    void testFindAllPatrons() {
        Response response = baseTarget.request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Patron> patrons = response.readEntity(new GenericType<>() {
        });
        assertNotNull(patrons);
    }

    @Test
    @Order(3)
    void testUpdatePatron() {
        Patron patron = new Patron();
        patron.setName("Jane Doe");
        patron.setEmail("jane@example.com");

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(patron, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");

        patron.setEmail("jane.updated@example.com");
        Response updateResponse = client.target(location)
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(patron, MediaType.APPLICATION_JSON));

        assertEquals(Response.Status.OK.getStatusCode(), updateResponse.getStatus());
    }

    @Test
    @Order(4)
    void testDeletePatron() {
        Patron patron = new Patron();
        patron.setName("Mark Twain");
        patron.setEmail("mark@example.com");

        Response createResponse = baseTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(patron, MediaType.APPLICATION_JSON));

        String location = createResponse.getHeaderString("Location");
        Response deleteResponse = client.target(location).request().delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
}
