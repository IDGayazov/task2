package entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Course {

    private Integer id;

    private String name;

    private List<Student> students;

}
