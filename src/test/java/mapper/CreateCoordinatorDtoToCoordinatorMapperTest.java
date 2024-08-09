package mapper;

import dto.coordinator.CreateCoordinatorDto;
import entity.Coordinator;
import entity.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateCoordinatorDtoToCoordinatorMapperTest {

    private Mapper<CreateCoordinatorDto, Coordinator> mapper =
            new CreateCoordinatorDtoToCoordinatorMapper();

    @Test
    void mapTest() {

        Coordinator coordinator = Coordinator.builder()
                .id(1)
                .name("James")
                .build();

        CreateCoordinatorDto coordinatorDto = CreateCoordinatorDto.builder()
                .id(1)
                .name("James")
                .build();

        Coordinator result = mapper.map(coordinatorDto);

        assertEquals(coordinator, result);

    }
}