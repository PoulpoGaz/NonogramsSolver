package fr.poulpogaz.nonogramssolver.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriorityQueueTest {

    @Test
    void test() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(i -> i, 100);

        for (int i = 0; i < 100; i++) {
            queue.insert(i, i);
        }

        int i = 99;
        while (!queue.isEmpty()) {
            assertEquals(i, queue.poll());
            i--;
        }
    }

    @Test
    void setPriorityTest() {
        PriorityQueue<Integer> queue = new PriorityQueue<>(i -> i, 100);

        queue.insert(1, 1);
        queue.insert(2, 2);
        queue.insert(3, 3);
        queue.insert(4, 4);

        queue.setPriority(2, 5);
        queue.setPriority(4, 2);
        assertEquals(2, queue.poll());

        queue.setPriority(3, 0);
        assertEquals(4, queue.poll());

        queue.setPriority(3, 5);
        assertEquals(3, queue.poll());

        queue.insert(5, 5);
        queue.setPriority(5, 0);

        assertEquals(1, queue.poll());
        assertEquals(5, queue.poll());
    }
}
