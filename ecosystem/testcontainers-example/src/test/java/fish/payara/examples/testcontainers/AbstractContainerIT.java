/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
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
