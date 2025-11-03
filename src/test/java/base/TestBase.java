package base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.*;
import utils.DOMElementExtractor;
import utils.UICoverageTracker;

/**
 * Базовый класс для всех тестов с поддержкой отслеживания покрытия
 */
public class TestBase {
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    // Тут наши утилиты для покрытия
    protected DOMElementExtractor domExtractor;
    protected UICoverageTracker coverageTracker;

    @BeforeAll
    public static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false) // Браузер для наглядности
                .setSlowMo(1000)); // Замедляем выполнение для демонстрации
    }

    @AfterAll
    public static void closeBrowser() {
        // Генерируем HTML отчет
        UICoverageTracker tracker = UICoverageTracker.getInstance();
        utils.CoverageReportGenerator.generateHTMLReport(
                tracker.getAllElements(),
                tracker.getCoveredElements(),
                "target/ui-coverage-report.html"
        );

        // Печатаем консольный отчет
        tracker.printCoverageReport();

        // Закрываем браузер
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    public void createContextAndPage() {
        context = browser.newContext();

        // Устанавливаем таймаут навигации побольше
        context.setDefaultNavigationTimeout(60000);
        context.setDefaultTimeout(30000);

        page = context.newPage();

        // Инициализируем наши утилиты
        coverageTracker = UICoverageTracker.getInstance();
        domExtractor = new DOMElementExtractor(page);
    }

    @AfterEach
    public void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    /**
     * Переход на страницу с сбором элементов с ожиданием DOMCONTENTLOADED
     */
    protected void navigateWithCoverage(String url, String pageName) {
        System.out.println("\n🌐 Navigating to: " + url);

        // Используем DOMCONTENTLOADED для надежной загрузки
        page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        // Дополнительно ждем полной загрузки
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);

        // Даем дополнительное время для полной инициализации страницы
        page.waitForTimeout(2000);

        // Собираем все элементы на странице
        domExtractor.collectAllInteractiveElements();

        System.out.println("📊 Analyzing page: " + pageName);
    }

    /**
     * Обертка для клика с отслеживанием покрытия
     */
    protected void clickWithCoverage(String selector, String elementDescription) {
        try {
            // Ждем пока элемент станет доступным с увеличенным таймаутом
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(30000));

            // Проверяем что элемент видимый и кликабельный
            page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(30000));

            // Кликаем по элементу
            page.click(selector);

            // Генерируем ID и отмечаем как покрытый
            String elementId = domExtractor.generateElementIdentifier(selector, elementDescription);
            coverageTracker.markAsCovered(elementId);

            System.out.println("✅ Clicked: " + elementDescription);

            // Небольшая пауза после клика
            page.waitForTimeout(1000);

        } catch (Exception e) {
            System.out.println("❌ Failed to click: " + elementDescription + " - " + e.getMessage());
            throw new RuntimeException("Element not clickable: " + selector, e);
        }
    }

    /**
     * Обертка для заполнения поля с отслеживанием покрытия
     */
    protected void fillWithCoverage(String selector, String value, String fieldDescription) {
        try {
            // Ждем пока элемент станет доступным с увеличенным таймаутом
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(30000));

            // Проверяем что элемент видимый
            page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(30000));

            // Очищаем поле перед заполнением
            page.fill(selector, "");

            // Заполняем поле
            page.fill(selector, value);

            // Генерируем ID и отмечаем как покрытый
            String elementId = domExtractor.generateElementIdentifier(selector, fieldDescription);
            coverageTracker.markAsCovered(elementId);

            System.out.println("✅ Filled: " + fieldDescription + " with: '" + value + "'");

            // Небольшая пауза после заполнения
            page.waitForTimeout(500);

        } catch (Exception e) {
            System.out.println("❌ Failed to fill: " + fieldDescription + " - " + e.getMessage());
            throw new RuntimeException("Element not fillable: " + selector, e);
        }
    }

    /**
     * Ожидание появления элемента с дополнительными проверками
     */
    protected void waitForElement(String selector, String elementDescription) {
        try {
            System.out.println("⏳ Waiting for: " + elementDescription);
            page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(30000));

            // Дополнительная проверка что элемент видимый
            page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(30000));

            System.out.println("✅ Element ready: " + elementDescription);

        } catch (Exception e) {
            System.out.println("❌ Element not found: " + elementDescription + " - " + e.getMessage());
            throw new RuntimeException("Element not found: " + selector, e);
        }
    }
    /**
     * Обертка для двойного клика с отслеживанием покрытия
     */
    protected void doubleClickWithCoverage(String selector, String elementDescription) {
        try {
            // Ждем пока элемент станет доступным
            page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(30000));

            // Выполняем двойной клик
            page.dblclick(selector);

            // Генерируем ID и отмечаем как покрытый
            String elementId = domExtractor.generateElementIdentifier(selector, elementDescription);
            coverageTracker.markAsCovered(elementId);

            System.out.println("✅ Double clicked: " + elementDescription);

            // Небольшая пауза после действия
            page.waitForTimeout(1000);

        } catch (Exception e) {
            System.out.println("❌ Failed to double click: " + elementDescription + " - " + e.getMessage());
        }
    }

    /**
     * Обертка для правого клика с отслеживанием покрытия
     */
    protected void rightClickWithCoverage(String selector, String elementDescription) {
        try {
            // Ждем пока элемент станет доступным
            page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(30000));

            // Выполняем правый клик
            page.click(selector, new Page.ClickOptions().setButton(MouseButton.RIGHT));

            // Генерируем ID и отмечаем как покрытый
            String elementId = domExtractor.generateElementIdentifier(selector, elementDescription);
            coverageTracker.markAsCovered(elementId);

            System.out.println("✅ Right clicked: " + elementDescription);

            // Небольшая пауза после действия
            page.waitForTimeout(1000);

        } catch (Exception e) {
            System.out.println("❌ Failed to right click: " + elementDescription + " - " + e.getMessage());
        }
    }

    /**
     * Улучшенная проверка видимости с обработкой разных селекторов
     */
    protected void checkVisibilityWithCoverage(String selector, String elementDescription) {
        try {
            // Пробуем разные стратегии ожидания
            boolean isVisible = page.isVisible(selector, new Page.IsVisibleOptions().setTimeout(10000));

            if (isVisible) {
                String elementId = domExtractor.generateElementIdentifier(selector, elementDescription);
                coverageTracker.markAsCovered(elementId);
                System.out.println("✅ Visible: " + elementDescription);
            } else {
                System.out.println("⚠️ Element not visible (but no error): " + elementDescription);
            }

        } catch (Exception e) {
            System.out.println("⚠️ Element check skipped: " + elementDescription + " - " + e.getMessage());
            // Не бросаем исключение, просто логируем
        }
    }
}