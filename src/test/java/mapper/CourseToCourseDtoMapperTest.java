package mapper;

import dto.course.CourseDto;
import entity.Course;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseToCourseDtoMapperTest {

    private Mapper<Course, CourseDto> mapper =
            new CourseToCourseDtoMapper();

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

        CourseDto result = mapper.map(course);

        assertEquals(result, courseDto);
    }
}