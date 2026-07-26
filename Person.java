package model;

public class Person {

    protected String name;
    protected int age;
    protected String nationalId;

    public Person(String name, int age, String id) {
        this.name = name;
        this.age = age;
        this.nationalId = id;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getNationalId() { return nationalId; }

}
