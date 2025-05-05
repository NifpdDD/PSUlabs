import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ReaderApp {
    private static final String FILE_PATH = "shared_file.txt";

    public static void main(String[] args) {
        while (true) {
            try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "r");
                 FileChannel channel = file.getChannel()) {
                
                System.out.println("\n[Reader] Ожидание блокировки файла...");
                try (FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) {
                    System.out.println("[Reader] Блокировка получена");
                    
                    StringBuilder content = new StringBuilder();
                    String line;
                    file.seek(0);
                    while ((line = file.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    
                    System.out.println("[Reader] Содержимое файла:\n" + content);
                    Thread.sleep(3000); // Имитация долгого чтения
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (IOException e) {
                System.err.println("[Reader] Файл не найден или ошибка доступа");
            }
            
            try {
                Thread.sleep(1000); // Пауза между попытками чтения
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}