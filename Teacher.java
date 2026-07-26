package model;

public class Teacher extends Person {

    int units;
    double ratePerUnit;

    public Teacher(String n, int a, String id, int u, double r) {
        super(n, a, id);
        units = u;
        ratePerUnit = r;
    }

    public double calculateSalary() {
        return units * ratePerUnit;
    }

}
