package mapper;

import dto.coordinator.CoordinatorDto;
import dto.student.StudentDto;
import entity.Coordinator;
import entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatorToCoordinatorDtoMapperTest {

    private Mapper<Coordinator, CoordinatorDto> mapper =
            new CoordinatorToCoordinatorDtoMapper();

    @Test
    void map() {

        List<Student> students = new ArrayList<>(List.of(
                new Student(1, "Name1", null, null),
                new Student(2, "Name2", null, null),
                new Student(3, "Name3", null, null)
        ));

        Coordinator coordinator = Coordinator.builder()
                .id(1)
                .name("Name")
                .students(students)
                .build();

        List<StudentDto> studentsDto = new ArrayList<>(List.of(
                new StudentDto(1, "Name1"),
                new StudentDto(2, "Name2"),
                new StudentDto(3, "Name3")
        ));

        CoordinatorDto expectedCoordinatorDto = CoordinatorDto.builder()
                .id(1)
                .name("Name")
                .students(studentsDto)
                .build();

        CoordinatorDto result = mapper.map(coordinator);

        assertEquals(expectedCoordinatorDto, result);

    }
}