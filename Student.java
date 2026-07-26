package model;

import java.util.ArrayList;

public class Student extends Person {

    public ArrayList<Course> courses = new ArrayList<>();

    public Student(String n, int a, String id) {
        super(n, a, id);
    }

    public void addCourse(Course c) {
        courses.add(c);
    }

    public double calculateGPA() {
        double total = 0;
        int units = 0;

        for (Course c : courses) {
            total += c.grade * c.units;
            units += c.units;
        }

        if (units == 0) return 0;

        return total / units;
    }

}
