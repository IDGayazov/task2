package mapper;

import dto.course.CourseDto;
import dto.student.CreateStudentDto;
import entity.Coordinator;
import entity.Course;
import entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateStudentDtoToStudentMapperTest {

    private static final Mapper<CreateStudentDto, Student> mapper =
            new CreateStudentDtoToStudentMapper();

    @Test
    void map() {

        List<Course> courses = new ArrayList<>(List.of(
                new Course(1, "Subject1", null),
                new Course(2, "Subject2", null),
                new Course(3, "Subject3", null)
        ));

        List<CourseDto> courseDto = new ArrayList<>(List.of(
                new CourseDto(1, "Subject1"),
                new CourseDto(2, "Subject2"),
                new CourseDto(3, "Subject3")
        ));

        CreateStudentDto createStudentDto = CreateStudentDto.builder()
                .id(1)
                .name("Name")
                .coordinatorId(1)
                .courses(courseDto)
                .build();

        Student expectedValue = Student.builder()
                .id(1)
                .name("Name")
                .coordinator(new Coordinator(1, null, null))
                .courses(courses)
                .build();

        Student result = mapper.map(createStudentDto);

        assertEquals(expectedValue, result);

    }
}