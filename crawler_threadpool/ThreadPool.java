public class MyThreadPool {
    private MyBlockingQueue<Runnable> myqueue;
    private List<MyThread> threads;
    private boolean isStopped;
    public MyThreadPool(int threadCount, int maxNoOfTasks) {
        myqueue = new MyBlockingQueue<>(maxNoOfTasks);
        threads = new ArrayList<>();
        for(int i = 0; i < threadCount; i ++) {
            threads.add(new MyThread(myqueue));
        }
        for(MyThread thread : threads) {
            thread.start();
        }
    }
    public synchronized void execute(Runnable task) throws InterruptedException {
        if(isStopped) {
            throw new IllegalStateException("ThreadPool is stopped");
        }
        myqueue.enqueue(task);
    }
    public synchronized void stop() {
        isStopped = true;
        for(MyThread pt : threads) {
            pt.doStop();
        }
    }
    private static class MyThread extends Thread {
        private MyBlockingQueue<Runnable> taskQueue;
        private boolean isStopped;
        public MyThread(MyBlockingQueue<Runnable> blockingQueue) {
            taskQueue = blockingQueue;
            isStopped = false;
        }
        public void run() {
            while(!isStopped) {
                try {
                    Runnable r = taskQueue.dequeue();
                    r.run();
                } catch(Exception e) {
                    // log
                }
            }
        }
        public synchronized void doStop() {
            isStopped = true;
            this.interrupt();
        }
        public synchronized boolean isStopped() {
            return isStopped;
        }
    }
}