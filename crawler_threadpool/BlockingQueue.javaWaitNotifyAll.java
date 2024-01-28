public class MyBlockingQueue<T> {
    private List<T> queue;
    private int limit = 10;
    public MyBlockingQueue(int limit) {
        queue = new LinkedList<>();
        this.limit = limit;
    }
    /* Notice how notifyAll() is only called
     * from enqueue() and dequeue() if the queue size is equal
     * to the size bounds (0 or limit).
     *
     * If the queue size is not equal to either bound when enqueue() or dequeue() is called,
     * there can be no threads waiting to either enqueue or dequeue items.*/
    public synchronized void enqueue(T t) throws InterruptedException {
        while(queue.size() == limit) {
            wait();
        }
        if(queue.size() == 0) {
            notifyAll();
        }
        queue.add(t);
    }
    public synchronized T dequeue() throws InterruptedException {
        while(queue.size() == 0) {
            wait();
        }
        if(queue.size() == limit) {
            notifyAll();
        }
        return queue.remove(0);
    }
}