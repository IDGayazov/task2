package mapper;

import dto.course.CourseDto;
import dto.student.CreateStudentDto;
import entity.Coordinator;
import entity.Course;
import entity.Student;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper implementation for converting a CreateStudentDto to a Student.
 */
public class CreateStudentDtoToStudentMapper implements Mapper<CreateStudentDto, Student> {

    private static final Mapper<CourseDto, Course> courseDtoToCourseMapper =
            new CourseDtoToCourseMapper();

    @Override
    public Student map(CreateStudentDto createStudentDto) {

        Coordinator coordinator = null;
        if (createStudentDto.getCoordinatorId() != null) {
            coordinator = Coordinator.builder()
                    .id(createStudentDto.getCoordinatorId())
                    .build();
        }

        List<CourseDto> coursesDto = createStudentDto.getCourses();

        List<Course> courses = null;

        if(coursesDto != null && !coursesDto.isEmpty()){
            courses = coursesDto.stream()
                    .map(courseDtoToCourseMapper::map)
                    .collect(Collectors.toList());
        }

        System.out.println(courses);

        return Student.builder()
                .id(createStudentDto.getId())
                .name(createStudentDto.getName())
                .coordinator(coordinator)
                .courses(courses)
                .build();
    }

}
