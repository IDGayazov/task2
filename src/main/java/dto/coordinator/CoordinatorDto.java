package dto.coordinator;

import dto.student.StudentDto;
import lombok.*;

import java.util.List;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CoordinatorDto {

    private Integer id;

    private String name;

    private List<StudentDto> students;
}

