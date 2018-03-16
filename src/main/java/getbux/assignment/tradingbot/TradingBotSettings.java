package getbux.assignment.tradingbot;

import getbux.assignment.protocol.InvestingAmount;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class TradingBotSettings {

    String productId;

    BigDecimal buyPrice;
    BigDecimal upperSellPrice;
    BigDecimal lowSellPrice;

    InvestingAmount investingAmount;

    public TradingBotSettings(String productId, String buyPriceString, String upperSellPriceString,
                              String lowSellPriceString ) throws IncorrectTradingBotSettingsException{
        this.productId = productId;
        buyPrice = new BigDecimal(buyPriceString);
        upperSellPrice = new BigDecimal(upperSellPriceString);
        lowSellPrice = new BigDecimal(lowSellPriceString);
        if(buyPrice.compareTo(upperSellPrice) >= 0 || buyPrice.compareTo(lowSellPrice) <= 0) {
            throw new IncorrectTradingBotSettingsException("buyPrice must be inside (lowSellPrice, upperSellLimit) range");
        }
        investingAmount = new InvestingAmount("BUX", 2, "200.00");
    }
}
