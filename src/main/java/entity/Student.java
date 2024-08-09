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
public class Student {

    private Integer id;

    private String name;

    private Coordinator coordinator;

    private List<Course> courses;

}
