package ll_design.conc_multithread;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
public class ConcurrentHashMapExample{
    public static
	void main(String[] args){
        ConcurrentHashMap<String,String> premiumPhone =
			new ConcurrentHashMap<String,String>();
        premiumPhone.put("Apple", "iPhone6");
        premiumPhone.put("HTC", "HTC one");
        premiumPhone.put("Samsung","S6");

        Iterator iterator =
		premiumPhone.keySet().iterator();
        while (iterator.hasNext())
        {
            System.out.println(
			premiumPhone.get(iterator.next()));
            premiumPhone.put("Sony", "Xperia Z");
        }
    }
}
/*1. Thread -Safe :
ConcurrentHashMap is
thread-safe that is ,
the code can be accessed
by single thread at a time .
while HashMap is not thread-safe .
2. Synchronization Method :
HashMap can be synchronized by using
synchronizedMap(HashMap)  method.
By using this,method we get a HashMap
object which is equivalent
to the HashTable object.
So every modification
is performed
on  Map is locked on Map object.*/
import java.util.*;
public class HashMapSynchronization {
    public static void main(String[] args) {
        // create map
        Map<String,String> map =
		new HashMap<String,String>();

        // populate the map
        map.put("1","ALIVE ");
        map.put("2","IS");
        map.put("3","AWESOME");

        // create a synchronized map
        Map<String,String> syncMap =
		 Collections.synchronizedMap(map);
        System.out.println(
		"Synchronized map :"+syncMap);
    }
}
/*3. Null Key:
ConcurrentHashMap does
not allow NULL values .
So the key can not be null in
ConcurrentHashMap .
While In HashMap there
can only be one null key.

4. Performance:
In multiple threaded
environment HashMap is
usually faster than
ConcurrentHashMap.
As only single thread can access the
certain portion of the
ConcurrentHashMap and thus
reducing the performance.
While in HashMap any
number of threads
can access the code
at the same time.*/