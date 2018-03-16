package getbux.assignment;

import getbux.assignment.tradingbot.NonBlockingTradingBot;
import getbux.assignment.tradingbot.TradingBotSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
public class TradingBotAppConfig {

    static TradingBotSettings settings;

    @Bean
    public RestTrader restTrader() {
        return new RestTrader();
    }

    @Bean
    public NonBlockingTradingBot tradingBot(RestTrader trader,
                                            WebSocketQuoteFeeder quoteFeeder) {
        return new NonBlockingTradingBot(trader, quoteFeeder, settings);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean WebSocketQuoteFeeder quoteFeeder() {
        return new WebSocketQuoteFeeder();
    }

}
