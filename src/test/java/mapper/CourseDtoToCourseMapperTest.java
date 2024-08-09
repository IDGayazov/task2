package mapper;

import dto.course.CourseDto;
import entity.Course;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseDtoToCourseMapperTest {

    private Mapper<CourseDto, Course> mapper = new CourseDtoToCourseMapper();

    @Test
    void mapTest() {

        CourseDto courseDto = CourseDto.builder()
                .id(1)
                .name("Math")
                .build();

        Course course = Course.builder()
                .id(1)
                .name("Math")
                .build();

        Course result = mapper.map(courseDto);

        assertEquals(result, course);

    }
}