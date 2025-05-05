import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentHashTable {

    static class HashTableMonitor {
        private final List<LinkedList<Integer>> table;
        private final ReentrantLock[] locks;
        private final int k;

        public HashTableMonitor(int k) {
            this.k = k;
            table = new ArrayList<>(k);
            locks = new ReentrantLock[k];
            
            for (int i = 0; i < k; i++) {
                table.add(new LinkedList<>());
                locks[i] = new ReentrantLock();
            }
        }

        public void addNumber(int x) {
            int hash = Math.abs(x % k);
            locks[hash].lock();
            try {
                table.get(hash).add(x);
                System.out.println(Thread.currentThread().getName() + 
                                  " добавил " + x + " в строку " + hash);
            } finally {
                locks[hash].unlock();
            }
        }

        public void printTable() {
            for (int i = 0; i < k; i++) {
                locks[i].lock();
                try {
                    System.out.println("Строка " + i + ": " + table.get(i));
                } finally {
                    locks[i].unlock();
                }
            }
        }
    }

    static class NumberGenerator implements Runnable {
        private final HashTableMonitor monitor;
        private final int numbersCount;

        public NumberGenerator(HashTableMonitor monitor, int numbersCount) {
            this.monitor = monitor;
            this.numbersCount = numbersCount;
        }

        @Override
        public void run() {
            Random rand = new Random();
            for (int i = 0; i < numbersCount; i++) {
                int num = rand.nextInt();
                monitor.addNumber(num);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Введите количество строк таблицы (k): ");
        int k = scanner.nextInt();
        
        System.out.print("Введите количество потоков (n): ");
        int n = scanner.nextInt();
        
        System.out.print("Введите количество чисел на поток: ");
        int count = scanner.nextInt();

        HashTableMonitor monitor = new HashTableMonitor(k);

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            threads.add(new Thread(new NumberGenerator(monitor, count), "Поток-" + (i+1)));
        }

        threads.forEach(Thread::start);
        
        // Ожидание завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nИтоговое состояние хеш-таблицы:");
        monitor.printTable();
    }
}