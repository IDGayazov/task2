package mapper;

import dto.course.CourseDto;
import entity.Course;

/**
 * Mapper implementation for converting a Course entity to a CourseDto.
 */
public class CourseToCourseDtoMapper implements Mapper<Course, CourseDto> {

    @Override
    public CourseDto map(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .build();
    }

}
