package service;

import dao.CoordinatorDao;
import dto.coordinator.CoordinatorDto;
import dto.coordinator.CreateCoordinatorDto;
import entity.Coordinator;
import mapper.CoordinatorToCoordinatorDtoMapper;
import mapper.CreateCoordinatorDtoToCoordinatorMapper;
import mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing coordinators.
 */
public class CoordinatorService {

    private static final Logger logger = LoggerFactory.getLogger(CoordinatorService.class);

    private CoordinatorDao coordinatorDao = new CoordinatorDao();

    private Mapper<CreateCoordinatorDto, Coordinator> createCoodinatorDtoToCoordinatorMapper =
            new CreateCoordinatorDtoToCoordinatorMapper();

    private Mapper<Coordinator, CoordinatorDto> coodinatorToCoordinatorDtoMapper =
            new CoordinatorToCoordinatorDtoMapper();

    /**
     * Saves a new coordinator.
     *
     * @param coordinatorDto the DTO containing the coordinator data
     */
    public void saveCoordinator(CreateCoordinatorDto coordinatorDto){
        logger.info("Saving coordinator {}", coordinatorDto);
        try {
            coordinatorDao.save(createCoodinatorDtoToCoordinatorMapper.map(coordinatorDto));
        }catch(SQLException e){
            logger.info("Error saving coordinator", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all coordinators.
     *
     * @return a list of CoordinatorDto objects representing all coordinators
     */
    public List<CoordinatorDto> getAllCoordinators(){

        logger.info("Fetching all coordinators");

        try{
            return coordinatorDao.getAll().stream()
                    .map(coodinatorToCoordinatorDtoMapper::map)
                    .collect(Collectors.toList());
        }catch(SQLException e){
            logger.error("Error fetching all coordinators");
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a coordinator by their ID.
     *
     * @param coordinatorId the ID of the coordinator
     * @return an Optional containing the CoordinatorDto if found, or empty if not
     */
    public Optional<CoordinatorDto> getCoordinatorById(Integer coordinatorId){
        logger.info("Fetching coordinator with ID {}", coordinatorId);
        try {
            return coordinatorDao.get(coordinatorId).map(coodinatorToCoordinatorDtoMapper::map);
        }catch(SQLException e){
            logger.error("Error fetching coordinator with ID {}", coordinatorId, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates a coordinator.
     *
     * @param coordinatorDto the DTO containing the updated coordinator data
     * @param coordinatorId the ID of the coordinator to update
     */
    public void updateCoordinator(CreateCoordinatorDto coordinatorDto, Integer coordinatorId){
        logger.info("Updating coordinator with ID {}: {}", coordinatorId, coordinatorDto);
        try{
            coordinatorDao.update(createCoodinatorDtoToCoordinatorMapper.map(coordinatorDto), coordinatorId);
        }catch(SQLException e){
            logger.error("Error updating coordinator with ID {}", coordinatorId, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a coordinator by their ID.
     *
     * @param coordinatorId the ID of the coordinator to delete
     */
    public void deleteCoordinator(Integer coordinatorId){
        logger.info("Deleting coordinator with ID: {}", coordinatorId);
        try{
            coordinatorDao.delete(coordinatorId);
        }catch(SQLException e){
            logger.error("Error deleting coordinator with ID: {}", coordinatorId, e);
            throw new RuntimeException(e);
        }
    }

}
