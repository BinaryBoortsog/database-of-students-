import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student {
    private int studentId;
    private String name;
    private int yearOfBirth;
    private List<Course> courses;

    public Student(String name, int yearOfBirth) {
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.courses = new ArrayList<>();
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        if (courses.contains(course)) {
            throw new IllegalArgumentException("Course already added: " + course);
        }
        courses.add(course);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(", ").append(yearOfBirth);
        for (Course course : courses) {
            sb.append(", ").append(course);
        }
        return sb.toString();
    }
}
