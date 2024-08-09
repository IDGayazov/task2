package dto.student;

import dto.course.CourseDto;
import lombok.*;

import java.util.List;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateStudentDto {

    private Integer id;
    private String name;
    private Integer coordinatorId;
    private List<CourseDto> courses;

}

