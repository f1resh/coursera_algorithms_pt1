/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int MIN_DEFAULT_CAPACITY = 10;

    private int size;
    private Item[] rq;

    // construct an empty randomized queue
    public RandomizedQueue() {
        rq = (Item[]) new Object[MIN_DEFAULT_CAPACITY];
        size = 0;
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    private int randomIndex() {
        return StdRandom.uniformInt(0, size);
    }

    private void resize() {
        Item[] newRq;
        if (size == rq.length) {
            newRq = (Item[]) new Object[rq.length * 2];
            System.arraycopy(rq, 0, newRq, 0, size);
            rq = newRq;
        }
        else if (rq.length > MIN_DEFAULT_CAPACITY && size <= rq.length / 4) {
            newRq = (Item[]) new Object[rq.length / 2];
            System.arraycopy(rq, 0, newRq, 0, size);
            rq = newRq;
        }
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("Empty argument!");
        resize();
        rq[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Randomized Queue is empty");
        resize();
        int idx = randomIndex();
        Item result = rq[idx];
        Item last = rq[size - 1];
        rq[idx] = last;
        rq[size - 1] = null;
        size--;
        return result;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("Randomized Queue is empty");
        return rq[randomIndex()];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private Item[] order;
        private int index;

        public RandomizedQueueIterator() {
            order = (Item[]) new Object[size()];
            index = size - 1;
            for (int i = 0; i < size(); ++i) {
                order[i] = rq[i];
            }
            StdRandom.shuffle(order);
        }

        @Override
        public boolean hasNext() {
            return index >= 0;
        }

        @Override
        public Item next() {
            if (!hasNext() || isEmpty())
                throw new NoSuchElementException("No more items to iterate");
            return order[index--];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported operation");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {

    }
}
