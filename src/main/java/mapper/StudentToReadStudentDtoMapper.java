package mapper;

import dto.coordinator.CreateCoordinatorDto;
import dto.course.CourseDto;
import dto.student.ReadStudentDto;
import entity.Student;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper implementation for converting a Student entity to a ReadStudentDto.
 */
public class StudentToReadStudentDtoMapper implements Mapper<Student, ReadStudentDto>{

    private static final CourseToCourseDtoMapper courseToCourseDtoMapper = new CourseToCourseDtoMapper();

    @Override
    public ReadStudentDto map(Student student) {

        CreateCoordinatorDto coordinatorDto = null;

        if(student.getCoordinator() != null){
            coordinatorDto = CreateCoordinatorDto.builder()
                    .id(student.getCoordinator().getId())
                    .name(student.getCoordinator().getName())
                    .build();
        }

        List<CourseDto> courses = null;
        if(student.getCourses() != null && !student.getCourses().isEmpty()){
            courses = student.getCourses().stream()
                    .filter(Objects::nonNull)
                    .map(courseToCourseDtoMapper::map)
                    .collect(Collectors.toList());
        }

        return ReadStudentDto.builder()
                .id(student.getId())
                .name(student.getName())
                .coordinator(coordinatorDto)
                .courses(courses)
                .build();
    }
}
