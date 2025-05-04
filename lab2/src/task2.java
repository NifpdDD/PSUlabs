import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class task2 {
    public static void main(String[] args) throws Exception {
        String text = "Hello, World!"; // Пример текста
        int k = 4; // Количество потоков (должно быть меньше длины текста)
        
        int checksum = calculateChecksum(text, k);
        System.out.println("Контрольная сумма: " + checksum);
    }

    public static int calculateChecksum(String text, int k) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(k);
        List<Future<Integer>> futures = new ArrayList<>();

        // Создаем задачи для каждого потока
        for (int i = 0; i < k; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                int partialSum = 0;
                for (int s = 0; threadId + k * s < text.length(); s++) {
                    int index = threadId + k * s;
                    partialSum += text.charAt(index);
                }
                return partialSum;
            }));
        }

        // Собираем результаты
        int totalSum = 0;
        for (Future<Integer> future : futures) {
            totalSum += future.get();
        }

        executor.shutdown();
        return totalSum % 256;
    }
}