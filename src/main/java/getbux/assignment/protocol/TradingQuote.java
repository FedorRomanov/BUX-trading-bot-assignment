package getbux.assignment.protocol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class TradingQuote {

    @NotNull
    String t; //type

    String id;
    int v; //version

    @NotNull
    Body body;

    public TradingQuote(String t, Body b) {
        this.t = t;
        this.body = b;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        @NotNull
        String securityId;
        @NotNull
        String currentPrice;

        public Body(String securityId, String currentPrice) {
            this.securityId = securityId;
            this.currentPrice = currentPrice;
        }
    }

    public boolean hasProductId(String productId) {
        return body.securityId.equalsIgnoreCase(productId);
    }

    public boolean isTradingQuote(){
        return "trading.quote".equalsIgnoreCase(t);
    }

    public String getCurrentPrice(){
        return body.currentPrice;
    }
}
