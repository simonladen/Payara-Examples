package fish.payara.examples.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * A Testcontainers {@link GenericContainer} pre-configured to run Payara Micro
 * with the application WAR deployed.
 *
 * <p>The no-arg constructor is fully self-configuring: it reads the Payara
 * Micro version and the path to the WAR file from the {@code payara.version}
 * and {@code war.path} system properties. Those properties are populated from
 * the Maven {@code payara.version} property and the build output directory by
 * the failsafe plugin (see pom.xml), so the version only needs to be defined
 * once for the whole build instead of being repeated in every IT test.</p>
 */
public class PayaraMicroContainer extends GenericContainer<PayaraMicroContainer> {

    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_CONTEXT_PATH = "/";
    protected static final String CONTEXT = "ObservabilityTool";

    /**
     * Fully self-configured container: version and WAR path are resolved from
     * the {@code payara.version} / {@code war.path} system properties set by Maven.
     */
    public PayaraMicroContainer() {
        this(DockerImageName.parse("payara/micro:" + requiredProperty("payara.version")));
        withDeploymentPath(requiredProperty("war.path"));
    }

    public PayaraMicroContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        withExposedPorts(DEFAULT_PORT);
        waitingFor(Wait.forLogMessage(".*Payara Micro .* ready.*\\n", 1));
    }

    public PayaraMicroContainer withDeploymentPath(String warPath) {
        withCopyFileToContainer(
            MountableFile.forHostPath(warPath),
            "/opt/payara/deployments/application.war"
        );
        return this;
    }

    public String getApplicationUrl() {
        return String.format(
            "http://%s:%d%s",
            getHost(),
            getMappedPort(DEFAULT_PORT),
            DEFAULT_CONTEXT_PATH
        );
    }

    private static String requiredProperty(String name) {
        String value = System.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "System property '" + name + "' is not set. It must be supplied by the Maven build "
                    + "(see the failsafe plugin's systemPropertyVariables in pom.xml).");
        }
        return value;
    }
}