package getbux.assignment.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuyConfirmation {
    String id;
    String positionId;
    Product product;
    InvestingAmount investingAmount;
    Price price;
    int leverage;
    String direction;
    String type;
    long dateCreated;

    static class Product {
        String securityId;
        String symbol;
        String displayName;
    }

    static class Price {
        String currency;
        int decimals;
        String amount;
    }
}
