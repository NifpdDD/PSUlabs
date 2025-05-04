import java.util.concurrent.*;

public class task1 {
    public static void main(String[] args) {
        int m = 5; // Количество строк
        int n = 3; // Количество столбцов
        double[][] matrix = new double[m][n];

        CountDownLatch latch = new CountDownLatch(m);
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Параллельное заполнение строк матрицы
        for (int i = 0; i < m; i++) {
            final int row = i;
            executor.submit(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int j = 0; j < n; j++) {
                    matrix[row][j] = random.nextDouble();
                }
                latch.countDown();
            });
        }

        try {
            latch.await(); // Ожидание завершения всех потоков
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        // Вывод матрицы
        System.out.println("\nСгенерированная матрица:");
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.printf("%8.2f", matrix[i][j]); // Форматирование до 2 знаков
            }
            System.out.println();
        }

        // Вычисление сумм столбцов
        double[] columnSums = new double[n];
        for (int j = 0; j < n; j++) {
            double sum = 0;
            for (int i = 0; i < m; i++) {
                sum += matrix[i][j];
            }
            columnSums[j] = sum;
        }

        // Поиск минимального столбца
        int minCol = 0;
        double minSum = columnSums[0];
        for (int j = 1; j < n; j++) {
            if (columnSums[j] < minSum) {
                minSum = columnSums[j];
                minCol = j;
            }
        }

        System.out.println("\nСтолбец с минимальной суммой: " + minCol);
    }
}