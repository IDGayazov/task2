package dao;

import entity.Course;
import entity.Student;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import util.DBManager;
import util.PropertiesLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentDaoTest {

    static PropertiesLoader propertiesLoader = new PropertiesLoader("application.properties");

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:10-alpine")
            .withDatabaseName(propertiesLoader.getProperty("database.name"))
            .withUsername(propertiesLoader.getProperty("database.username"))
            .withPassword(propertiesLoader.getProperty("database.password"));

    private DBManager connectionProvider;

    private StudentDao studentDao;

    @BeforeAll
    static void startContainer() {
        postgres.start();

        Flyway flyway = Flyway.configure()
                .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }

    @AfterAll
    static void stopContainer() {
        postgres.stop();
    }

    @BeforeEach
    void init() {
        connectionProvider = new DBManager(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        studentDao = new StudentDao(connectionProvider);

        // clean table before each test
        cleanTable("DELETE FROM stud_course");
        cleanTable("DELETE FROM student");
        cleanTable("DELETE FROM course");

    }



    @Test
    @Order(1)
    void save() {

        Integer studId = 1;
        String studName = "Name";

        Student student = Student.builder()
                .id(studId)
                .name(studName)
                .build();

        // save student
        try {
            studentDao.save(student);
        }catch(SQLException e){
            fail();
        }

        Student result = new Student();

        // get student from table
        try(Connection connection = connectionProvider.getConnection();
            PreparedStatement getStudentByIdStatement = connection.prepareStatement(
                    "SELECT id, sname FROM student WHERE id = ?"
            )){

            getStudentByIdStatement.setObject(1, student.getId());
            ResultSet resultSet = getStudentByIdStatement.executeQuery();

            if(resultSet.next()){
                result.setId(resultSet.getObject("id", Integer.class));
                result.setName(resultSet.getString("sname"));
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        assertEquals(student, result);
    }

    @Test
    @Order(2)
    void saveStudentWithCoursesList() {

        CourseDao courseDao = new CourseDao(connectionProvider);

        Integer studId = 1;
        String studName = "Name";
        List<Course> courses = new ArrayList<>(List.of(
                new Course(1, "Subject1", null),
                new Course(2, "Subject2", null),
                new Course(3, "Subject3", null)
        ));

        // save all courses in course table
        for(Course course: courses){
            try {
                courseDao.save(course);
            } catch (SQLException e) {
                fail();
            }
        }

        Student student = Student.builder()
                .id(studId)
                .name(studName)
                .courses(courses)
                .build();

        // save student
        try {
            studentDao.save(student);
        }catch(SQLException e){
            fail();
        }

        List<StudCourse> expectedStudCourses = new ArrayList<>();

        for(Course course: courses){
            expectedStudCourses.add(new StudCourse(
                    student.getId(),
                    course.getId()
            ));
        }

        List<StudCourse> result = new ArrayList<>();

        // get student/course relation from table stud_course
        try(Connection connection = connectionProvider.getConnection();
            PreparedStatement getStudentCoursesStatement = connection.prepareStatement(
                    "SELECT stud_id, course_id FROM stud_course"
            )){

            ResultSet resultSet = getStudentCoursesStatement.executeQuery();

            while(resultSet.next()){
                StudCourse studCourse = new StudCourse(
                        resultSet.getObject("stud_id", Integer.class),
                        resultSet.getObject("course_id", Integer.class)
                );
                result.add(studCourse);
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        result.sort(Comparator.comparing(StudCourse::getCourse_id));
        expectedStudCourses.sort(Comparator.comparing(StudCourse::getCourse_id));

        assertEquals(expectedStudCourses, result);
    }

    @Test
    @Order(3)
    void update() {

        Integer studId = 1;
        String studName = "Name";

        Student student = Student.builder()
                .id(studId)
                .name(studName)
                .build();

        // save student
        try {
            studentDao.save(student);
        }catch(SQLException e) {
            fail();
        }

        Student newStudent = Student.builder()
                .id(1)
                .name("Name2")
                .build();

        // update student
        try {
            studentDao.update(newStudent, student.getId());
        }catch(SQLException e) {
            fail();
        }

        Student result = new Student();

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement getStudentByIdStatement = connection.prepareStatement(
                     "SELECT id, sname FROM student WHERE id = ?"
             )){

            getStudentByIdStatement.setObject(1, student.getId());
            ResultSet resultSet = getStudentByIdStatement.executeQuery();

            if(resultSet.next()){
                result.setId(resultSet.getObject("id", Integer.class));
                result.setName(resultSet.getString("sname"));
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        assertEquals(newStudent.getName(), result.getName());

    }

    @Test
    @Order(4)
    void get() {

        Integer id = 1;
        String name = "Name";

        Student student = new Student(id, name, null, null);

        try {
            studentDao.save(student);
        }catch(SQLException e) {
            fail();
        }

        Optional<Student> result = Optional.empty();
        try {
            result = studentDao.get(student.getId());
        }catch(SQLException e){
            fail();
        }

        if(result.isPresent()){
            assertEquals(result.get().getName(), student.getName());
        }else{
            fail();
        }
    }

    @Test
    @Order(5)
    void getAll() {

        List<Student> students = new ArrayList<>(List.of(
                new Student(1, "Name1", null, null),
                new Student(2, "Name2", null, null),
                new Student(3, "Name3", null, null)
        ));

        for(Student student: students){

            try{
                studentDao.save(student);
            }catch(SQLException e){
                fail();
            }
        }

        List<Student> result = new ArrayList<>();
        try{
            result = studentDao.getAll();
        }catch(SQLException e){
            fail();
        }

        students.sort(Comparator.comparing(Student::getId));
        result.sort(Comparator.comparing(Student::getId));

        assertEquals(students, result);
    }

    @Test
    @Order(6)
    void getAllWithCourses() {

        CourseDao courseDao = new CourseDao(connectionProvider);

        List<Course> courses1 = new ArrayList<>(Collections.singletonList(
                new Course(1, "Subject1", null)));
        List<Course> courses2 = new ArrayList<>(Collections.singletonList(
                new Course(2, "Subject2", null)));
        List<Course> courses3 = new ArrayList<>(Collections.singletonList(
                new Course(3, "Subject3", null)));

        // save all courses in course table
        for(Course course: courses1){
            try {
                courseDao.save(course);
            } catch (SQLException e) {
                fail();
            }
        }

        for(Course course: courses2){
            try {
                courseDao.save(course);
            } catch (SQLException e) {
                fail();
            }
        }

        for(Course course: courses3){
            try {
                courseDao.save(course);
            } catch (SQLException e) {
                fail();
            }
        }

        List<Student> students = new ArrayList<>(List.of(
                new Student(1, "Name1", null, courses1),
                new Student(2, "Name2", null, courses2),
                new Student(3, "Name3", null, courses3)
        ));

        for(Student student: students){
            try{
                studentDao.save(student);
            }catch(SQLException e){
                fail();
            }
        }

        List<Student> result = new ArrayList<>();
        try{
            result = studentDao.getAll();
        }catch(SQLException e){
            fail();
        }

        students.sort(Comparator.comparing(Student::getId));
        result.sort(Comparator.comparing(Student::getId));

        assertEquals(students, result);
    }


    @Test
    @Order(7)
    void delete() {

        Integer id = 1;
        String name = "Name";

        Student student = new Student(id, name, null, null);

        // save student
        try {
            studentDao.save(student);
        }catch(SQLException e){
            fail();
        }

        // delete student
        try {
            studentDao.delete(id);
        } catch (SQLException e) {
            fail();
        }

        // check if student was deleted
        Optional<Student> result = Optional.empty();
        try {
            result = studentDao.get(id);
        }catch(SQLException e){
            fail();
        }

        if(result.isPresent()){
            fail();
        }else{
            assertTrue(true);
        }
    }

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    private class StudCourse{
        public final int stud_id;
        public final int course_id;
    }

    private void cleanTable(String DELETE_QUERY) {
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     DELETE_QUERY
             )) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}