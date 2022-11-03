package fr.poulpogaz.nonogramssolver.utils;

import java.util.function.Function;

/**
 * A priority queue that guarantees O(log(n)) complexity for all operations
 */
public class PriorityQueue<E> {

    private final Element<E>[] elements;

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
        elements = (Element<E>[]) new Element[maxSize];
    }

    public void clear() {
        size = 0;
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
            return elements[0].element;
        }
    }

    public E poll() {
        if (isEmpty()) {
            return null;
        } else {
            size--;
            swap(0, size);
            shiftDown(0);

            elements[size].index = -1;
            return elements[size].element;
        }
    }

    public void insert(E element, double priority) {
        checkSize();
        Element<E> e = getOrCreate(size, element, priority);
        elements[size] = e;
        indices[e.index] = size;
        size++;
        shiftUp(size - 1);
    }

    public void setPriority(E element, double newPriority) {
        Element<E> e = get(element);
        double old = e.priority;
        e.priority = newPriority;

        if (indices[elementToIndex.apply(element)] != indices[e.index]) {
            throw new IllegalStateException();
        }

        if (old < newPriority) {
            shiftUp(indices[e.index]);
        } else if (old > newPriority) {
            shiftDown(indices[e.index]);
        }
    }

    /**
     * O(1) complexity
     */
    public boolean contains(E element) {
        Element<E> e = get(element);

        if (e == null) {
            return false;
        } else {
            return e.index >= 0;
        }
    }

    private Element<E> get(E element) {
        return elements[indices[elementToIndex.apply(element)]];
    }

    private void checkSize() {
        if (size >= elements.length) {
            throw new IllegalStateException("Buffer overflow");
        }
    }

    private Element<E> getOrCreate(int i, E element, double priority) {
        Element<E> e = elements[i];

        if (e == null) {
            e = new Element<>(element, priority, elementToIndex.apply(element));
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
        return elements[node].priority;
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
        Element<E> temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;

        indices[elements[i].index] = i;
        indices[elements[j].index] = j;
    }

    private static class Element<E> {

        private E element;
        private double priority;

        /**
         * index in the indices array
         */
        private int index;

        public Element(E element, double priority, int index) {
            this.element = element;
            this.priority = priority;
            this.index = index;
        }
    }
}
