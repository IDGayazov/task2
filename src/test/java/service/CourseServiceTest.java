package service;

import dao.CourseDao;
import dto.course.CourseDto;
import dto.course.ReadCourseDto;
import entity.Course;
import mapper.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CourseServiceTest {

    @Mock
    private CourseDao courseDao;

    @Mock
    private Mapper<CourseDto, Course> courseDtoToCourseMapper;

    @Mock
    private Mapper<Course, ReadCourseDto> courseToReadCourseDtoMapper;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveCourse() throws SQLException {

        Course course = Course.builder()
                .name("Math")
                .build();

        CourseDto courseDto = CourseDto.builder()
                .name("Math")
                .build();

        when(courseDtoToCourseMapper.map(courseDto)).thenReturn(course);

        courseService.saveCourse(courseDto);

        verify(courseDao).save(course);

    }

    @Test
    public void testUpdateCourse() throws SQLException {

        Course course = Course.builder()
                .name("Math")
                .build();

        CourseDto courseDto = CourseDto.builder()
                .name("Math")
                .build();

        Integer courseId = 1;

        when(courseDtoToCourseMapper.map(courseDto)).thenReturn(course);

        courseService.updateCourse(courseDto, courseId);

        verify(courseDao).update(course, courseId);

    }

    @Test
    public void testGetAllCourses() throws SQLException {

        Course course = Course.builder()
                .name("Math")
                .build();

        ReadCourseDto readCourseDto = ReadCourseDto.builder()
                .name("Math")
                .build();

        List<Course> courses = new ArrayList<>(List.of(course));
        List<ReadCourseDto> readCourseDtos = new ArrayList<>(List.of(readCourseDto));

        when(courseToReadCourseDtoMapper.map(course)).thenReturn(readCourseDto);

        when(courseDao.getAll()).thenReturn(courses);

        List<ReadCourseDto> result = courseService.getAllCourses();

        assertEquals(result, readCourseDtos);
    }

    @Test
    public void testGetCourseById() throws SQLException {

        Course course = Course.builder()
                .name("Math")
                .build();

        ReadCourseDto readCourseDto = ReadCourseDto.builder()
                .name("Math")
                .build();

        Integer courseId = 1;

        when(courseToReadCourseDtoMapper.map(course)).thenReturn(readCourseDto);
        when(courseDao.get(courseId)).thenReturn(Optional.of(course));

        Optional<ReadCourseDto> result = courseService.getCourseById(courseId);

        assertTrue(result.isPresent());
        assertEquals(result.get(), readCourseDto);

    }

    @Test
    public void testDeleteCourse() throws SQLException {

        Integer courseId = 1;

        courseService.deleteCourse(courseId);

        verify(courseDao).delete(courseId);
    }

}
