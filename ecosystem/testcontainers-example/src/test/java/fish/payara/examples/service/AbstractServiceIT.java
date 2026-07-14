package fish.payara.examples.service;

import fish.payara.examples.testcontainers.AbstractContainerIT;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for the REST integration tests running against the shared
 * Payara Micro Testcontainer (see {@link AbstractContainerIT}).
 *
 * <p>Subclasses only need to implement {@link #resourcePath()} to say which
 * REST resource they exercise; the JAX-RS {@link Client} and the
 * {@link #baseTarget} pointing at that resource are set up and torn down
 * automatically before/after each test.</p>
 */
abstract class AbstractServiceIT extends AbstractContainerIT {

    protected Client client;
    protected WebTarget baseTarget;

    @BeforeEach
    void setUpClient() {
        client = ClientBuilder.newClient();
        baseTarget = client.target(applicationContextUrl() + resourcePath());
    }

    @AfterEach
    void tearDownClient() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Resource path, relative to the application's "application" context
     * (see {@link fish.payara.examples.testcontainers.AbstractContainerIT#APPLICATION_CONTEXT}),
     * exercised by the subclass, e.g. {@code "resources/books"}.
     */
    protected abstract String resourcePath();
}
