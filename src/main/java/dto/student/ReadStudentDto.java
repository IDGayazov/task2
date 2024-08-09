package dto.student;

import dto.coordinator.CreateCoordinatorDto;
import dto.course.CourseDto;
import entity.Coordinator;
import entity.Course;
import lombok.*;

import java.util.List;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReadStudentDto {

    private int id;
    private String name;
    private CreateCoordinatorDto coordinator;
    private List<CourseDto> courses;

}
