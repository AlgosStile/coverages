package utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Класс для отслеживания покрытия UI элементов тестами
 */
public class UICoverageTracker {
    private static UICoverageTracker instance;
    private Set<String> allElements = new HashSet<>();
    private Set<String> coveredElements = new HashSet<>();

    // Приватный конструктор для Singleton
    private UICoverageTracker() {}

    // Получаем экземпляр трекера
    public static UICoverageTracker getInstance() {
        if (instance == null) {
            instance = new UICoverageTracker();
        }
        return instance;
    }

    // Добавляем элемент в список всех элементов
    public void addElement(String elementId) {
        allElements.add(elementId);
    }

    // Отмечаем элемент как покрытый тестом
    public void markAsCovered(String elementId) {
        coveredElements.add(elementId);
    }

    // Получаем процент покрытия
    public double getCoveragePercentage() {
        if (allElements.isEmpty()) return 0.0;
        return (double) coveredElements.size() / allElements.size() * 100;
    }

    // Получаем статистику
    public void printCoverageReport() {
        System.out.println("=== UI COVERAGE REPORT ===");
        System.out.println("Total elements: " + allElements.size());
        System.out.println("Covered elements: " + coveredElements.size());
        System.out.println("Coverage: " + String.format("%.2f", getCoveragePercentage()) + "%");
        System.out.println("Uncovered elements: " + (allElements.size() - coveredElements.size()));
    }

    // Геттеры для отчетов
    public Set<String> getAllElements() {
        return new HashSet<>(allElements);
    }

    public Set<String> getCoveredElements() {
        return new HashSet<>(coveredElements);
    }

    // Очищаем данные (для нового запуска)
    public void reset() {
        allElements.clear();
        coveredElements.clear();
    }
}