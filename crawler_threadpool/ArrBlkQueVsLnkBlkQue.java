/*ArrayBlockingQueue is bounded, blocking queue that
stores the elements internally in an array.

LinkedBlockingQueue
The LinkedBlockingQueue keeps the elements internally in a
linked structure (linked nodes).
Similarity:
The ArrayBlockingQueue/LinkedBlockingQueue stores the elements
internally in FIFO (First In, First Out) order. The head of the
queue is the element which has been in queue the longest time,
and the tail of the queue is the element which has been in the
queue the shortest time.

Differences:
1.a)ArrayBlockingQueue is backed by array that size
will never change after creation. Setting the capacity
to Integer.MAX_VALUE would create a big array with
high costs in space. ArrayBlockingQueue is always bounded.
b)LinkedBlockingQueue creates nodes dynamically until the
capacity is reached. This is by default Integer.MAX_VALUE.
 Using such a big capacity has no extra costs in space.
 LinkedBlockingQueue is optionally bounded.

2.LinkedBlockingQueue has a putLock and a takeLock for insertion
and removal respectively but ArrayBlockingQueue uses only 1 lock.
ArrayBlockingQueue uses single-lock double condition algorithm
and LinkedBlockingQueue is variant of the "two lock queue"
algorithm and it has 2 locks 2 conditions ( takeLock , putLock).
Two Lock Queue algorithm is being used by LinkedBlockingQueue
Implementation.Thus LinkedBlockingQueue's take and put can
work concurrently, but this is not the case with ArrayBlockingQueue.*/
static class Node<E> {
    E item;
    Node<E> next;
    Node(E x) { item = x; }
}
public LinkedBlockingQueue(int capacity){
    if (capacity < = 0) throw new IllegalArgumentException();
    this.capacity = capacity;
    last = head = new Node< E >(null);   // Maintains a underlying linkedlist. ( Use when size is not known )
}
public ArrayBlockingQueue(int capacity, boolean fair) {
    if (capacity < = 0)
        throw new IllegalArgumentException();
    this.items = new Object[capacity]; // Maintains a underlying array
    lock = new ReentrantLock(fair);
    notEmpty = lock.newCondition();
    notFull =  lock.newCondition();
}