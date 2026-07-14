package fish.payara.examples.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Exercises the "Book" JSF page (book.xhtml): create, edit and delete a book
 * through the browser, driven by Playwright against the deployed application.
 */
class BookUiIT extends AbstractUiIT {

    @Test
    void createsABookAndShowsItInTheList() {
        String title = unique("Playwright Book");

        navigateTo("book.xhtml");
        // Leave the Isbn field blank: the id is auto-generated on create, same
        // as the REST API path (see BookBean#save / AbstractService#create).
        page.getByLabel("Title:").fill(title);
        page.getByLabel("Author:").fill("Jane Author");
        page.getByLabel("Pages:").fill("123");
        clickSave();

        Locator row = rowContaining(title);
        assertThat(row).isVisible();
        assertThat(row).containsText("Jane Author");
        assertThat(row).containsText("123");
    }

    @Test
    void editsABook() {
        String title = unique("Book To Edit");
        String updatedTitle = unique("Updated Book");

        navigateTo("book.xhtml");
        page.getByLabel("Title:").fill(title);
        page.getByLabel("Author:").fill("Original Author");
        page.getByLabel("Pages:").fill("50");
        clickSave();

        rowContaining(title).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Edit")).click();
        page.getByLabel("Title:").fill(updatedTitle);
        clickSave();

        assertThat(rowContaining(updatedTitle)).containsText("Original Author");
        assertThat(rowContaining(title)).hasCount(0);
    }

    @Test
    void deletesABook() {
        String title = unique("Book To Delete");

        navigateTo("book.xhtml");
        page.getByLabel("Title:").fill(title);
        page.getByLabel("Author:").fill("Delete Author");
        page.getByLabel("Pages:").fill("10");
        clickSave();

        assertThat(rowContaining(title)).isVisible();

        rowContaining(title).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Delete")).click();

        assertThat(rowContaining(title)).hasCount(0);
    }
}
