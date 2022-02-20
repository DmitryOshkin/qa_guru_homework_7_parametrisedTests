package yandex.oshkin;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static org.openqa.selenium.By.linkText;

public class ParametrizedWebTests extends TestBase {

    void fillSearchField(String key) {
        $(".ant-input").click();
        $(".ant-input").sendKeys(key);
        $(".ant-input").pressEnter();
    }

    @ParameterizedTest(name = "Тестирование поиска всех книг по имени автора: ({0})")
    @ValueSource(strings = {"Максим Дорофеев", "Максим Батырев", "Уильям Шекспир"})
    void searchAllBooksForAuthorsValueTests(String author) {

        fillSearchField(author);

        $(".sc-11o5u18-0").$(linkText("книги")).click();

        $("h1").shouldHave(text("Все книги по запросу «" + author + "»"));
    }

    public enum Author {
        MD("Максим Дорофеев"),
        MB("Максим Батырев"),
        USH("Уильям Шекспир");

        private final String authorName;

        Author(String author) {
            this.authorName = author;
        }

        public String getName() {
            return authorName;
        }
    }

    @ParameterizedTest(name = "Тестирование поиска всех книг по имени автора: ({0})")
    //Уважаемый проверяющий, есть ли способ при прогоне теста в имени получать не MD MB USH вместо "{0}", а значение имени?
    @EnumSource(Author.class)
    void searchAllBooksForAuthorsEnumTests(Author authorName) {

        fillSearchField(String.valueOf(authorName.getName()));

        $(".sc-11o5u18-0").$(linkText("книги")).click();

        $("h1").shouldHave(text("Все книги по запросу «" + authorName.getName() + "»"));
    }

    @ParameterizedTest(name = "Тестирование поиска конкретной книги: ({1}); по имени автора: ({0})")
    @CsvSource(delimiter = ';', value = {
            "Максим Дорофеев; Путь джедая. Поиск собственной методики продуктивности",
            "Максим Батырев; 45 татуировок личности. Правила моей жизни",
            "Уильям Шекспир; Гамлет, принц датский"
    })
    void searchBookForAuthorsCsvTests(String author, String bookName) {

        fillSearchField(author);

        $(".sc-11o5u18-0").$(linkText("книги")).click();

        $(withText(bookName)).should(Condition.visible);
    }

    @ParameterizedTest(name = "Тестирование поиска конкретной книги: ({1}); по имени автора: ({0})")
    @CsvFileSource(resources = "/dataCsv.csv")
    void searchBookForAuthorsCsvFileTests(String author, String bookName) {

        fillSearchField(author);

        $(".sc-11o5u18-0").$(linkText("книги")).click();

        $(withText(bookName)).should(Condition.visible);
    }

    static Stream<Arguments> commonSearchTestCsvSource() {
        return Stream.of(
                Arguments.of("Максим Дорофеев", "Путь джедая. Поиск собственной методики продуктивности", 273),
                Arguments.of("Максим Батырев", "45 татуировок личности. Правила моей жизни", 227),
                Arguments.of("Уильям Шекспир", "Гамлет, принц датский", 83)
        );
    }

    @ParameterizedTest(name = "Тестирование поиска конкретной книги: ({1}); по имени автора: ({0})")
    @MethodSource("commonSearchTestCsvSource")
    void searchBookForAuthorsUseMethodTests(String author, String bookName, int pages) {

        fillSearchField(author);

        $(".sc-11o5u18-0").$(linkText("книги")).click();

        $(".sc-1bmhumr-6").$(linkText(bookName)).click();

        $(".sc-1c0xbiw-0 .sc-1c0xbiw-9").shouldHave(text(String.valueOf(pages)));
    }


}
