import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/*UBER RATE LIMITER 
rate limiter with multithreading, threads
Also as metrics-collector, metric collector*/
class RateLimiter1 {
    public int REQUEST_LIMIT = 100;
    public Long TIME_LIMIT = 1000L;

    public class HitCounter {
        public Queue<Long> queue;

        public HitCounter() {
            queue = new LinkedList<>();
        }

        public boolean hit(long timestamp) {
            /* when a timestamp hit, we should poll all the timestamp before TIME_LIMIT */
            while (!queue.isEmpty() && timestamp - queue.peek() >= TIME_LIMIT)
                queue.poll();
            if (queue.size() < REQUEST_LIMIT) {
                queue.add(timestamp);
                return true;
            }
            return false;
        }
    }

    public HashMap<String, HitCounter> clientTimeStampMap = new HashMap<>();

    public boolean isAllow(String clientId) {
        long currTime = System.currentTimeMillis();
        if (!clientTimeStampMap.containsKey(clientId)) {
            HitCounter h = new HitCounter();
            h.hit(currTime);
            clientTimeStampMap.put(clientId, h);
            return true;
        } else {
            HitCounter h = clientTimeStampMap.get(clientId);
            return h.hit(currTime);
        }
    }
}

/* Multi-threaded Rate-Limiter */
class RateLimiter2 {
    public static final long REQUESTS_COUNTER_TTL_SEC = 10L;
    public static final long REQUESTS_BLOCK_TTL_SEC = 24 * 60 * 60L;

    private final Map<String, ReqCounter> requestCounters = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedRequestIds = new ConcurrentHashMap<>();
    private final ScheduledExecutorService reqTtlCleanupExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService blockedReqTtlCleanupExecutor = Executors.newSingleThreadScheduledExecutor();
    {
        reqTtlCleanupExecutor.scheduleAtFixedRate(() -> {
            long curSecond = System.currentTimeMillis() / 1000;
            requestCounters
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().second < curSecond)
                    .forEach(entry -> requestCounters.remove(entry.getKey()));
        },
                REQUESTS_COUNTER_TTL_SEC,
                REQUESTS_COUNTER_TTL_SEC,
                TimeUnit.SECONDS);
        blockedReqTtlCleanupExecutor.scheduleAtFixedRate(() -> {
            long curSecond = System.currentTimeMillis() / 1000;
            blockedRequestIds
                    .entrySet()
                    .stream()
                    .filter(entry -> (curSecond - entry.getValue()) > REQUESTS_BLOCK_TTL_SEC)
                    .forEach(entry -> requestCounters.remove(entry.getKey()));
        },
                REQUESTS_BLOCK_TTL_SEC,
                REQUESTS_BLOCK_TTL_SEC,
                TimeUnit.SECONDS);
    }

    public boolean isAllow(String reqId) {
        if (blockedRequestIds.containsKey(reqId))
            return false;
        else {
            long curSecond = System.currentTimeMillis() / 1000;
            ReqCounter reqCounter = requestCounters.compute(reqId, (k, v) -> {
                ReqCounter newValue = v;
                if (newValue == null || newValue.second > curSecond)
                    newValue = new ReqCounter(curSecond, 0L);
                newValue.count = 1L + newValue.count;
                return newValue;
            });
            if (reqCounter.count < 100)
                return true;
            else {
                blockedRequestIds.put(reqId, curSecond);
                return false;
            }
        }
    }

    private static class ReqCounter {
        private Long second = 0L;
        private Long count = 0L;

        public ReqCounter(Long second, Long count) {
            this.second = second;
            this.count = count;
        }
    }
}