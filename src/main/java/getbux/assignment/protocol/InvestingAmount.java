package getbux.assignment.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvestingAmount {
    String currency;
    int decimals;
    String amount;
}
