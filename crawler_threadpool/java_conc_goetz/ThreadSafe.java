package java_conc_goetz;
@ThreadSafe
class SeqGen {
    @GuardedBy("this") private int nxtVal;
    public synchronized int getNext(){
        return nxtVal++;
    }
}
@ThreadSafe
public class StatelessFactorizer implements Servlet { 
    public void service(ServletRequest req, ServletResponse resp) { 
        BigInteger i = extractFromRequest(req); 
        BigInteger[] factors = factor(i); 
        encodeIntoResponse(resp, factors); 
    } 
} 
/*NOT SUGGESTED */
@NotThreadSafe 
public class UnsafeCountingFactorizer implements Servlet { 
    private long count = 0; 
    public long getCount() { return count; } 
    public void service(ServletRequest req, ServletResponse resp) { 
        BigInteger i = extractFromRequest(req); 
        BigInteger[] factors = factor(i); 
        ++count; 
        encodeIntoResponse(resp, factors); 
    } 
}
/*RACE CONDITION NOT SUGGESTED */
@NotThreadSafe 
public class LazyInitRace { 
    private ExpensiveObject instance = null; 
    public ExpensiveObject getInstance() { 
        if (instance == null) 
            instance = new ExpensiveObject(); 
        return instance; 
    } 
} 
/*Servlet that Counts Requests Using AtomicLong. */
@ThreadSafe 
public class CountingFactorizer implements Servlet { 
    private final AtomicLong count = new AtomicLong(0); 
    public long getCount() { return count.get(); } 
    public void service(ServletRequest req, ServletResponse resp) { 
        BigInteger i = extractFromRequest(req); 
        BigInteger[] factors = factor(i); 
        count.incrementAndGet(); 
        encodeIntoResponse(resp, factors); 
    } 
}
@NotThreadSafe 
public class UnsafeCachingFactorizer implements Servlet { 
    private final AtomicReference<BigInteger> lastNumber 
        = new AtomicReference<BigInteger>(); 
    private final AtomicReference<BigInteger[]> lastFactors 
        = new AtomicReference<BigInteger[]>(); 
    public void service(ServletRequest req, ServletResponse resp) { 
        BigInteger i = extractFromRequest(req); 
        if (i.equals(lastNumber.get())) 
            encodeIntoResponse(resp, lastFactors.get() ); 
        else { 
            BigInteger[] factors = factor(i); 
            lastNumber.set(i); 
            lastFactors.set(factors); 
            encodeIntoResponse(resp, factors); 
        } 
    } 
} 
@ThreadSafe 
public class CachedFactorizer implements Servlet { 
    @GuardedBy("this") private BigInteger lastNumber; 
    @GuardedBy("this") private BigInteger[] lastFactors; 
    @GuardedBy("this") private long hits; 
    @GuardedBy("this") private long cacheHits; 
    public synchronized long getHits() { return hits; } 
    public synchronized double getCacheHitRatio() { 
        return (double) cacheHits / (double) hits; 
    } 
    public void service(ServletRequest req, ServletResponse resp) { 
        BigInteger i = extractFromRequest(req); 
        BigInteger[] factors = null; 
        synchronized (this) { 
            ++hits; 
            if (i.equals(lastNumber)) { 
                ++cacheHits; 
                factors = lastFactors.clone(); 
            } 
        } 
        if (factors == null) { 
            factors = factor(i); 
            synchronized (this) { 
                lastNumber = i; 
                lastFactors = factors.clone(); 
            } 
        } 
        encodeIntoResponse(resp, factors); 
    } 
} 
/*Listing 3.8. Using a Factory Method to Prevent the this Reference from Escaping 
During Construction.*/
public class SafeListener { 
    private final EventListener listener; 
    private SafeListener() { 
        listener = new EventListener() { 
        public void onEvent(Event e) { 
            doSomething(e); 
        } 
    }; 
 } 
 public static SafeListener newInstance(EventSource source) { 
    SafeListener safe = new SafeListener(); 
    source.registerListener(safe.listener); 
    return safe; 
}
/*IMMUITABLE HOLDER FOR CACHING NUMBER/FACTORS */
@Immutable 
class OneValueCache { 
    private final BigInteger lastNumber; 
    private final BigInteger[] lastFactors; 
    public OneValueCache(BigInteger i, 
        BigInteger[] factors) { 
        lastNumber = i; 
        lastFactors = Arrays.copyOf(factors, factors.length); 
    } 
    public BigInteger[] getFactors(BigInteger i) { 
        if (lastNumber == null || !lastNumber.equals(i)) 
            return null; 
        else 
            return Arrays.copyOf(lastFactors, lastFactors.length); 
    } 
}

@ThreadSafe 
public class MonitorVehicleTracker { 
    @GuardedBy("this") 
    private final Map<String, MutablePoint> locations; 
    public MonitorVehicleTracker( 
        Map<String, MutablePoint> locations) { 
            this.locations = deepCopy(locations); 
    } 
    public synchronized Map<String, MutablePoint> getLocations() { 
        return deepCopy(locations); 
    } 
    public synchronized MutablePoint getLocation(String id) { 
        MutablePoint loc = locations.get(id); 
        return loc == null ? null : new MutablePoint(loc); 
    } 
    public synchronized void setLocation(String id, int x, int y) { 
        MutablePoint loc = locations.get(id); 
        if (loc == null) 
        throw new IllegalArgumentException("No such ID: " + id); 
        loc.x = x; 
        loc.y = y; 
    } 
    private static Map<String, MutablePoint> deepCopy( 
        Map<String, MutablePoint> m) { 
        Map<String, MutablePoint> result = 
        new HashMap<String, MutablePoint>(); 
        for (String id : m.keySet()) 
            result.put(id, new MutablePoint(m.get(id))); 
        return Collections.unmodifiableMap(result); 
    } 
} 
@NotThreadSafe 
public class MutablePoint { 
    public int x, y; 
    public MutablePoint() { x = 0; y = 0; } 
    public MutablePoint(MutablePoint p) { 
        this.x = p.x; 
        this.y = p.y; 
    }   
} 

@ThreadSafe 
public class PublishingVehicleTracker { 
    private final Map<String, SafePoint> locations; 
    private final Map<String, SafePoint> unmodifiableMap; 
    public PublishingVehicleTracker( 
        Map<String, SafePoint> locations) { 
            this.locations 
            = new ConcurrentHashMap<String, SafePoint>(locations); 
            this.unmodifiableMap 
                = Collections.unmodifiableMap(this.locations); 
        }   
        public Map<String, SafePoint> getLocations() { 
            return unmodifiableMap; 
        } 
        public SafePoint getLocation(String id) { 
            return locations.get(id); 
        } 
        public void setLocation(String id, int x, int y) { 
            if (!locations.containsKey(id)) 
                throw new IllegalArgumentException( 
                "invalid vehicle name: " + id); 
            locations.get(id).set(x, y); 
        } 
} 
