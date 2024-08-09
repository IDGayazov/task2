package mapper;

import dto.coordinator.CoordinatorDto;
import dto.coordinator.CreateCoordinatorDto;
import entity.Coordinator;
import entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatorToCreateCoordinatorDtoMapperTest {

    private Mapper<Coordinator, CreateCoordinatorDto> mapper =
            new CoordinatorToCreateCoordinatorDtoMapper();

    @Test
    void mapTest() {

        List<Student> studentsList = new ArrayList<>(List.of(
                new Student(1, "Will", null, null),
                new Student(2, "Bill", null, null)
        ));

        Coordinator coordinator = Coordinator.builder()
                .id(1)
                .name("James")
                .students(studentsList)
                .build();

        CreateCoordinatorDto coordinatorDto = CreateCoordinatorDto.builder()
                .id(1)
                .name("James")
                .build();

        CreateCoordinatorDto result = mapper.map(coordinator);

        assertEquals(result, coordinatorDto);

    }
}