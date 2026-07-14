package fish.payara.examples.ui;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Exercises the "Loan" JSF page (loan.xhtml). Unlike the other entities, a
 * loan is created by picking an existing librarian, patron and book from
 * dropdowns, so this test first creates one of each through their own pages
 * (reusing the same browser session) before creating, editing and deleting
 * the loan itself.
 */
class LoanUiIT extends AbstractUiIT {

    @Test
    void createsALoanFromAnExistingLibrarianPatronAndBook() {
        String librarianName = unique("Loan Librarian");
        String patronName = unique("Loan Patron");
        String bookTitle = unique("Loan Book");

        createLibrarian(librarianName);
        createPatron(patronName);
        createBook(bookTitle);
        fillAndSaveLoan(librarianName, patronName, bookTitle, "2026-07-14T09:00", "2026-07-28T09:00");

        Locator row = rowContaining(librarianName);
        assertThat(row).isVisible();
        assertThat(row).containsText(patronName);
        assertThat(row).containsText(bookTitle);
        assertThat(row).containsText("2026-07-14T09:00");
    }

    @Test
    void editsALoan() {
        String librarianName = unique("Loan Librarian Edit");
        String patronName = unique("Loan Patron Edit");
        String bookTitle = unique("Loan Book Edit");

        createLibrarian(librarianName);
        createPatron(patronName);
        createBook(bookTitle);
        fillAndSaveLoan(librarianName, patronName, bookTitle, "2026-07-14T09:00", "2026-07-28T09:00");

        rowContaining(librarianName).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Edit")).click();
        page.getByLabel("Return Date:").fill("2026-08-04T09:00");
        clickSave();

        assertThat(rowContaining(librarianName)).containsText("2026-08-04T09:00");
    }

    @Test
    void deletesALoan() {
        String librarianName = unique("Loan Librarian Delete");
        String patronName = unique("Loan Patron Delete");
        String bookTitle = unique("Loan Book Delete");

        createLibrarian(librarianName);
        createPatron(patronName);
        createBook(bookTitle);
        fillAndSaveLoan(librarianName, patronName, bookTitle, "2026-07-14T09:00", "2026-07-28T09:00");

        assertThat(rowContaining(librarianName)).isVisible();

        rowContaining(librarianName).getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("Delete")).click();

        assertThat(rowContaining(librarianName)).hasCount(0);
    }

    private void createLibrarian(String name) {
        navigateTo("librarian.xhtml");
        page.getByLabel("Name:").fill(name);
        clickSave();
    }

    private void createPatron(String name) {
        navigateTo("patron.xhtml");
        page.getByLabel("Name:").fill(name);
        clickSave();
    }

    private void createBook(String title) {
        navigateTo("book.xhtml");
        page.getByLabel("Title:").fill(title);
        page.getByLabel("Author:").fill("Loan Test Author");
        page.getByLabel("Pages:").fill("42");
        clickSave();
    }

    private void fillAndSaveLoan(String librarianName, String patronName, String bookTitle,
                                 String loanDate, String returnDate) {
        navigateTo("loan.xhtml");
        page.getByLabel("Loan Date:").fill(loanDate);
        page.getByLabel("Return Date:").fill(returnDate);
        page.getByLabel("Librarian:").selectOption(new SelectOption().setLabel(librarianName));
        page.getByLabel("Patron:").selectOption(new SelectOption().setLabel(patronName));
        page.getByLabel("Book:").selectOption(new SelectOption().setLabel(bookTitle));
        clickSave();
    }
}
