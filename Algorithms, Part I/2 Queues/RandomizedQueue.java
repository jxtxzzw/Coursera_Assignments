import java.util.Iterator;
import java.util.NoSuchElementException;
 
import edu.princeton.cs.algs4.StdRandom;
 
public class RandomizedQueue<Item> implements Iterable<Item> {
 
    private Node<Item> head;
    private int size;
 
    // construct an empty randomized queue
    public RandomizedQueue() {
    }
 
    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }
 
    // return the number of items on the randomized queue
    public int size() {
        return size;
    }
 
    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node<Item> node = new Node<>(item);
        node.setNext(head);
        head = node;
        size++;
    }
 
    private Item randomize(boolean delete) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int r = StdRandom.uniform(0, size);
        if (r == 0) {
            // remove head only
            Item item = head.getItem();
            if (delete) {
                head = head.getNext();
                size--;
            }
            return item;
        }
        // go through, and keep pre and cur
        Node<Item> pre = head;
        Node<Item> cur = head.getNext();
        for (int i = 1; i < r; i++) {
            pre = cur;
            cur = cur.getNext();
        }
        Item item = cur.getItem();
        if (delete) {
            pre.setNext(cur.getNext());
            size--;
        }
        return item;
    }
 
    // remove and return a random item
    public Item dequeue() {
        return randomize(true);
    }
 
    // return a random item (but do not remove it)
    public Item sample() {
        return randomize(false);
    }
 
    // return an independent iterator over items in random order
    @Override
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
 
            int left = size;
            boolean[] visited = new boolean[left];
 
            @Override
            public boolean hasNext() {
                return left > 0;
            }
 
            @Override
            public Item next() {
                if (left == 0) {
                    throw new NoSuchElementException();
                }
                int r = StdRandom.uniform(size);
                while (visited[r]) {
                    r = StdRandom.uniform(size);
                }
                visited[r] = true;
                left--;
                Node<Item> cur = head;
 
                for (int i = 0; i < r; i++) {
                    cur = cur.getNext();
                }
                return cur.getItem();
            }
        };
    }
 
    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> rq = new RandomizedQueue<>();
        rq.enqueue(1);
        rq.enqueue(2);
        rq.enqueue(3);
        System.out.println(rq.isEmpty());
        System.out.println(rq.size());
        System.out.println(rq.sample());
        System.out.println(rq.dequeue());
        Iterator<Integer> it = rq.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
 
}
 
class Node<Item> {
    private final Item item;
    private Node<Item> next;
 
    public Node(Item item) {
        this.item = item;
        next = null;
    }
 
    public void setNext(Node<Item> next) {
        this.next = next;
    }
 
    public Node<Item> getNext() {
        return next;
    }
 
    public Item getItem() {
        return item;
    }
}