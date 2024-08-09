package mapper;

import dto.student.StudentDto;
import entity.Coordinator;
import entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StudentToStudentDtoMapperTest {

    private Mapper<Student, StudentDto> mapper =
            new StudentToStudentDtoMapper();
    @Test
    void mapTest() {

        Student student = Student.builder()
                .id(1)
                .name("James")
                .coordinator(new Coordinator(1, "Pit", null))
                .courses(new ArrayList<>())
                .build();

        StudentDto studentDto = StudentDto.builder()
                .id(1)
                .name("James")
                .build();

        StudentDto result = mapper.map(student);

        assertEquals(studentDto, result);
    }
}