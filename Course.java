import java.util.Objects;

public class Course {
    private String department;
    private int courseNumber;
    private int credits;

    public Course(String department, int courseNumber, int credits) {
        this.department = department;
        this.courseNumber = courseNumber;
        this.credits = credits;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return courseNumber == course.courseNumber && credits == course.credits && department.equals(course.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(department, courseNumber, credits);
    }

    @Override
    public String toString() {
        return department + " " + courseNumber + " (" + credits + " credits)";
    }
}
