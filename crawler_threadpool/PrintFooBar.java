import java.util.concurrent.Semaphore;
class FooBar {
    private int n;
    Semaphore s1 = new Semaphore(0);
    Semaphore s2 = new Semaphore(1);
    public FooBar(int n) {
        this.n = n;
    }
    public void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            // printFoo.run() outputs "foo". Do not change or remove this line.
            s2.acquire(1);
            printFoo.run();
            s1.release(0);
        }
    }
    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            // printBar.run() outputs "bar". Do not change or remove this line.
            s1.acquire(0);
            printBar.run();
            s2.release(1);
        }
    }
}