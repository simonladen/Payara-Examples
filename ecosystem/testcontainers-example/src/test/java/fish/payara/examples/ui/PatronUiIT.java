package fish.payara.examples.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Exercises the "Patron" JSF page (patron.xhtml): create, edit and delete a
 * patron through the browser, driven by Playwright against the deployed
 * application.
 */
class PatronUiIT extends AbstractUiIT {

    @Test
    void createsAPatronAndShowsItInTheList() {
        String name = unique("Playwright Patron");

        navigateTo("patron.xhtml");
        // Leave the Patron ID field blank: it's auto-generated on create, same
        // as the REST API path (see PatronBean#save / AbstractService#create).
        page.getByLabel("Name:").fill(name);
        page.getByLabel("Address:").fill("1 Library Way");
        page.getByLabel("Email:").fill("patron@example.com");
        clickSave();

        Locator row = rowContaining(name);
        assertThat(row).isVisible();
        assertThat(row).containsText("1 Library Way");
        assertThat(row).containsText("patron@example.com");
    }

    @Test
    void editsAPatron() {
        String name = unique("Patron To Edit");

        navigateTo("patron.xhtml");
        page.getByLabel("Name:").fill(name);
        page.getByLabel("Address:").fill("Old Address");
        page.getByLabel("Email:").fill("old@example.com");
        clickSave();

        rowContaining(name).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Edit")).click();
        page.getByLabel("Email:").fill("new@example.com");
        clickSave();

        assertThat(rowContaining(name)).containsText("new@example.com");
    }

    @Test
    void deletesAPatron() {
        String name = unique("Patron To Delete");

        navigateTo("patron.xhtml");
        page.getByLabel("Name:").fill(name);
        page.getByLabel("Address:").fill("Somewhere");
        page.getByLabel("Email:").fill("delete@example.com");
        clickSave();

        assertThat(rowContaining(name)).isVisible();

        rowContaining(name).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Delete")).click();

        assertThat(rowContaining(name)).hasCount(0);
    }
}
