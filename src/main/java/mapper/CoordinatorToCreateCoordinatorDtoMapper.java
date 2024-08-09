package mapper;

import dto.coordinator.CreateCoordinatorDto;
import entity.Coordinator;

/**
 * Mapper implementation for converting a Coordinator entity to a CreateCoordinatorDto.
 */
public class CoordinatorToCreateCoordinatorDtoMapper implements Mapper<Coordinator, CreateCoordinatorDto>{

    @Override
    public CreateCoordinatorDto map(Coordinator coordinator) {
        return CreateCoordinatorDto.builder()
                .id(coordinator.getId())
                .name(coordinator.getName())
                .build();
    }
}
