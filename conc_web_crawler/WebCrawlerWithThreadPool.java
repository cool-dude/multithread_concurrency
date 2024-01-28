package ll_design.conc_multithread.conc_crawler;
import java.util.*;
import java.util.concurrent.*;
interface HTMLParser{
    public List<String> getUrls(String url){}
}
class Sln1 {
    ConcurrentHashMap map = new ConcurrentHashMap<>();
    Random rand = new Random();
    public List<String> crawl(String startUrl, HTMLParser htmlParser) {
        Set<String> seen = map.keySet();
        BlockingQueue<String> q = new LinkedBlockingQueue<>();
        q.add(startUrl);
        String hostname = getHostname(startUrl);
        int N = 35;
        CountDownLatch latch = new CountDownLatch(N);
        Runnable worker = () -> {
            try {
                while (true) {
                    String url = q.poll(25 + rand.nextInt(25), TimeUnit.MILLISECONDS);
                    if (url == null)
                        break;
                    seen.add(url);
                    for (String nextUrl : htmlParser.getUrls(url)) {
                        if (!seen.contains(nextUrl) && isSameHost(hostname, nextUrl)) {
                            q.add(nextUrl);
                        }
                    }
                }
            }
            catch (InterruptedException e) {
            }
            finally {
                latch.countDown();
            }
        };
        for (int w = 0; w < N; w++)
            new Thread(w).start();
        try {
            latch.await();
        }
        catch (Exception e) {
        }
    }
    String getHostname(String url) {
        int idx = url.indexOf("/", 7);
        return idx == -1 ? url : url.substring(0, idx);
    }
    boolean isSameHost(String host, String newUrl) {
        if (newUrl.startsWith(host)) {
            return true;
        }
        return false;
    }
}