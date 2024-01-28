package java_conc_goetz;
//1 Thread / WebServer
class ThreadPerTaskWebServer { 
    public static void main(String[] args) throws IOException { 
        ServerSocket socket = new ServerSocket(80); 
        while (true) { 
            final Socket connection = socket.accept(); 
            Runnable task = new Runnable() { 
                public void run() { 
                    handleRequest(connection); 
                } 
            }; 
            new Thread(task).start(); 
        } 
    } 
}
class TaskExecutionWebServer { 
    private static final int NTHREADS = 100; 
    private static final Executor exec 
        = Executors.newFixedThreadPool(NTHREADS); 
    public static void main(String[] args) throws IOException { 
        ServerSocket socket = new ServerSocket(80); 
        while (true) { 
        final Socket connection = socket.accept(); 
    Runnable task = new Runnable() { 
        public void run() { 
            handleRequest(connection); 
        } 
    }; 
    exec.execute(task); 
    } 
 } 
}
public interface ExecutorService extends Executor { 
    void shutdown(); 
    List<Runnable> shutdownNow(); 
    boolean isShutdown(); 
    boolean isTerminated(); 
    boolean awaitTermination(long timeout, TimeUnit unit) 
    throws InterruptedException; 
    // ... additional convenience methods for task submission 
}
/*Listing 6.11. Callable and Future Interfaces.*/
public interface Callable<V> { 
    V call() throws Exception; 
} 
public interface Future<V> { 
    boolean cancel(boolean mayInterruptIfRunning); 
    boolean isCancelled(); 
    boolean isDone(); 
    V get() throws InterruptedException, ExecutionException, 
        CancellationException; V get(long timeout, TimeUnit unit) 
        throws InterruptedException, ExecutionException, CancellationException, TimeoutException; 
} 

public class Renderer { 
    private final ExecutorService executor; 
    Renderer(ExecutorService executor) { this.executor = executor; } 
    void renderPage(CharSequence source) { 
        final List<ImageInfo> info = scanForImageInfo(source); 
        CompletionService<ImageData> completionService = 
        new ExecutorCompletionService<ImageData>(executor); 
        for (final ImageInfo imageInfo : info) 
            completionService.submit(new Callable<ImageData>() { 
            public ImageData call() { 
                return imageInfo.downloadImage(); 
            } 
        }); 
        renderText(source); 
        try { 
            for (int t = 0, n = info.size(); t < n; t++) { 
                Future<ImageData> f = completionService.take(); 
                ImageData imageData = f.get(); 
                renderImage(imageData); 
            } 
        } 
        catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        } 
        catch (ExecutionException e) { 
            throw launderThrowable(e.getCause()); 
        } 
    } 
}
// LogWriter
public class LogWriter { 
    private final BlockingQueue<String> queue; 
    private final LoggerThread logger; 
    public LogWriter(Writer writer) { 
        this.queue = new LinkedBlockingQueue<String>(CAPACITY); 
        this.logger = new LoggerThread(writer); 
    } 
    public void start() { logger.start(); } 
    public void log(String msg) throws InterruptedException { 
        queue.put(msg); 
    } 
    private class LoggerThread extends Thread { 
    private final PrintWriter writer; 
    ... 
    public void run() { 
        try { 
            while (true) 
                writer.println(queue.take()); 
        } 
        catch(InterruptedException ignored) { 
        } 
        finally { 
            writer.close(); 
        } 
    } 
    } 
} 

public class LogService { 
    private final BlockingQueue<String> queue; 
    private final LoggerThread loggerThread; 
    private final PrintWriter writer; 
    @GuardedBy("this") private boolean isShutdown; 
    @GuardedBy("this") private int reservations; 
    public void start() { loggerThread.start(); } 
    public void stop() { 
        synchronized (this) { isShutdown = true; } 
        loggerThread.interrupt(); 
    } 
    public void log(String msg) throws InterruptedException { 
        synchronized (this) { 
            if (isShutdown) 
                throw new IllegalStateException(...); 
            ++reservations; 
        } 
        queue.put(msg); 
    } 
    private class LoggerThread extends Thread { 
    public void run() { 
    try { 
        while (true) { 
        try { 
 synchronized (this) { 
 if (isShutdown && reservations == 0) 
 break; 
 } 
 String msg = queue.take(); 
 synchronized (this) { --reservations; } 
 writer.println(msg); 
 } catch (InterruptedException e) { /* retry */ } 
 } 
 } finally { 
 writer.close(); 
 } 
 } 
 } 
}

