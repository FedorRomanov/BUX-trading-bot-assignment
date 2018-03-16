package getbux.assignment.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BuyRequest {

    String productId;
    InvestingAmount investingAmount;
    int leverage;
    String direction;

}
