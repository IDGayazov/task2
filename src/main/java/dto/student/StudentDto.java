package dto.student;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StudentDto {

    private int id;
    private String name;

}
