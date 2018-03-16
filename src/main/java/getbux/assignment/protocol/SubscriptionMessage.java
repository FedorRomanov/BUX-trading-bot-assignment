package getbux.assignment.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public class SubscriptionMessage {
    String productId;

    public String asJsonString() {
        return "{\n" +
                "\"subscribeTo\": [\n" +
                "\"trading.product." + productId + "\"\n" +
                "]}";
    }
}
