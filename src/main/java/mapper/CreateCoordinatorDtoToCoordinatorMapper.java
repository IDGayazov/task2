package mapper;

import dto.coordinator.CreateCoordinatorDto;
import entity.Coordinator;

/**
 * Mapper implementation for converting a CreateCoordinatorDto to a Coordinator.
 */
public class CreateCoordinatorDtoToCoordinatorMapper implements Mapper<CreateCoordinatorDto, Coordinator>{

    @Override
    public Coordinator map(CreateCoordinatorDto coordinatorDto) {
        return Coordinator.builder()
                .id(coordinatorDto.getId())
                .name(coordinatorDto.getName())
                .build();
    }
}
