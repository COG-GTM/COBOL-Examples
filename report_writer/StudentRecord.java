public class StudentRecord {
    private final int studentId;
    private final String studentName;
    private final String major;
    private final int numCourses;

    public StudentRecord(int studentId, String studentName, String major, int numCourses) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.major = major;
        this.numCourses = numCourses;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getMajor() {
        return major;
    }

    public int getNumCourses() {
        return numCourses;
    }

    public static StudentRecord parseFromLine(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }
        
        int studentId = Integer.parseInt(line.substring(0, 6));
        String studentName = line.substring(6, 26);
        String major = line.substring(26, 29);
        int numCourses = Integer.parseInt(line.substring(29, 31));
        
        return new StudentRecord(studentId, studentName, major, numCourses);
    }
}
