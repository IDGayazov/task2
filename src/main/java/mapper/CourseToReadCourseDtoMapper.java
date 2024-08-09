package mapper;

import dto.course.ReadCourseDto;
import dto.student.StudentDto;
import entity.Course;
import entity.Student;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper implementation for converting a Course to a ReadCourseDto.
 */
public class CourseToReadCourseDtoMapper implements Mapper<Course, ReadCourseDto>{

    private static final Mapper<Student, StudentDto> studentToStudentDtoMapper =
            new StudentToStudentDtoMapper();

    @Override
    public ReadCourseDto map(Course value) {

        List<StudentDto> students = null;
        if(value.getStudents() != null){
            students = value.getStudents().stream()
                    .filter(Objects::nonNull)
                    .map(studentToStudentDtoMapper::map)
                    .collect(Collectors.toList());
        }

        return ReadCourseDto.builder()
                .id(value.getId())
                .name(value.getName())
                .students(students)
                .build();
    }
}
