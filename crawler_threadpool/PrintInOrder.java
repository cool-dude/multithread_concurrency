/*LC1115: Print FooBar Alternately
https://leetcode.com/problems/print-foobar-alternately/
Suppose you are given the following code:
class FooBar {
  public void foo() {
    for (int i = 0; i < n; i++) {
      print("foo");
    }
  }
  public void bar() {
    for (int i = 0; i < n; i++) {
      print("bar");
    }
  }
}
The same instance of FooBar will be passed to
two different threads. Thread A will call foo()
while thread B will call bar(). Modify the
given program to output "foobar" n times.
Example 1:
Input: n = 1
Output: "foobar"
Explanation: There are two threads being
fired asynchronously. One of them calls foo(),
while the other calls bar(). "foobar" is being output 1 time.

Example 2:
Input: n = 2
Output: "foobarfoobar"
Explanation: "foobar" is being output 2 times.*/
/*"Semaphore is a bowl of marbles" - Professor Stark
Semaphore is a bowl of marbles (or locks in this case).
If you need a marble, and there are none, you wait.
You wait until there is one marble and then you take it.
If you release(), you will add one marble to the bowl
(from thin air). If you release(100), you will add
100 marbles to the bowl (from thin air).
The thread calling third() will wait until the end of second()
when it releases a '3' marble. The second() will wait
until the end of first() when it releases a '2' marble.
Since first() never acquires anything, it will never wait.
There is a forced wait ordering.
With semaphores, you can start out with 1 marble or 0
marbles or 100 marbles. A thread can take marbles (up
until it's empty) or put many marbles (out of thin air) at a time.
Upvote and check out my other concurrency solutions.*/
import java.util.concurrent.*;
class Foo {
    Semaphore run2, run3;
    public void first() { print("first"); }
    public void second() { print("second"); }
    public void third() { print("third"); }
    public Foo() {
        run2 = new Semaphore(0);
        run3 = new Semaphore(0);
    }
    public void first(Runnable printFirst) throws InterruptedException {
        printFirst.run();
        run2.release();
    }
    public void second(Runnable printSecond) throws InterruptedException {
        run2.acquire();
        printSecond.run();
        run3.release();
    }
    public void third(Runnable printThird) throws InterruptedException {
        run3.acquire();
        printThird.run();
    }
}