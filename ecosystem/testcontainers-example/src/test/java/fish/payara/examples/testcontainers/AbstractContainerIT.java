package fish.payara.examples.testcontainers;

/**
 * Base class for every integration test that needs a running Payara Micro
 * instance with the application deployed, whether it is exercised over REST
 * (see the {@code *ServiceIT} classes) or through a browser with Playwright
 * (see the {@code *UiIT} classes).
 *
 * <p>This deliberately does <b>not</b> use {@code @Testcontainers}/{@code @Container}.
 * Those JUnit 5 annotations manage the container's lifecycle per test class:
 * a static container gets started in that class's {@code beforeAll} and, in
 * some circumstances, stopped again in its {@code afterAll} once that class's
 * tests are done - which is exactly wrong for a container meant to be shared
 * by several test classes, since whichever class happens to run first ends up
 * tearing the container down again before the next class's tests get a
 * chance to run against it.</p>
 *
 * <p>Instead this follows Testcontainers' documented
 * <a href="https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/">
 * "singleton container"</a> pattern: the container is started eagerly, exactly
 * once, in a static initializer (guaranteed by the JVM's class-initialization
 * semantics to run at most once no matter how many subclasses trigger it), and
 * is never explicitly stopped - Testcontainers' own Ryuk reaper cleans it up
 * when the JVM (the forked failsafe JVM running the whole IT suite) exits.</p>
 */
public abstract class AbstractContainerIT {

    /**
     * Context path the WAR is deployed under inside the container. Payara
     * Micro's autodeploy derives the context root from the deployed file's
     * name, and {@link PayaraMicroContainer#withDeploymentPath} always copies
     * the WAR in as {@code application.war}, so this is always "application" -
     * it's not the project's artifact name.
     */
    protected static final String APPLICATION_CONTEXT = "application/";

    protected static final PayaraMicroContainer payara;

    static {
        payara = new PayaraMicroContainer();
        payara.start();
    }

    /** Base URL of the deployed application, always ending with a trailing slash. */
    protected static String applicationUrl() {
        String appUrl = payara.getApplicationUrl();
        return appUrl.endsWith("/") ? appUrl : appUrl + "/";
    }

    /** Base URL of the deployed application's "application" context, ending with a trailing slash. */
    protected static String applicationContextUrl() {
        return applicationUrl() + APPLICATION_CONTEXT;
    }

    /** Container logs, safe to call even if the container failed to start. */
    protected static String containerLogs() {
        try {
            return payara.getLogs();
        } catch (Exception e) {
            return "<no logs>";
        }
    }
}
