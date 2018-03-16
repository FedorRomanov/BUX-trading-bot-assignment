package getbux.assignment.tradingbot;

import org.junit.Test;

public class TradingBotSettingsTest {

    @Test
    public void instantiatingSettingWithCorrectValuesShouldPass(){
        new TradingBotSettings("sb26493", "12387.3", "12401.5", "12387.1");
    }

    @Test(expected = IncorrectTradingBotSettingsException.class)
    public void instantiatingSettingWithInCorrectValuesShouldThrowException(){
        new TradingBotSettings("sb26493", "12387.3", "11401.5", "12387.1");
    }

}