package mapper;

import dto.coordinator.CoordinatorDto;
import dto.student.StudentDto;
import entity.Coordinator;
import entity.Student;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper implementation for converting a Coordinator entity to a CoordinatorDto.
 */
public class CoordinatorToCoordinatorDtoMapper implements Mapper<Coordinator, CoordinatorDto>{

    private static final Mapper<Student, StudentDto> studentToStudentDtoMapper
            = new StudentToStudentDtoMapper();

    @Override
    public CoordinatorDto map(Coordinator coordinator) {

        List<StudentDto> studentDtoList = null;
        if(coordinator.getStudents() != null){
            studentDtoList = coordinator.getStudents().stream()
                    .map(studentToStudentDtoMapper::map)
                    .collect(Collectors.toList());
        }
        return CoordinatorDto.builder()
                .id(coordinator.getId())
                .name(coordinator.getName())
                .students(studentDtoList)
                .build();
    }
}