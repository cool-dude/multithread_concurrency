class BoundedSemaphore {
    int signal;
    int capacity;
    public BoundedSemaphore(int cap) {
        signal = 0;
        capacity = cap;
    }
    /* Notice how the acquire() method now
     * blocks if the number of signals is equal to the upper bound.
     * Not until a thread has called release() will
     * the thread calling take() be allowed to deliver its signal,
     * if the BoundedSemaphore has reached its upper signal limit.
     * If the queue size is not equal to either bound when enqueue() or dequeue() is called,
     * there can be no threads waiting to either enqueue or dequeue items.*/
    public synchronized void acquire() throws InterruptedException {
        while(signal == capacity) {
            wait();
        }
        if(signal == 0) {
            notifyAll();
        }
        signal++;
    }
    public synchronized void release() throws InterruptedException {
        while(signal == 0) {
            wait();
        }
        if(signal == capacity) {
            notifyAll();
        }
        signal --;
    }
}