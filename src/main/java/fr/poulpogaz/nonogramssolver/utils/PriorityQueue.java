package fr.poulpogaz.nonogramssolver.utils;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * A priority queue that guarantees O(log(n)) complexity for all operations except clear...
 */
public class PriorityQueue<E> {

    private final Node<E>[] nodes;

    /**
     * Converts an element to an index in the indices array
     */
    private final Function<E, Integer> elementToIndex;

    /**
     * Each element as an index given by the elementToIndex function.
     * This index is mapped through this variable to the index in the "elemnts" array
     */
    private final int[] indices;

    private int size = 0;

    public PriorityQueue(Function<E, Integer> elementToIndex, int maxSize) {
        this.elementToIndex = elementToIndex;
        this.indices = new int[maxSize];
        nodes = (Node<E>[]) new Node[maxSize];

        Arrays.fill(indices, -1);
    }

    public void clear() {
        size = 0;
        Arrays.fill(indices, -1);

        for (Node<E> node : nodes) {
            if (node != null) {
                node.index = -1;
                node.element = null;
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public E peek() {
        if (isEmpty()) {
            return null;
        } else {
            return nodes[0].element;
        }
    }

    public E poll() {
        if (isEmpty()) {
            return null;
        } else {
            size--;
            swap(0, size);
            shiftDown(0);

            Node<E> removed = nodes[size];

            indices[removed.index] = -1;
            removed.index = -1;

            E e = removed.element;
            removed.element = null;

            return e;
        }
    }

    public void insert(E element, double priority) {
        checkSize();
        Node<E> e = getOrCreate(size, element, priority);
        nodes[size] = e;
        indices[e.index] = size;
        size++;
        shiftUp(size - 1);
    }

    public void setPriority(E element, double newPriority) {
        int i = indices[elementToIndex.apply(element)];

        if (i < 0) {
            throw new NoSuchElementException();
        }

        Node<E> e = nodes[i];
        double old = e.priority;
        e.priority = newPriority;

        if (old < newPriority) {
            shiftUp(i);
        } else if (old > newPriority) {
            shiftDown(i);
        }
    }

    /**
     * O(1) complexity
     */
    public boolean contains(E element) {
        int i = indices[elementToIndex.apply(element)];

        return i >= 0;
    }

    private void checkSize() {
        if (size >= nodes.length) {
            throw new IllegalStateException("Buffer overflow");
        }
    }

    private Node<E> getOrCreate(int i, E element, double priority) {
        Node<E> e = nodes[i];

        if (e == null) {
            e = new Node<>(element, priority, elementToIndex.apply(element));
        } else {
            e.element = element;
            e.priority = priority;
            e.index = elementToIndex.apply(element);
        }

        return e;
    }

    private void shiftUp(int node) {
        int ancestor = ancestor(node);

        while (node != 0 && getPriority(ancestor) < getPriority(node)) {
            swap(node, ancestor);
            node = ancestor;
            ancestor = ancestor(node);
        }
    }

    private void shiftDown(int node) {
        while (true) {
            int child = node;
            int leftChild = leftChild(node);
            int rightChild = rightChild(node);

            if (leftChild < size && getPriority(leftChild) > getPriority(child)) {
                child = leftChild;
            }
            if (rightChild < size && getPriority(rightChild) > getPriority(child)) {
                child = rightChild;
            }

            if (child == node) {
                break;
            }

            swap(child, node);
            node = child;
        }
    }

    private double getPriority(int node) {
        return nodes[node].priority;
    }

    private int leftChild(int i) {
        return 2 * i + 1;
    }

    private int rightChild(int i) {
        return 2 * i + 2;
    }

    private int ancestor(int i) {
        return (i - 1) / 2;
    }

    /**
     * Swap in the element at index i and index j in the elements array. It must also update
     * the indices array
     */
    private void swap(int i, int j) {
        Node<E> temp = nodes[i];
        nodes[i] = nodes[j];
        nodes[j] = temp;

        indices[nodes[i].index] = i;
        indices[nodes[j].index] = j;
    }

    private static class Node<E> {

        private E element;
        private double priority;

        /**
         * index in the indices array
         */
        private int index;

        public Node(E element, double priority, int index) {
            this.element = element;
            this.priority = priority;
            this.index = index;
        }
    }
}
