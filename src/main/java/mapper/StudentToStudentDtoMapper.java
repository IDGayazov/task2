package mapper;

import dto.student.StudentDto;
import entity.Student;

/**
 * Mapper implementation for converting a Student entity to a StudentDto.
 */
public class StudentToStudentDtoMapper implements Mapper<Student, StudentDto>{

    @Override
    public StudentDto map(Student value) {
        return StudentDto.builder()
                .id(value.getId())
                .name(value.getName())
                .build();
    }
}
