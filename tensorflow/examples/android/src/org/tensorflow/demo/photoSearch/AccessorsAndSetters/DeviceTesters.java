package org.tensorflow.demo.photoSearch.AccessorsAndSetters;

/**
 * Created by mgo983 on 8/4/17.
 */

public class DeviceTesters {
    private String id;
    private String firstName;
    private String lastName;

    public DeviceTesters(){

    }

    public DeviceTesters(String _firstName, String _lastName){
        this.firstName = _firstName;
        this.lastName = _lastName;
    }

    public String getId(){ return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() {return lastName; }

    public void setId(String _id){ this.id = _id; }
    public void setFirstName(String _firstName){ this.firstName = _firstName; }
    public void setLastName(String _lastName){ this.lastName = _lastName; }
}
