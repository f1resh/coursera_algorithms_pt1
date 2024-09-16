import java.util.Iterator;
import java.util.NoSuchElementException;

/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */
public class Deque<Item> implements Iterable<Item> {

    private class Node {
        private Item value;
        private Node next;
        private Node prev;

        public Node(Item item) {
            value = item;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    // construct an empty deque
    public Deque() {
        size = 0;
        head = null;
        tail = null;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return (head == null && tail == null);
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Element is empty!");
        Node newNode = new Node(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        }
        else {
            head.next = newNode;
            newNode.prev = head;
            head = newNode;
        }
        ++size;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Element is empty!");
        Node newNode = new Node(item);
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        }
        else {
            tail.prev = newNode;
            newNode.next = tail;
            tail = newNode;
        }
        ++size;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        Item result = head.value;
        if (size == 1) {
            head = null;
            tail = null;
        }
        else {
            Node tmp = head;
            head = head.prev;
            head.next = null;
            tmp.prev = null;
        }
        --size;
        return result;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        Item result = tail.value;
        if (size == 1) {
            head = null;
            tail = null;
        }
        else {
            Node tmp = tail;
            tail = tail.next;
            tail.prev = null;
            tmp.next = null;
        }
        --size;
        return result;
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = head;
        private int idx = 0;

        @Override
        public boolean hasNext() {
            return idx < size();
        }

        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("No next item in the deque");
            Item item = current.value;
            current = current.prev;
            ++idx;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported operation");
        }
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
    }

}
