import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

class Node {
    int value;
    Node left;
    Node right;

    public Node(int value) {
        this.value = value;
    }
}

class Result {
    long sum;
    long count;

    Result(long sum, long count) {
        this.sum = sum;
        this.count = count;
    }
}

class TreeTask extends RecursiveTask<Result> {
    private final Node node;

    public TreeTask(Node node) {
        this.node = node;
    }

    @Override
    protected Result compute() {
        if (node == null) {
            return new Result(0, 0);
        }

        // Создаем задачи для левого и правого поддеревьев
        TreeTask leftTask = new TreeTask(node.left);
        TreeTask rightTask = new TreeTask(node.right);

        // Запускаем задачи параллельно
        leftTask.fork();
        rightTask.fork();

        // Собираем результаты
        Result leftResult = leftTask.join();
        Result rightResult = rightTask.join();

        // Объединяем результаты
        long totalSum = node.value + leftResult.sum + rightResult.sum;
        long totalCount = 1 + leftResult.count + rightResult.count;

        return new Result(totalSum, totalCount);
    }
}

public class BinaryTreeProcessor {
    public static void main(String[] args) {
        // Создаем тестовое дерево
        Node root = new Node(10);
        root.left = new Node(5);
        root.right = new Node(15);
        root.left.left = new Node(2);
        root.left.right = new Node(7);
        root.right.right = new Node(20);

        ForkJoinPool pool = new ForkJoinPool();
        TreeTask task = new TreeTask(root);
        
        Result result = pool.invoke(task);
        
        long sum = result.sum;
        double average = (double) sum / result.count;

        System.out.println("Сумма элементов: " + sum);
        System.out.println("Среднее значение: " + average);
    }
}