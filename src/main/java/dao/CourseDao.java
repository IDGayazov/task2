package dao;

import entity.Course;
import entity.Student;
import util.DBManager;

import java.sql.*;
import java.util.*;

/**
 * DAO implementation for managing Course entities.
 */
public class CourseDao implements Dao<Course, Integer>{

    private static final String INSERT_COURSE = "INSERT INTO course(cname) VALUES(?)";

    private static final String UPDATE_COURSE_BY_ID = "UPDATE course SET cname = ? WHERE id = ?";

    private static final String DELETE_COURSE_BY_ID = "DELETE FROM course WHERE id = ?";

    private static final String SELECT_ALL_COURSES =
            "SELECT course.id AS id, cname, student.id AS studId, sname " +
                    "FROM course " +
                    "LEFT JOIN stud_course ON course.id = stud_course.course_id " +
                    "LEFT JOIN student ON student.id = stud_course.stud_id";

    private static final String SELECT_COURSE_BY_ID = SELECT_ALL_COURSES + " WHERE course.id = ?";

    private final DBManager dbManager;

    public CourseDao(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public CourseDao() {
        this.dbManager = new DBManager();
    }

    /**
     * Saves a new course to the database.
     *
     * @param item the course to save
     */
    @Override
    public void save(Course item) throws SQLException {
        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement saveCourseStatement =
                    connection.prepareStatement(INSERT_COURSE, Statement.RETURN_GENERATED_KEYS)){

            saveCourseStatement.setString(1, item.getName());

            saveCourseStatement.executeUpdate();

            ResultSet resultSet = saveCourseStatement.getGeneratedKeys();

            if(resultSet.next()){
                item.setId(resultSet.getObject("id", Integer.class));
            }
        }
    }

    /**
     * Updates an existing course in the database.
     *
     * @param item the course to update
     * @param id the ID of the course to update
     */
    @Override
    public void update(Course item, Integer id) throws SQLException {
        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement updateCourseStatement = connection.prepareStatement(UPDATE_COURSE_BY_ID)){

            updateCourseStatement.setString(1, item.getName());
            updateCourseStatement.setInt(2, id);

            updateCourseStatement.executeUpdate();

        }
    }

    /**
     * Retrieves a course from the database by ID.
     *
     * @param id the ID of the course to retrieve
     * @return an Optional containing the found student, or empty if not found
     */
    @Override
    public Optional<Course> get(Integer id) throws SQLException {

        Course course = null;

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement getCourseByIdStatement = connection.prepareStatement(SELECT_COURSE_BY_ID)){

            getCourseByIdStatement.setObject(1, id);

            ResultSet resultSet = getCourseByIdStatement.executeQuery();

            while(resultSet.next()){

                Integer studId = resultSet.getObject("studId", Integer.class);

                if(course != null && studId != null){
                    course.getStudents().add(Student.builder()
                                    .id(resultSet.getObject("studId", Integer.class))
                                    .name(resultSet.getString("sname"))
                            .build());
                }else{
                    course = buildCourse(resultSet);
                }

            }

        }

        return Optional.ofNullable(course);
    }

    /**
     * Retrieves all courses from the database.
     *
     * @return a list of all courses
     */
    @Override
    public List<Course> getAll() throws SQLException {

        List<Course> courses = new ArrayList<>();

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement getCourses = connection.prepareStatement(SELECT_ALL_COURSES)){

            ResultSet resultSet = getCourses.executeQuery();

            Map<Integer, Course> courseIdToCourseMap = new HashMap<>();

            while(resultSet.next()) {

                Integer courseId = resultSet.getObject("id", Integer.class);

                if(!courseIdToCourseMap.containsKey(courseId)){

                    Course course = buildCourse(resultSet);
                    courseIdToCourseMap.put(courseId, course);
                    courses.add(course);

                }else{

                    Integer studId = resultSet.getObject("studId", Integer.class);

                    if(studId != null){

                        courseIdToCourseMap.get(courseId).getStudents().add(Student.builder()
                                        .id(studId)
                                        .name(resultSet.getString("sname"))
                                .build());
                    }
                }
            }

        }
        return courses;
    }

    /**
     * Deletes a course from the database by ID.
     *
     * @param courseId the ID of the course to delete
     */
    @Override
    public void delete(Integer courseId) throws SQLException {

        try(Connection connection = this.dbManager.getConnection();
            PreparedStatement deleteCourseStatement = connection.prepareStatement(DELETE_COURSE_BY_ID)){

            deleteCourseStatement.setInt(1, courseId);

            deleteCourseStatement.executeUpdate();

        }
    }

    /**
     * Builds a Course object from a ResultSet.
     *
     * @param resultSet the ResultSet to build the student from
     * @return the built Course object
     * @throws SQLException if an SQL error occurs
     */
    public static Course buildCourse(ResultSet resultSet) throws SQLException {

        Integer studentId = resultSet.getObject("studId", Integer.class);
        Student student = null;
        if(studentId != null){
            student = Student.builder()
                    .id(studentId)
                    .name(resultSet.getString("sname"))
                    .build();
        }

        return Course.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("cname"))
                .students(student != null ? new ArrayList<>(Collections.singletonList(student)) : null)
                .build();
    }
}
