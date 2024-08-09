package mapper;

import dto.course.CourseDto;
import entity.Course;

/**
 * Mapper implementation for converting a CourseDto to a Course.
 */
public class CourseDtoToCourseMapper implements Mapper<CourseDto, Course>{

    @Override
    public Course map(CourseDto value) {
        return Course.builder()
                .id(value.getId())
                .name(value.getName())
                .build();
    }
}
