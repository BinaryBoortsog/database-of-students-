import java.io.*;
import java.util.*;

public class Database {
    private List<Student> students;
    private PriorityQueue<Integer> availableIds;
    private static int nextAvailableId = 1;

    public Database() {
        students = new ArrayList<>();
        availableIds = new PriorityQueue<>();
    }

    public void importStudentsFromFile(String filename) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");

                if (data.length < 3 || (data.length - 2) % 3 != 0) {
                    throw new IllegalArgumentException("Invalid student record format: " + line);
                }

                String name = data[0];
                int yearOfBirth = Integer.parseInt(data[1]);

                Student student = new Student(name, yearOfBirth);
                for (int i = 2; i < data.length; i += 3) {
                    String department = data[i];
                    int courseNumber = Integer.parseInt(data[i + 1]);
                    int credits = Integer.parseInt(data[i + 2]);
                    student.addCourse(new Course(department, courseNumber, credits));
                }

                addStudent(student);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in student record: " + e.getMessage());
        }
    }

    public void exportStudentsToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            for (Student student : students) {
                writer.println(student);
            }
        }
    }

    public void addStudent(Student student) {
        int studentId;
        if (availableIds.isEmpty()) {
            studentId = nextAvailableId++;
        } else {
            studentId = availableIds.poll();
        }
        student.setStudentId(studentId);
        students.add(student);
        System.out.println("Student added successfully with ID: " + studentId);
    }

    public void deleteStudent(int studentId) {
        int indexToRemove = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId() == studentId) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            System.err.println("Error: No student found with ID " + studentId);
            return;
        }

        Student deletedStudent = students.remove(indexToRemove);
        availableIds.add(deletedStudent.getStudentId());
        System.out.println("Student with ID " + studentId + " has been deleted.");
    }

    public void updateStudent(int studentId, Student updatedStudent) {
        int indexToUpdate = -1;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getStudentId() == studentId) {
                indexToUpdate = i;
                break;
            }
        }

        if (indexToUpdate == -1) {
            System.err.println("Error: No student found with ID " + studentId);
            return;
        }

        Student existingStudent = students.get(indexToUpdate);

        if (!updatedStudent.getName().isBlank()) {
            existingStudent.setName(updatedStudent.getName());
            System.out.println("Student name updated successfully.");
        }

        if (updatedStudent.getYearOfBirth() > 0) {
            existingStudent.setYearOfBirth(updatedStudent.getYearOfBirth());
            System.out.println("Year of birth updated successfully.");
        }

        List<Course> updatedCourses = updatedStudent.getCourses();
        if (!updatedCourses.isEmpty()) {
            for (Course course : updatedCourses) {
                try {
                    existingStudent.addCourse(course);
                    System.out.println("Course added successfully: " + course);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public Student searchStudentById(int studentId) {
        for (Student student : students) {
            if (student.getStudentId() == studentId) {
                return student;
            }
        }
        return null;
    }

    public List<Student> searchStudentByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Search name cannot be empty or null.");
        }

        String searchTerm = name.toLowerCase();
        List<Student> matchingStudents = new ArrayList<>();

        for (Student student : students) {
            if (student.getName().toLowerCase().contains(searchTerm)) {
                matchingStudents.add(student);
            }
        }

        return matchingStudents;
    }

    public List<Student> searchStudentsByCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null.");
        }

        List<Student> matchingStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.getCourses().contains(course)) {
                matchingStudents.add(student);
            }
        }
        return matchingStudents;
    }

    public void printAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students in the database.");
            return;
        }

        System.out.println("STUDENT DATABASE");
        System.out.println("-----------------");

        for (Student student : students) {
            System.out.println(student.toString());
            for (Course course : student.getCourses()) {
                System.out.println("\t" + course.toString());
            }
            System.out.println("-----------------");
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Database <input_file>");
            return;
        }

        String inputFilename = args[0];
        Database db = new Database();

        try {
            db.importStudentsFromFile(inputFilename);
        } catch (FileNotFoundException e) {
            System.err.println("Error: Input file not found - " + inputFilename);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        do {
            System.out.println("\nDATABASE OF STUDENTS!");
            System.out.println("=====================");
            System.out.println("Choose from the menu of options:");
            System.out.println("1. Print the student details.");
            System.out.println("2. Search for a student.");
            System.out.println("3. Add a student record.");
            System.out.println("4. Delete a student record.");
            System.out.println("5. Update a student record.");
            System.out.println("6. Exit the database.");
            System.out.print("Enter your option: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        db.printAllStudents();
                        break;
                    case 2:
                        System.out.println("Search by (1) ID, (2) Name, or (3) Course?: ");
                        int searchChoice = scanner.nextInt();
                        scanner.nextLine();

                        switch (searchChoice) {
                            case 1:
                                System.out.print("Enter student ID: ");
                                int searchId = scanner.nextInt();
                                scanner.nextLine();
                                Student foundById = db.searchStudentById(searchId);
                                if (foundById != null) {
                                    System.out.println(foundById);
                                } else {
                                    System.out.println("No student found with ID " + searchId);
                                }
                                break;
                            case 2:
                                System.out.print("Enter student name: ");
                                String searchName = scanner.nextLine();
                                List<Student> foundByName = db.searchStudentByName(searchName);
                                if (foundByName.isEmpty()) {
                                    System.out.println("No students found with name containing '" + searchName + "'");
                                } else {
                                    foundByName.forEach(System.out::println);
                                }
                                break;
                            case 3:
                                System.out.print("Enter course department: ");
                                String dept = scanner.nextLine().toUpperCase();
                                System.out.print("Enter course number: ");
                                int courseNum = scanner.nextInt();
                                System.out.print("Enter course credits: ");
                                int credits = scanner.nextInt();
                                scanner.nextLine();
                                List<Student> foundByCourse = db.searchStudentsByCourse(new Course(dept, courseNum, credits));
                                if (foundByCourse.isEmpty()) {
                                    System.out.println("No students found taking that course.");
                                } else {
                                    foundByCourse.forEach(System.out::println);
                                }
                                break;
                            default:
                                System.err.println("Invalid search option.");
                        }
                        break;
                    case 3:
                        System.out.print("Enter student name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter year of birth: ");
                        int yearOfBirth = scanner.nextInt();
                        scanner.nextLine();

                        Student newStudent = new Student(name, yearOfBirth);
                        System.out.print("Enter number of courses to add: ");
                        int numCourses = scanner.nextInt();
                        scanner.nextLine();

                        for (int i = 0; i < numCourses; i++) {
                            System.out.print("Enter course department: ");
                            String courseDept = scanner.nextLine();
                            System.out.print("Enter course number: ");
                            int courseNumber = scanner.nextInt();
                            System.out.print("Enter course credits: ");
                            int courseCredits = scanner.nextInt();
                            scanner.nextLine();
                            newStudent.addCourse(new Course(courseDept, courseNumber, courseCredits));
                        }

                        db.addStudent(newStudent);
                        break;
                    case 4:
                        System.out.print("Enter student ID to delete: ");
                        int deleteId = scanner.nextInt();
                        scanner.nextLine();
                        db.deleteStudent(deleteId);
                        break;
                    case 5:
                        System.out.print("Enter student ID to update: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine();

                        Student foundStudent = db.searchStudentById(updateId);
                        if (foundStudent == null) {
                            System.out.println("No student found with ID " + updateId);
                            break;
                        }

                        System.out.println("Current student details: " + foundStudent);
                        System.out.print("Enter new name (leave blank to keep current): ");
                        String newName = scanner.nextLine();
                        System.out.print("Enter new year of birth (0 to keep current): ");
                        int newYearOfBirth = scanner.nextInt();
                        scanner.nextLine();

                        Student updatedStudent = new Student(
                                newName.isBlank() ? foundStudent.getName() : newName,
                                newYearOfBirth == 0 ? foundStudent.getYearOfBirth() : newYearOfBirth
                        );

                        System.out.print("Enter number of courses to add: ");
                        int numNewCourses = scanner.nextInt();
                        scanner.nextLine();
                        for (int i = 0; i < numNewCourses; i++) {
                            System.out.print("Enter course department: ");
                            String newCourseDept = scanner.nextLine();
                            System.out.print("Enter course number: ");
                            int newCourseNumber = scanner.nextInt();
                            System.out.print("Enter course credits: ");
                            int newCourseCredits = scanner.nextInt();
                            scanner.nextLine();
                            updatedStudent.addCourse(new Course(newCourseDept, newCourseNumber, newCourseCredits));
                        }

                        db.updateStudent(updateId, updatedStudent);
                        break;
                    case 6:
                        break;
                    default:
                        System.err.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        } while (choice != 6);

        System.out.print("Save changes to output file? (y/n): ");
        String saveChoice = scanner.nextLine().toLowerCase();
        if (saveChoice.equals("y")) {
            System.out.print("Enter output filename: ");
            String outputFilename = scanner.nextLine();
            try {
                db.exportStudentsToFile(outputFilename);
                System.out.println("Data saved to " + outputFilename);
            } catch (IOException e) {
                System.err.println("Error saving data to file: " + e.getMessage());
            }
        }
    }
}
