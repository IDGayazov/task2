package service;

import dao.CourseDao;
import dto.course.CourseDto;
import dto.course.ReadCourseDto;
import entity.Course;
import mapper.CourseDtoToCourseMapper;
import mapper.CourseToReadCourseDtoMapper;
import mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing courses.
 */
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private CourseDao courseDao = new CourseDao();

    private Mapper<CourseDto, Course> courseDtoToCourseMapper
            = new CourseDtoToCourseMapper();

    private Mapper<Course, ReadCourseDto> courseToReadCourseDtoMapper
            = new CourseToReadCourseDtoMapper();

    /**
     * Saves a new course.
     *
     * @param courseDto the DTO containing the course data
     */
    public void saveCourse(CourseDto courseDto){
        logger.info("Save course {}", courseDto);
        try{
            courseDao.save(courseDtoToCourseMapper.map(courseDto));
        }catch(SQLException e){
            logger.error("Error saving course", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a course by their ID.
     *
     * @param courseId the ID of the course
     * @return an Optional containing the ReadCourseDto if found, or empty if not
     */
    public Optional<ReadCourseDto> getCourseById(Integer courseId){
        logger.info("Fetching course with ID {}", courseId);
        try{
            return courseDao.get(courseId).map(courseToReadCourseDtoMapper::map);
        }catch(SQLException e){
            logger.info("Error fetching course with ID: {}", courseId, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all courses.
     *
     * @return a list of ReadCourseDto objects representing all courses
     */
    public List<ReadCourseDto> getAllCourses(){
        logger.info("Fetching all courses");
        try{
            return courseDao.getAll().stream()
                    .map(courseToReadCourseDtoMapper::map)
                    .collect(Collectors.toList());
        }catch(SQLException e){
            logger.error("Error fetching all courses");
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a course by their ID.
     *
     * @param courseId the ID of the course to delete
     */
    public void deleteCourse(Integer courseId){
        logger.info("Deleting course with ID {}", courseId);
        try {
            courseDao.delete(courseId);
        }catch(SQLException e){
            logger.error("Error deleting course with ID {}", courseId, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates a course.
     *
     * @param courseDto the DTO containing the updated course data
     * @param courseId the ID of the course to update
     */
    public void updateCourse(CourseDto courseDto, Integer courseId){
        logger.info("Updating course with ID {}: {}", courseId, courseDto);
        try {
            courseDao.update(courseDtoToCourseMapper.map(courseDto), courseId);
        }catch(SQLException e){
            logger.error("Error updating course with ID {}", courseId, e);
            throw new RuntimeException(e);
        }
    }
}
