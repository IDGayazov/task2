package service;

import dao.StudentDao;
import dto.course.CourseDto;
import dto.student.CreateStudentDto;
import dto.student.ReadStudentDto;
import entity.Course;
import entity.Student;
import mapper.CourseToCourseDtoMapper;
import mapper.CreateStudentDtoToStudentMapper;
import mapper.Mapper;
import mapper.StudentToReadStudentDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing students.
 */
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private StudentDao studentDao = new StudentDao();

    private Mapper<CreateStudentDto, Student> createStudentDtoToStudentMapper =
            new CreateStudentDtoToStudentMapper();

    private Mapper<Student, ReadStudentDto> studentToReadStudentDtoMapper =
            new StudentToReadStudentDtoMapper();

    private Mapper<Course, CourseDto> courseToCourseDtoMapper =
            new CourseToCourseDtoMapper();

    /**
     * Saves a new student.
     *
     * @param studentDto the DTO containing the student data
     */
    public void saveStudent(CreateStudentDto studentDto){
        logger.info("Save student {}", studentDto);
        try{
            studentDao.save(createStudentDtoToStudentMapper.map(studentDto));
        }catch(SQLException e){
            logger.error("Error saving student", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all students.
     *
     * @return a list of ReadStudentDto objects representing all students
     */
    public List<ReadStudentDto> getAllStudents(){
        logger.info("Fetching all students");
        try{
            return studentDao.getAll().stream()
                    .map(studentToReadStudentDtoMapper::map)
                    .collect(Collectors.toList());
        }catch(SQLException e){
            logger.error("Error fetching all students");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a student by their ID.
     *
     * @param id the ID of the student
     * @return an Optional containing the ReadStudentDto if found, or empty if not
     */
    public Optional<ReadStudentDto> getStudentById(Integer id){
        logger.info("Fetching student with ID {}", id);
        try {
            return studentDao.get(id).map(studentToReadStudentDtoMapper::map);
        }catch(SQLException e){
            logger.info("Error fetching student with ID: {}", id, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates a student.
     *
     * @param item the DTO containing the updated student data
     * @param id the ID of the student to update
     */
    public void updateStudent(CreateStudentDto item, Integer id){

        logger.info("Updating student with ID {}: {}", id, item);

        try{
            if(item.getCourses() != null){

                Optional<Student> student = studentDao.get(id);

                student.ifPresent((stud) -> {

                    Set<Integer> newCoursesId = item.getCourses().stream()
                            .map(CourseDto::getId)
                            .collect(Collectors.toSet());

                    Set<Integer> coursesId = new HashSet<>();
                    List<CourseDto> courses = new ArrayList<>();
                    if(stud.getCourses() != null) {
                        coursesId = stud.getCourses().stream()
                                .map(Course::getId)
                                .collect(Collectors.toSet());

                        courses = stud.getCourses().stream()
                                .map(courseToCourseDtoMapper::map)
                                .collect(Collectors.toList());

                    }

                    newCoursesId.removeAll(coursesId);

                    courses.addAll(item.getCourses());

                    item.getCourses().removeIf(courseDto -> !newCoursesId.contains(courseDto.getId()));

                });
            }
            studentDao.update(createStudentDtoToStudentMapper.map(item), id);
        }catch(SQLException e){
            logger.error("Error updating student with ID {}", id, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a student by their ID.
     *
     * @param id the ID of the student to delete
     */
    public void deleteStudent(Integer id){
        logger.info("Deleting student with ID: {}", id);
        try{
            studentDao.delete(id);
        }catch(SQLException e){
            logger.error("Error deleting student with ID: {}", id, e);
            throw new RuntimeException(e);
        }
    }
}
