package RubrikBathroomScheduler;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import RubrikBathroomScheduler.enums.PersonType;
import RubrikBathroomScheduler.model.Bathroom;
import RubrikBathroomScheduler.model.Person;
public class CustomThreadPoolExecutor {
    BlockingQueue<Person> repubs,dems;
    BlockingQueue<Person> curPersons;
    private final Thread[] workerThreads;
    Bathroom bathroom;
    private Integer index;
    PersonType personType;
    public CustomThreadPoolExecutor(Integer poolSize,List<Person> senators) {
        this.repubs = new LinkedBlockingDeque<>();
        this.dems = new LinkedBlockingDeque<>();
        this.workerThreads = new Thread[poolSize];
        this.index = 0;
        fillupWaitingQueue(senators);
        startThreads();
    }
    public void startThreads() {
        for (int i = 0; i < workerThreads.length; i++) {
            int finalI = i;
            workerThreads[i] = new Thread(() -> consume(finalI));
            workerThreads[i].start();
        }
    }
    public void fillupWaitingQueue(List<Person> senators) {
        BlockingQueue<Person> b;
        for(Person person:senators){
            if(person.getType()==PersonType.DEM){
                b=dems;
            }
            else{
                b=repubs;
            }
            synchronized (b) {
                System.out.println("Adding person");
                b.add(person);
                index++;
                b.notifyAll();
            }
        }       
    }
    private void consume(Integer threadId) throws InterruptedException {
        while (true) {
            System.out.println("Thread: " + threadId + " is trying to acquire lock");
            Person task;
            BlockingQueue<Person> prev=curPersons;
            curPersons=threadId==0?repubs:(bathroom.getBathroomType()!=PersonType.NONE)
                ?prev:(prev==repubs?dems:repubs);
            PersonType pType=curPersons==dems?PersonType.DEM:PersonType.REP;
            synchronized (curPersons) {
                System.out.println("Thread: " + threadId + " has acquired lock");
                while (bathroom.getBathroomType()!=pType||bathroom.getSize()==bathroom.getCapacity()) {
                    System.out.println("Thread: " + threadId + " is waiting");
                    try {
                        curPersons.wait();
                    } 
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                index--;
                System.out.println("Thread: " + threadId + " is consuming task at index: " + index);
                task = curPersons.poll();
            }
            bathroom.setBathroomType(task.getType());
            bathroom.incrementOccupancy();
            task.run();
            curPersons.wait(curPersons.remainingCapacity()*1000);
            bathroom.decrementOccupancy();
            if(bathroom.getSize()==0) bathroom.setBathroomType(PersonType.NONE);
        }
    }
}