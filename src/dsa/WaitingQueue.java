package dsa;

/**
 * A manually implemented Queue using singly-linked nodes.nho
 *
 * DSA REQUIREMENT: This is a hand-coded Queue (not java.util.Queue).
 * It is used as a Trainer Waiting List for members waiting to be
 * assigned a trainer when none is immediately available.
 *
 * Operations: enqueue (add to rear), dequeue (remove from front),
 *             peek (view front), isEmpty, size, display.
 *
 * @param <T> Generic type, used with String (memberId) in this project.
 */
public class WaitingQueue<T> {

    /**
     * Inner class representing a single node in the linked list.
     */
    private static class Node<T> {
        T       data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> front; // Points to the first element (dequeue end)
    private Node<T> rear;  // Points to the last element  (enqueue end)
    private int     size;

    /**
     * Initialises an empty queue.
     */
    public WaitingQueue() {
        front = null;
        rear  = null;
        size  = 0;
    }

    /**
     * Adds an element to the rear of the queue.
     *
     * @param data The element to enqueue.
     */
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        if (rear == null) {
            // Queue is empty: front and rear both point to new node
            front = newNode;
            rear  = newNode;
        } else {
            rear.next = newNode;
            rear      = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the element at the front of the queue.
     *
     * @return The front element.
     * @throws RuntimeException if the queue is empty.
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Cannot dequeue: Waiting list is empty.");
        }
        T data = front.data;
        front  = front.next;
        if (front == null) {
            rear = null; // Queue became empty
        }
        size--;
        return data;
    }

    /**
     * Returns the front element without removing it.
     *
     * @return The front element.
     * @throws RuntimeException if the queue is empty.
     */
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Cannot peek: Waiting list is empty.");
        }
        return front.data;
    }

    /**
     * Checks whether the queue is empty.
     *
     * @return true if no elements exist, false otherwise.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements currently in the queue.
     */
    public int size() {
        return size;
    }

    /**
     * Displays all elements in the queue from front to rear.
     */
    public void display() {
        if (isEmpty()) {
            System.out.println("  (Waiting list is empty)");
            return;
        }
        Node<T> current = front;
        int     pos     = 1;
        while (current != null) {
            System.out.printf("  %d. %s%n", pos++, current.data);
            current = current.next;
        }
    }
}
