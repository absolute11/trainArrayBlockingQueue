package org.example;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main{
    private static final int TEXT_LENGTH = 100000;
    private static final int QUEUE_CAPACITY = 100;
    private static final int NUM_TEXTS = 10000;

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
    private static int countSymbol(String text, char symbol) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == symbol) {
                count++;
            }
        }
        return count;
    }

    private static void analyzeQueue(BlockingQueue<String> queue, char symbol) {
        int maxCount = 0;
        String maxText = "";

        while (true) {
            try {
                String text = queue.take();
                if (text.isEmpty()) {
                    break;
                }

                int count = countSymbol(text, symbol);
                if (count > maxCount) {
                    maxCount = count;
                    maxText = text;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Символ '" + symbol + "': Встречается  = " + maxCount + ", Текст = " + maxText);
    }
    public static void main(String[] args) {
        Thread generatetTextThread = new Thread(() -> {
            for (int i = 0; i < NUM_TEXTS; i++) {
                String text = generateText("abc", TEXT_LENGTH);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                queueA.put("");//помещаю пустые строки в очереди для сигнализации завершении потоков,
                queueB.put("");//чтобы когда поток завершает генерацию текстов- поток анализатор мог завершить свою работу.
                queueC.put("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread threadA = new Thread(() -> analyzeQueue(queueA, 'a'));
        Thread threadB = new Thread(() -> analyzeQueue(queueB, 'b'));
        Thread threadC = new Thread(() -> analyzeQueue(queueC, 'c'));

        generatetTextThread.start();
        threadA.start();
        threadB.start();
        threadC.start();

        try {
            generatetTextThread.join();
            threadA.join();
            threadB.join();
            threadC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
