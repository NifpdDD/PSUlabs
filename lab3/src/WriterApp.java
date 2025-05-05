import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class WriterApp {
    private static final String FILE_PATH = "shared_file.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n[Writer] Введите текст для записи (или 'exit' для выхода):");
            String input = scanner.nextLine();
            
            if ("exit".equalsIgnoreCase(input)) break;
            
            try (RandomAccessFile file = new RandomAccessFile(FILE_PATH, "rw");
                 FileChannel channel = file.getChannel()) {
                
                System.out.println("[Writer] Ожидание блокировки файла...");
                try (FileLock lock = channel.lock()) {
                    System.out.println("[Writer] Блокировка получена");
                    
                    file.seek(file.length());
                    file.writeBytes(input + "\n");
                    System.out.println("[Writer] Текст успешно записан");
                }
            } catch (IOException e) {
                System.err.println("[Writer] Ошибка: " + e.getMessage());
            }
        }
    }
}