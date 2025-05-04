import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class task3 {
    // Функция, для которой вычисляется интеграл (пример: y = x^2)
    public static double f(double x) {
        return x * x;
    }

    public static void main(String[] args) throws Exception {
        double a = 0.0; // Начало отрезка
        double b = 2.0; // Конец отрезка
        int n = 1000;   // Количество элементов разбиения
        int m = 1000;   // Количество участков внутри элемента

        ExecutorService executor = Executors.newFixedThreadPool(n);
        List<Future<Double>> futures = new ArrayList<>();

        // Разбиваем отрезок [a, b] на n элементов и создаем задачи
        for (int i = 0; i < n; i++) {
            final int elementIndex = i;
            futures.add(executor.submit(() -> {
                double elementA = a + elementIndex * (b - a) / n;
                double elementB = a + (elementIndex + 1) * (b - a) / n;
                double dx = (elementB - elementA) / m;
                double sum = 0.0;

                for (int j = 0; j < m; j++) {
                    double x = elementA + j * dx;
                    sum += f(x) * dx; // Метод левых прямоугольников
                }

                return sum;
            }));
        }

        // Собираем результаты
        double totalArea = 0.0;
        for (Future<Double> future : futures) {
            totalArea += future.get();
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.printf("Приближенная площадь: %.6f%n", totalArea);
    }
}