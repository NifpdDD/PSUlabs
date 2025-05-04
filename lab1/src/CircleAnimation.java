import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class CircleAnimation extends JFrame {
    private final CirclePanel panel;
    private final WorkerThread[] threads;
    private final JComboBox<String>[] priorityBoxes;

    public CircleAnimation() {
        setTitle("Анимация кругов с приоритетами");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель для рисования кругов
        panel = new CirclePanel();
        add(panel, BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = new JPanel(new GridLayout(1, 3));
        priorityBoxes = new JComboBox[3];
        String[] priorities = {"Минимальный", "Обычный", "Максимальный"};

        for (int i = 0; i < 3; i++) {
            JPanel group = new JPanel(new BorderLayout());
            priorityBoxes[i] = new JComboBox<>(priorities);
            priorityBoxes[i].setSelectedIndex(1);
            group.add(new JLabel("Поток " + (i+1) + ": "), BorderLayout.WEST);
            group.add(priorityBoxes[i], BorderLayout.CENTER);
            controlPanel.add(group);
        }
        add(controlPanel, BorderLayout.SOUTH);

        // Создание потоков
        threads = new WorkerThread[3];
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
        for (int i = 0; i < 3; i++) {
            final int index = i;
            threads[i] = new WorkerThread(colors[i], panel);
            priorityBoxes[i].addActionListener(e -> updateThreadPriority(index));
        }

    }

    private void updateThreadPriority(int index) {
        int priority;
        switch ((String)priorityBoxes[index].getSelectedItem()) {
            case "Минимальный": priority = Thread.MIN_PRIORITY; break;
            case "Максимальный": priority = Thread.MAX_PRIORITY; break;
            default: priority = Thread.NORM_PRIORITY;
        }
        threads[index].updatePriority(priority); // Исправленный вызов
    }


    public void startAnimation() {
        for (WorkerThread thread : threads) {
            thread.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CircleAnimation frame = new CircleAnimation();
            frame.setVisible(true);
            frame.startAnimation();
        });
    }
}

class CirclePanel extends JPanel {
    private final AtomicInteger[] x = {new AtomicInteger(100), new AtomicInteger(100), new AtomicInteger(100)};
    private final AtomicInteger[] y = {new AtomicInteger(100), new AtomicInteger(300), new AtomicInteger(500)};

    public void updatePosition(int index, int newX, int newY) {
        x[index].set(newX);
        y[index].set(newY);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
        for (int i = 0; i < 3; i++) {
            g.setColor(colors[i]);
            g.fillOval(x[i].get(), y[i].get(), 50, 50);
        }
    }
}

class WorkerThread extends Thread {
    private static final Random random = new Random();
    private final CirclePanel panel;
    private final int index;
    private volatile boolean running = true;
    private volatile int currentPriority = Thread.NORM_PRIORITY;

    public WorkerThread(Color color, CirclePanel panel) {
        this.panel = panel;
        this.index = color == Color.RED ? 0 : color == Color.GREEN ? 1 : 2;
    }

    @Override
    public void run() {
        while (running) {
            int newX = random.nextInt(panel.getWidth() - 50);
            int newY = random.nextInt(panel.getHeight() - 50);

            SwingUtilities.invokeLater(() ->
                    panel.updatePosition(index, newX, newY)
            );

            try {
                Thread.sleep(calculateDelay());
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private int calculateDelay() {
        switch (currentPriority) {
            case Thread.MAX_PRIORITY:
                return 50 + random.nextInt(100);
            case Thread.MIN_PRIORITY:
                return 300 + random.nextInt(400);
            default:
                return 150 + random.nextInt(200);
        }
    }

    // Новый метод вместо переопределения setPriority
    public void updatePriority(int priority) {
        this.currentPriority = priority;
        super.setPriority(priority);
    }
}