import java.util.concurrent.*;

public class task2 {
    private static final int PRECISION = 2; // Точность округления

    public static void main(String[] args) throws InterruptedException {
        int m = 5; // Строки
        int n = 3; // Столбцы

        double[][] matrix = createMatrixParallel(m, n);
        System.out.println("Исходная матрица:");
        printMatrix(matrix);

        parallelSortRows(matrix);

        System.out.println("\nМатрица после сортировки:");
        printMatrix(matrix);
    }

    // Параллельная сортировка строк
    private static void parallelSortRows(double[][] matrix) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch latch = new CountDownLatch(matrix.length);

        for (int i = 0; i < matrix.length; i++) {
            final int rowIdx = i;
            executor.submit(() -> {
                sortRowByCounting(matrix[rowIdx], PRECISION);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
    }

    // Сортировка строки методом подсчёта
    private static void sortRowByCounting(double[] row, int precision) {
        long[] longRow = new long[row.length];
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        final double scale = Math.pow(10, precision);

        // Преобразование в long с учётом точности
        for (int i = 0; i < row.length; i++) {
            longRow[i] = Math.round(row[i] * scale);
            min = Math.min(min, longRow[i]);
            max = Math.max(max, longRow[i]);
        }

        // Массив подсчёта
        int[] count = new int[(int)(max - min + 1)];
        for (long num : longRow) {
            count[(int)(num - min)]++;
        }

        // Сборка в обратном порядке (по убыванию)
        int index = 0;
        for (int i = count.length-1; i >= 0; i--) {
            while (count[i] > 0) {
                longRow[index++] = i + min;
                count[i]--;
            }
        }

        // Обратное преобразование
        for (int i = 0; i < row.length; i++) {
            row[i] = longRow[i] / scale;
        }
    }

    // Параллельное создание матрицы (из предыдущего решения)
    private static double[][] createMatrixParallel(int m, int n) {
        double[][] matrix = new double[m][n];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch latch = new CountDownLatch(m);

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
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        return matrix;
    }

    // Вывод матрицы
    private static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double num : row) {
                System.out.printf("%8.2f", num);
            }
            System.out.println();
        }
    }
}