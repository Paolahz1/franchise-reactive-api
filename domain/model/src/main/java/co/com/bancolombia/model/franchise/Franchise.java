package co.com.bancolombia.model.franchise;
import java.util.List;

import co.com.bancolombia.model.branch.Branch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Franchise {

    private Long id;
    private String name;
    private List<Branch> branchList;

}
