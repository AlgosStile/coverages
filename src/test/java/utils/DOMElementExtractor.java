package utils;

import com.microsoft.playwright.Page;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для извлечения информации о DOM-элементах
 */
public class DOMElementExtractor {

    private Page page;
    private UICoverageTracker coverageTracker;

    public DOMElementExtractor(Page page) {
        this.page = page;
        this.coverageTracker = UICoverageTracker.getInstance();
    }

    /**
     * Собирает все интерактивные элементы со страницы
     */
    public void collectAllInteractiveElements() {
        try {
            // JavaScript код, который выполнится в браузере
            Object result = page.evaluate("() => {\n" +
                    "const interactiveSelectors = [\n" +
                    "  'button', 'input', 'select', 'textarea', 'a',\n" +
                    "  '[role=button]', '[onclick]',\n" +
                    "  '[data-testid]', '[id]'\n" +
                    "];\n" +
                    "\n" +
                    "let allElements = [];\n" +
                    "\n" +
                    "interactiveSelectors.forEach(selector => {\n" +
                    "  try {\n" +
                    "    const found = document.querySelectorAll(selector);\n" +
                    "    found.forEach(element => {\n" +
                    "      if (element.offsetParent !== null) { // Только видимые элементы\n" +
                    "        let identifier = '';\n" +
                    "        \n" +
                    "        // Сначала пытаемся получить data-testid\n" +
                    "        if (element.getAttribute('data-testid')) {\n" +
                    "          identifier = 'data-testid:' + element.getAttribute('data-testid');\n" +
                    "        } \n" +
                    "        // Затем ID\n" +
                    "        else if (element.id && element.id.trim() !== '') {\n" +
                    "          identifier = 'id:' + element.id;\n" +
                    "        }\n" +
                    "        // Затем текст кнопки/ссылки\n" +
                    "        else if (element.textContent && element.textContent.trim() !== '') {\n" +
                    "          identifier = element.tagName + ':text=' + element.textContent.trim().substring(0, 30);\n" +
                    "        }\n" +
                    "        // Иначе используем селектор\n" +
                    "        else {\n" +
                    "          identifier = selector + ':' + Math.random().toString(36).substring(7);\n" +
                    "        }\n" +
                    "        \n" +
                    "        if (identifier && !allElements.includes(identifier)) {\n" +
                    "          allElements.push(identifier);\n" +
                    "        }\n" +
                    "      }\n" +
                    "    });\n" +
                    "  } catch (e) {\n" +
                    "    // Игнорируем ошибки для отдельных селекторов\n" +
                    "  }\n" +
                    "});\n" +
                    "\n" +
                    "return allElements;\n" +
                    "}");

            // Преобразуем результат в List<String>
            if (result instanceof List) {
                List<?> rawList = (List<?>) result;
                List<String> elements = new ArrayList<>();

                for (Object item : rawList) {
                    if (item instanceof String) {
                        elements.add((String) item);
                    }
                }

                // Добавляем элементы в трекер
                for (String element : elements) {
                    coverageTracker.addElement(element);
                }

                System.out.println("✓ Collected " + elements.size() + " interactive elements");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Could not collect elements: " + e.getMessage());
        }
    }

    /**
     * Генерирует уникальный идентификатор для элемента
     */
    public String generateElementIdentifier(String selector, String description) {
        return selector + "::" + (description != null ? description : "element");
    }
}