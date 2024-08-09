package service;

import dao.CoordinatorDao;
import dto.coordinator.CoordinatorDto;
import dto.coordinator.CreateCoordinatorDto;
import entity.Coordinator;
import mapper.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoordinatorServiceTest {

    @Mock
    private CoordinatorDao coordinatorDao;

    @Mock
    private Mapper<CreateCoordinatorDto, Coordinator> createCoodinatorDtoToCoordinatorMapper;

    @Mock
    private Mapper<Coordinator, CoordinatorDto> coodinatorToCoordinatorDtoMapper;

    @InjectMocks
    private CoordinatorService coordinatorService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveStudent() throws SQLException {

        CreateCoordinatorDto createCoordinatorDto = CreateCoordinatorDto.builder()
                .name("James")
                .build();

        Coordinator coordinator = Coordinator.builder()
                .name("James")
                .build();

        when(createCoodinatorDtoToCoordinatorMapper.map(createCoordinatorDto)).thenReturn(coordinator);

        coordinatorService.saveCoordinator(createCoordinatorDto);

        verify(coordinatorDao).save(coordinator);

    }

    @Test
    public void testUpdateStudent() throws SQLException {

        CreateCoordinatorDto createCoordinatorDto = CreateCoordinatorDto.builder()
                .name("James")
                .build();

        Coordinator coordinator = Coordinator.builder()
                .name("James")
                .build();

        Integer coordinatorId = 1;

        when(createCoodinatorDtoToCoordinatorMapper.map(createCoordinatorDto)).thenReturn(coordinator);

        coordinatorService.updateCoordinator(createCoordinatorDto, coordinatorId);

        verify(coordinatorDao).update(coordinator, coordinatorId);

    }

    @Test
    public void testGetAllStudents() throws SQLException {

        CoordinatorDto coordinatorDto = CoordinatorDto.builder()
                .id(1)
                .name("Bill")
                .build();

        Coordinator coordinator = Coordinator.builder()
                .id(1)
                .name("Bill")
                .build();

        List<CoordinatorDto> coordinatorDtos = new ArrayList<>(List.of(coordinatorDto));
        List<Coordinator> coordinators = new ArrayList<>(List.of(coordinator));

        when(coodinatorToCoordinatorDtoMapper.map(coordinator)).thenReturn(coordinatorDto);

        when(coordinatorDao.getAll()).thenReturn(coordinators);

        List<CoordinatorDto> coordinatorDtoList = coordinatorService.getAllCoordinators();

        assertEquals(coordinatorDtoList, coordinatorDtos);
    }

    @Test
    public void testGetStudentById() throws SQLException {

        CoordinatorDto coordinatorDto = CoordinatorDto.builder()
                .id(1)
                .name("Bill")
                .build();

        Coordinator coordinator = Coordinator.builder()
                .id(1)
                .name("Bill")
                .build();

        Integer coordinatorId = 1;

        when(coodinatorToCoordinatorDtoMapper.map(coordinator)).thenReturn(coordinatorDto);
        when(coordinatorDao.get(1)).thenReturn(Optional.of(coordinator));

        Optional<CoordinatorDto> result = coordinatorService.getCoordinatorById(coordinatorId);

        assertTrue(result.isPresent());
        assertEquals(result.get(), coordinatorDto);
    }

    @Test
    public void testDeleteStudent() throws SQLException {

        Integer coordinatorId = 1;

        coordinatorService.deleteCoordinator(coordinatorId);

        verify(coordinatorDao).delete(coordinatorId);
    }

}
