package dao;

import entity.Coordinator;
import entity.Course;
import entity.Student;
import util.DBManager;

import java.sql.*;
import java.util.*;

/**
 * DAO implementation for managing Student entities.
 */
public class StudentDao implements Dao<Student, Integer>{

    private static final String INSERT_STUDENT = "INSERT INTO student(sname, coordinator_id) VALUES (?, ?)";

    private static final String INSERT_STUD_COURSE = "INSERT INTO stud_course(stud_id, course_id) VALUES (?, ?)";

    private static final String SELECT_STUDENTS =
            "SELECT student.id AS id, student.sname AS name, " +
                    "coordinator.id AS coordId, cord_name, " +
                    "course.id AS courseId, course.cname AS course_name " +
                    "FROM student " +
                    "LEFT JOIN coordinator ON coordinator.id = student.coordinator_id " +
                    "LEFT JOIN stud_course ON stud_course.stud_id = student.id " +
                    "LEFT JOIN course ON course.id = stud_course.course_id";

    private static final String SELECT_STUDENT_BY_ID =
            SELECT_STUDENTS + " WHERE student.id = ?";


    private static final String UPDATE_STUDENT_BY_ID = "UPDATE student SET sname = ?, coordinator_id = ? " +
                                                        "WHERE id = ?";

    private static final String DELETE_STUDENT_BY_ID = "DELETE FROM student WHERE id = ?";

    private final DBManager dbManager;

    public StudentDao(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public StudentDao() {
        this.dbManager = new DBManager();
    }

    /**
     * Saves a new student to the database.
     *
     * @param item the student to save
     */
    @Override
    public void save(Student item) throws SQLException {

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement insertStudentStatement =
                    connection.prepareStatement(INSERT_STUDENT, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement insertCourseToStudent =
                    connection.prepareStatement(INSERT_STUD_COURSE)){

            Integer coordinatorId = null;
            if(item.getCoordinator() != null){
                coordinatorId = item.getCoordinator().getId();
            }

            insertStudentStatement.setString(1, item.getName());
            insertStudentStatement.setObject(2, coordinatorId);

            insertStudentStatement.executeUpdate();

            ResultSet genKeys = insertStudentStatement.getGeneratedKeys();

            Integer studId = null;
            if(genKeys.next()){
                studId =genKeys.getObject("id", Integer.class);
                item.setId(studId);
            }

            List<Course> courses = item.getCourses();
            if(courses != null && !courses.isEmpty()){
                Set<Integer> courseIds = new HashSet<>();
                for(Course course: courses){

                    if(courseIds.contains(course.getId())){
                        continue;
                    }

                    insertCourseToStudent.setObject(1, studId);
                    insertCourseToStudent.setObject(2, course.getId());
                    insertCourseToStudent.executeUpdate();

                    courseIds.add(course.getId());
                }
            }

        }
    }

    /**
     * Updates an existing student in the database.
     *
     * @param item the student to update
     * @param studentId the ID of the student to update
     */
    @Override
    public void update(Student item, Integer studentId) throws SQLException {
        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENT_BY_ID);
            PreparedStatement insertCourseToStudent = connection.prepareStatement(INSERT_STUD_COURSE)){

            Integer coordinatorId = null;

            if(item.getCoordinator() != null){
                coordinatorId = item.getCoordinator().getId();
            }

            List<Course> courses = item.getCourses();

            if(courses != null && !courses.isEmpty()){

                Set<Integer> courseIds = new HashSet<>();

                for(Course course: courses){

                    if(courseIds.contains(course.getId())){
                        continue;
                    }

                    insertCourseToStudent.setObject(1, studentId);
                    insertCourseToStudent.setObject(2, course.getId());
                    insertCourseToStudent.executeUpdate();

                    courseIds.add(course.getId());
                }
            }

            insertCourseToStudent.setString(1, item.getName());
            insertCourseToStudent.setObject(2, coordinatorId);

            statement.setString(1, item.getName());
            statement.setObject(2, coordinatorId);
            statement.setObject(3, studentId);
            
            statement.executeUpdate();

        }
    }

    /**
     * Retrieves a student from the database by ID.
     *
     * @param id the ID of the student to retrieve
     * @return an Optional containing the found student, or empty if not found
     */
    @Override
    public Optional<Student> get(Integer id) throws SQLException {

        Student student = null;

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(SELECT_STUDENT_BY_ID)
        ){
            statement.setObject(1, id);

            ResultSet resultSet = statement.executeQuery();

            Map<Integer, Student> idToStudentMap = new HashMap<>();

            while(resultSet.next()){

                Integer studId = resultSet.getInt("id");

                if(idToStudentMap.containsKey(studId)){
                    idToStudentMap.get(studId).getCourses().add(Course.builder()
                                    .id(resultSet.getInt("courseId"))
                                    .name(resultSet.getString("course_name"))
                            .build());
                }else {
                    student = buildStudent(resultSet);
                    idToStudentMap.put(student.getId(), student);
                }
            }

        }
        return Optional.ofNullable(student);
    }

    /**
     * Retrieves all students from the database.
     *
     * @return a list of all students
     */
    @Override
    public List<Student> getAll() throws SQLException {

        List<Student> students = new ArrayList<>();

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(SELECT_STUDENTS)){

            ResultSet resultSet = statement.executeQuery();

            Map<Integer, Student> idToStudentMap = new HashMap<>();

            while(resultSet.next()){

                Integer studId = resultSet.getInt("id");

                if(idToStudentMap.containsKey(studId)){
                    idToStudentMap.get(studId).getCourses().add(Course.builder()
                            .id(resultSet.getInt("courseId"))
                            .name(resultSet.getString("course_name"))
                            .build());
                }else {
                    Student student = buildStudent(resultSet);
                    students.add(student);
                    idToStudentMap.put(student.getId(), student);
                }
            }

        }
        return students;
    }

    /**
     * Deletes a student from the database by ID.
     *
     * @param id the ID of the student to delete
     */
    @Override
    public void delete(Integer id) throws SQLException {

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT_BY_ID)){

            preparedStatement.setObject(1, id);
            preparedStatement.executeUpdate();

        }

    }

    /**
     * Builds a Student object from a ResultSet.
     *
     * @param resultSet the ResultSet to build the student from
     * @return the built Student object
     * @throws SQLException if an SQL error occurs
     */
    private Student buildStudent(ResultSet resultSet) throws SQLException {
        Coordinator coordinator = null;

        if(resultSet.getObject("coordId", Integer.class) != null){
            coordinator = buildCoordinator(resultSet);
        }

        Course course = null;

        if(resultSet.getObject("courseId", Integer.class) != null){
            course = buildCourse(resultSet);
        }

        return Student.builder()
                .id(resultSet.getObject("id", Integer.class))
                .name(resultSet.getString("name"))
                .coordinator(coordinator)
                .courses(course != null ? new ArrayList<>(Collections.singletonList(course)) : null)
                .build();
    }

    /**
     * Builds a Course object from a ResultSet.
     *
     * @param resultSet the ResultSet to build the course from
     * @return the built Course object
     * @throws SQLException if an SQL error occurs
     */
    private static Course buildCourse(ResultSet resultSet) throws SQLException {
        Course course;
        course = Course.builder()
                .id(resultSet.getObject("courseId", Integer.class))
                .name(resultSet.getObject("course_name", String.class))
                .build();
        return course;
    }

    /**
     * Builds a Coordinator object from a ResultSet.
     *
     * @param resultSet the ResultSet to build the coordinator from
     * @return the built Coordinator object
     * @throws SQLException if an SQL error occurs
     */
    private static Coordinator buildCoordinator(ResultSet resultSet) throws SQLException {
        Coordinator coordinator;
        coordinator = Coordinator.builder()
                .id(resultSet.getObject("coordId", Integer.class))
                .name(resultSet.getString("cord_name"))
                .build();
        return coordinator;
    }

}
