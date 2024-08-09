package dto.coordinator;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateCoordinatorDto {

    private Integer id;

    private String name;
}
