package mapper;

import dto.coordinator.CreateCoordinatorDto;
import dto.course.CourseDto;
import dto.student.ReadStudentDto;
import entity.Coordinator;
import entity.Course;
import entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StudentToReadStudentDtoMapperTest {

    private static final Mapper<Student, ReadStudentDto> mapper =
            new StudentToReadStudentDtoMapper();

    @Test
    void map() {

        List<Course> courses = new ArrayList<>(List.of(
                new Course(1, "Name1", null),
                new Course(2, "Name2", null),
                new Course(3, "Name3", null)
        ));

        Student student = Student.builder()
                .id(1)
                .name("Name")
                .coordinator(new Coordinator(1, "CoordName", null))
                .courses(courses)
                .build();

        List<CourseDto> coursesDto = new ArrayList<>(List.of(
                new CourseDto(1, "Name1"),
                new CourseDto(2, "Name2"),
                new CourseDto(3, "Name3")
        ));

        ReadStudentDto expectedValue = ReadStudentDto.builder()
                .id(1)
                .name("Name")
                .coordinator(new CreateCoordinatorDto(1, "CoordName"))
                .courses(coursesDto)
                .build();

        ReadStudentDto result = mapper.map(student);

        assertEquals(expectedValue, result);

    }
}