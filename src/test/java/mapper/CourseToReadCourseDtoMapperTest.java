package mapper;

import dto.course.ReadCourseDto;
import dto.student.StudentDto;
import entity.Course;
import entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourseToReadCourseDtoMapperTest {

    private static final Mapper<Course, ReadCourseDto> mapper =
            new CourseToReadCourseDtoMapper();

    @Test
    void map() {

        List<Student> students = new ArrayList<>(List.of(
                new Student(1, "Name1", null, null),
                new Student(2, "Name2", null, null),
                new Student(3, "Name3", null, null)
        ));

        Course course = Course.builder()
                .id(1)
                .name("Subject")
                .students(students)
                .build();

        List<StudentDto> studentDtos = new ArrayList<>(List.of(
                new StudentDto(1, "Name1"),
                new StudentDto(2, "Name2"),
                new StudentDto(3, "Name3")
        ));

        ReadCourseDto expectedValue = ReadCourseDto.builder()
                .id(1)
                .name("Subject")
                .students(studentDtos)
                .build();

        ReadCourseDto result = mapper.map(course);

        assertEquals(expectedValue, result);

    }
}