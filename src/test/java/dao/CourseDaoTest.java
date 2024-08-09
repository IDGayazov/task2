package dao;

import entity.Course;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import util.DBManager;
import util.PropertiesLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseDaoTest {

    static PropertiesLoader propertiesLoader = new PropertiesLoader("application.properties");

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:10-alpine")
            .withDatabaseName(propertiesLoader.getProperty("database.name"))
            .withUsername(propertiesLoader.getProperty("database.username"))
            .withPassword(propertiesLoader.getProperty("database.password"));

    private DBManager connectionProvider;

    private CourseDao courseDao;

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
        courseDao = new CourseDao(connectionProvider);

        // clean table before each test
        try(Connection connection = connectionProvider.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM course"
            )){
            preparedStatement.executeUpdate();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Test
    @Order(1)
    void save() {

        Integer courseId = 1;
        String courseName = "Subject";

        Course course = Course.builder()
                .id(courseId)
                .name(courseName)
                .build();

        // save course
        try {
            courseDao.save(course);
        }catch(SQLException e){
            fail();
        }

        Course result = new Course();

        // get course from table
        try(Connection connection = connectionProvider.getConnection();
            PreparedStatement getCourseByIdStatement = connection.prepareStatement(
                    "SELECT id, cname FROM course WHERE id = ?"
            )){

            getCourseByIdStatement.setObject(1, course.getId());
            ResultSet resultSet = getCourseByIdStatement.executeQuery();

            if(resultSet.next()){
                result.setId(resultSet.getObject("id", Integer.class));
                result.setName(resultSet.getString("cname"));
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        assertEquals(course, result);
    }

    @Test
    @Order(2)
    void update() {

        Integer courseId = 1;
        String courseName = "Subject";

        Course course = Course.builder()
                .id(courseId)
                .name(courseName)
                .build();

        // save course
        try {
            courseDao.save(course);
        }catch(SQLException e) {
            fail();
        }

        Course newCourse = Course.builder()
                .id(1)
                .name("Subject2")
                .build();

        // update course
        try {
            courseDao.update(newCourse, course.getId());
        }catch(SQLException e) {
            fail();
        }

        Course result = new Course();

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement getCoordinatorByIdStatement = connection.prepareStatement(
                     "SELECT id, cname FROM course WHERE id = ?"
             )){

            getCoordinatorByIdStatement.setObject(1, course.getId());
            ResultSet resultSet = getCoordinatorByIdStatement.executeQuery();

            if(resultSet.next()){
                result.setId(resultSet.getObject("id", Integer.class));
                result.setName(resultSet.getString("cname"));
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }

        assertEquals(newCourse.getName(), result.getName());

    }

    @Test
    @Order(3)
    void get() {

        Integer id = 1;
        String name = "Subject";

        Course course = new Course(id, name, null);

        try {
            courseDao.save(course);
        }catch(SQLException e) {
            fail();
        }

        Optional<Course> result = Optional.empty();
        try {
            result = courseDao.get(course.getId());
        }catch(SQLException e){
            fail();
        }

        if(result.isPresent()){
            assertEquals(result.get().getName(), course.getName());
        }else{
            fail();
        }
    }

    @Test
    @Order(4)
    void getAll() {

        List<Course> courses = new ArrayList<>(List.of(
                new Course(1, "Subject1", null),
                new Course(2, "Subject2", null),
                new Course(3, "Subject3", null)
        ));

        for(Course course: courses){

            try{
                courseDao.save(course);
            }catch(SQLException e){
                fail();
            }
        }

        List<Course> result = new ArrayList<>();
        try{
            result = courseDao.getAll();
        }catch(SQLException e){
            fail();
        }

        courses.sort(Comparator.comparing(Course::getId));
        result.sort(Comparator.comparing(Course::getId));

        assertEquals(courses, result);
    }

    @Test
    @Order(5)
    void delete() {

        Integer id = 1;
        String name = "Subject";

        Course course = new Course(id, name, null);

        // save course
        try {
            courseDao.save(course);
        }catch(SQLException e){
            fail();
        }

        // delete course
        try {
            courseDao.delete(id);
        } catch (SQLException e) {
            fail();
        }

        // check if course was deleted
        Optional<Course> result = Optional.empty();
        try {
            result = courseDao.get(id);
        }catch(SQLException e){
            fail();
        }

        if(result.isPresent()){
            fail();
        }else{
            assertTrue(true);
        }
    }
}
