package fish.payara.examples.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Exercises the "Librarian" JSF page (librarian.xhtml): create, edit and
 * delete a librarian through the browser, driven by Playwright against the
 * deployed application.
 */
class LibrarianUiIT extends AbstractUiIT {

    @Test
    void createsALibrarianAndShowsItInTheList() {
        String name = unique("Playwright Librarian");

        navigateTo("librarian.xhtml");
        // Leave the Librarian ID field blank: it's auto-generated on create,
        // same as the REST API path (see LibrarianBean#save / AbstractService#create).
        page.getByLabel("Name:").fill(name);
        page.getByLabel("Department:").fill("Circulation");
        clickSave();

        Locator row = rowContaining(name);
        assertThat(row).isVisible();
        assertThat(row).containsText("Circulation");
    }

    @Test
    void editsALibrarian() {
        String name = unique("Librarian To Edit");

        navigateTo("librarian.xhtml");
        page.getByLabel("Name:").fill(name);
        page.getByLabel("Department:").fill("Old Department");
        clickSave();

        rowContaining(name).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Edit")).click();
        page.getByLabel("Department:").fill("New Department");
        clickSave();

        assertThat(rowContaining(name)).containsText("New Department");
    }

    @Test
    void deletesALibrarian() {
        String name = unique("Librarian To Delete");

        navigateTo("librarian.xhtml");
        page.getByLabel("Name:").fill(name);
        page.getByLabel("Department:").fill("Doomed Department");
        clickSave();

        assertThat(rowContaining(name)).isVisible();

        rowContaining(name).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Delete")).click();

        assertThat(rowContaining(name)).hasCount(0);
    }
}
