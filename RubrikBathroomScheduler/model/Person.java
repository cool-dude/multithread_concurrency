package RubrikBathroomScheduler.model;

import RubrikBathroomScheduler.enums.PersonType;

public class Person implements Runnable{
    String id;
    PersonType pt;
    public Person(String id_,PersonType pt_,Ba){
        id=id_;
        pt=pt_;
    }
    public PersonType getType(){ return pt;}
    @Override
    public void run() {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'run'");
        
    }
}
    
