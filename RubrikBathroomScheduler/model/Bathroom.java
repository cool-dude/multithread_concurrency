package RubrikBathroomScheduler.model;
import RubrikBathroomScheduler.enums.PersonType;
public class Bathroom {
    int size;
    int capacity;
    PersonType type;
    public Bathroom(int capa) {
        size = 0;
        capacity = capa;
        type=PersonType.NONE;
    }
    public int getSize(){ return size;}
    public int getCapacity(){ return capacity;}
    public synchronized void incrementOccupancy(){ 
        if(size<capacity) ++size;
    }
    public synchronized void decrementOccupancy(){ 
        if(size>0) --size;
    }
    public PersonType getBathroomType(){return type;}
    public synchronized void setBathroomType(PersonType t_){
        type=t_;
    }
    @Override
    public String toString() {
        return "Bathroom{" +
                "size=" + size +
                ", capacity='" + capacity + '\'' +
                '}';
    }
}