package dto.course;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CourseDto {
    private Integer id;
    private String name;
}
