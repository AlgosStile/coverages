package tests;

import base.TestBase;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Рабочий тест для демонстрации системы покрытия UI
 * Используем стабильный сайт: https://demoqa.com/text-box
 */
public class WorkingTextBoxTest extends TestBase {

    private final String BASE_URL = "https://demoqa.com/text-box";

    @Test
    void testTextBoxFormWithCoverage() {
        // Шаг 1: Переходим на страницу и собираем элементы с DOMCONTENTLOADED
        navigateWithCoverage(BASE_URL, "DemoQA Text Box Page");

        // Шаг 1.5: Ждем ключевые элементы формы
        waitForElement("#userName", "Full Name input field");
        waitForElement("#userEmail", "Email input field");
        waitForElement("#submit", "Submit button");

        // Шаг 2: Заполняем поле Full Name
        fillWithCoverage("#userName", "John Doe", "Full Name field");

        // Шаг 3: Заполняем поле Email
        fillWithCoverage("#userEmail", "john.doe@example.com", "Email field");

        // Шаг 4: Заполняем поле Current Address
        fillWithCoverage("#currentAddress", "123 Main Street, City, Country", "Current Address field");

        // Шаг 5: Заполняем поле Permanent Address
        fillWithCoverage("#permanentAddress", "456 Second Street, Another City", "Permanent Address field");

        // Шаг 6: Прокручиваем к кнопке Submit для уверенности
        page.evaluate("document.getElementById('submit').scrollIntoView()");

        // Шаг 7: Кликаем по кнопке Submit
        clickWithCoverage("#submit", "Submit button");

        // Шаг 8: Ждем появления результатов
        waitForElement("#output", "Results output section");

        // Шаг 9: Проверяем что форма отправилась (появляется результат)
        checkVisibilityWithCoverage("#output", "Results output section");

        // Шаг 10: Проверяем конкретные результаты
        boolean isNameDisplayed = page.isVisible("#name");
        boolean isEmailDisplayed = page.isVisible("#email");
        boolean isCurrentAddressDisplayed = page.isVisible("#currentAddress");
        boolean isPermanentAddressDisplayed = page.isVisible("#permanentAddress");

        assertTrue(isNameDisplayed, "Name should be displayed in results");
        assertTrue(isEmailDisplayed, "Email should be displayed in results");
        assertTrue(isCurrentAddressDisplayed, "Current Address should be displayed in results");
        assertTrue(isPermanentAddressDisplayed, "Permanent Address should be displayed in results");

        // Шаг 11: Проверяем содержимое результатов
        String nameText = page.textContent("#name");
        String emailText = page.textContent("#email");

        assertNotNull(nameText, "Name result should not be null");
        assertNotNull(emailText, "Email result should not be null");
        assertTrue(nameText.contains("John Doe"), "Name should contain 'John Doe'");
        assertTrue(emailText.contains("john.doe@example.com"), "Email should contain 'john.doe@example.com'");

        System.out.println("🎉 Тест успешно завершен! Форма отправлена и проверена.");
        System.out.println("📝 Результаты: " + nameText + " | " + emailText);
    }

    @Test
    void testAdditionalElementsCoverage() {
        // Дополнительный тест для увеличения покрытия
        navigateWithCoverage("https://demoqa.com/buttons", "DemoQA Buttons Page");

        // Ждем загрузки ключевых элементов
        waitForElement("#doubleClickBtn", "Double Click Button");
        waitForElement("#rightClickBtn", "Right Click Button");
        waitForElement("button:has-text('Click Me')", "Dynamic Click Button");

        // Используем специальные действия для кнопок:

        // 1. Двойной клик по первой кнопке
        doubleClickWithCoverage("#doubleClickBtn", "Double Click Button");

        // 2. Правый клик по второй кнопке
        rightClickWithCoverage("#rightClickBtn", "Right Click Button");

        // 3. Обычный клик по третьей кнопке
        clickWithCoverage("button:has-text('Click Me')", "Dynamic Click Button");

        // Проверяем сообщения (используем улучшенную версию без ошибок)
        checkVisibilityWithCoverage("#doubleClickMessage", "Double Click Message");
        checkVisibilityWithCoverage("#rightClickMessage", "Right Click Message");
        checkVisibilityWithCoverage("#dynamicClickMessage", "Dynamic Click Message");

        // Дополнительно: проверяем текст сообщений если они есть
        try {
            String doubleClickText = page.textContent("#doubleClickMessage");
            if (doubleClickText != null) {
                System.out.println("📝 Double click message: " + doubleClickText);
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Double click message not available");
        }

        System.out.println("🎉 Дополнительный тест завершен! Больше элементов покрыто.");
    }

    @Test
    void testCheckboxPageCoverage() {
        // Тест для страницы с чекбоксами
        navigateWithCoverage("https://demoqa.com/checkbox", "DemoQA Checkbox Page");

        // Ждем загрузки элементов
        waitForElement(".rct-checkbox", "Checkbox element");

        // Раскрываем дерево
        clickWithCoverage(".rct-collapse-btn", "Expand tree button");

        // Ждем появления дочерних элементов
        page.waitForTimeout(1000);

        // Кликаем по чекбоксу Desktop
        clickWithCoverage("label[for='tree-node-desktop']", "Desktop checkbox");

        // Проверяем результат
        checkVisibilityWithCoverage("#result", "Checkbox result");

        System.out.println("🎉 Тест чекбоксов завершен!");
    }

    @Test
    void testSimpleElementsCoverage() {
        // Простой тест для демонстрации работы без сложных взаимодействий
        navigateWithCoverage("https://demoqa.com/elements", "DemoQA Elements Main Page");

        // Просто собираем элементы и проверяем видимость основных компонентов
        checkVisibilityWithCoverage(".main-header", "Main Header");
        checkVisibilityWithCoverage(".left-pannel", "Left Panel");
        checkVisibilityWithCoverage(".playgound-body", "Playground Body");

        // Кликаем по нескольким ссылкам в левой панели
        clickWithCoverage("li:has-text('Text Box')", "Text Box menu item");
        page.waitForTimeout(1000);

        clickWithCoverage("li:has-text('Check Box')", "Check Box menu item");
        page.waitForTimeout(1000);

        clickWithCoverage("li:has-text('Radio Button')", "Radio Button menu item");

        System.out.println("🎉 Простой тест навигации завершен!");
    }
}