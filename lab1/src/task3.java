import java.util.Scanner;
import java.util.concurrent.*;

public class task3 {
    public static void main(String[] args) throws InterruptedException {
        int m = 5; // Строки
        int n = 3; // Столбцы
        
        // 1. Параллельное создание матрицы с натуральными числами
        int[][] matrix = createNaturalMatrix(m, n);
        System.out.println("Исходная матрица:");
        printMatrix(matrix);

        // 2. Ввод множителя
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите множитель: ");
        int multiplier = scanner.nextInt();
        scanner.close();

        // 3. Параллельное умножение
        parallelMultiply(matrix, multiplier);
        
        // 4. Вывод результата
        System.out.println("\nРезультат умножения:");
        printMatrix(matrix);
    }

    private static int[][] createNaturalMatrix(int m, int n) throws InterruptedException {
        int[][] matrix = new int[m][n];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch latch = new CountDownLatch(m);

        for (int i = 0; i < m; i++) {
            final int row = i;
            executor.submit(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int j = 0; j < n; j++) {
                    matrix[row][j] = random.nextInt(1, 100); // Генерация чисел от 1 до 99
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
        return matrix;
    }

    private static void parallelMultiply(int[][] matrix, int multiplier) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        CountDownLatch latch = new CountDownLatch(matrix.length);

        for (int i = 0; i < matrix.length; i++) {
            final int row = i;
            executor.submit(() -> {
                for (int j = 0; j < matrix[row].length; j++) {
                    matrix[row][j] *= multiplier;
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int num : row) {
                System.out.printf("%8d", num);
            }
            System.out.println();
        }
    }
}