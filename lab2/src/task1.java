import java.util.concurrent.*;

public class task1 {
    private static final int N = 5; // Количество строк
    private static final int M = 3; // Количество столбцов

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        double[][] matrix = new double[N][M];
        CountDownLatch[] rowReadyLatches = new CountDownLatch[N];
        CountDownLatch allRowsLatch = new CountDownLatch(N);
        ExecutorService executor = Executors.newFixedThreadPool(N * 2);

        // Инициализация защелок
        for (int i = 0; i < N; i++) {
            rowReadyLatches[i] = new CountDownLatch(1);
        }

        // Запуск потоков для заполнения строк
        for (int i = 0; i < N; i++) {
            final int row = i;
            executor.submit(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int j = 0; j < M; j++) {
                    matrix[row][j] = random.nextDouble();
                }
                rowReadyLatches[row].countDown(); // Сигнал о готовности строки
                allRowsLatch.countDown(); // Уведомление о завершении заполнения строки
            });
        }

        // Ожидание завершения заполнения всех строк
        allRowsLatch.await();

        // Вывод матрицы
        System.out.println("Сгенерированная матрица:");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                System.out.printf("%8.2f", matrix[i][j]);
            }
            System.out.println();
        }

        // Запуск потоков для подсчета сумм
        Future<Double>[] sumFutures = new Future[N];
        for (int i = 0; i < N; i++) {
            final int row = i;
            sumFutures[i] = executor.submit(() -> {
                rowReadyLatches[row].await(); // Ожидание готовности строки
                double sum = 0;
                for (double num : matrix[row]) {
                    sum += num;
                }
                return sum;
            });
        }

        // Сбор результатов
        double totalSum = 0;
        for (Future<Double> future : sumFutures) {
            totalSum += future.get();
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.printf("\nСумма матрицы: %.2f%n", totalSum);
    }
}