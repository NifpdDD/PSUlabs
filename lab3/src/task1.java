import java.util.Scanner;

public class task1 {

    private static double dotProduct = 0;
    private static final Object lock = new Object(); // Мьютекс для синхронизации

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите элементы первого вектора через пробел:");
        double[] vectorA = parseVector(scanner.nextLine());

        System.out.println("Введите элементы второго вектора через пробел:");
        double[] vectorB = parseVector(scanner.nextLine());

        if (vectorA.length != vectorB.length) {
            System.out.println("Ошибка: векторы должны быть одинаковой длины!");
            return;
        }

        Thread[] threads = new Thread[vectorA.length];

        // Создание и запуск потоков
        for (int i = 0; i < vectorA.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                double product = vectorA[index] * vectorB[index];
                
                // Синхронизированный блок для обновления общей суммы
                synchronized (lock) {
                    dotProduct += product;
                }
            });
            threads[i].start();
        }

        // Ожидание завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Скалярное произведение: " + dotProduct);
    }

    private static double[] parseVector(String input) {
        String[] tokens = input.trim().split("\\s+");
        double[] vector = new double[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            try {
                vector[i] = Double.parseDouble(tokens[i]);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода! Используйте числа.");
                System.exit(1);
            }
        }
        return vector;
    }
}