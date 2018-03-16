package getbux.assignment.tradingbot;

import getbux.assignment.protocol.BuyRequest;
import getbux.assignment.RestTrader;
import getbux.assignment.protocol.TradingQuote;
import getbux.assignment.WebSocketQuoteFeeder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NonBlockingTradingBotTest {

    public static final String PRODUCT_ID = "sb26493";

    NonBlockingTradingBot tradingBot;
    RestTrader trader;
    WebSocketQuoteFeeder quoteFeeder;
    TradingBotSettings settings;

    @Before
    public void setUp()  {
        trader = mock(RestTrader.class);
        quoteFeeder = mock(WebSocketQuoteFeeder.class);
        settings = new TradingBotSettings(PRODUCT_ID, "12401.3", "12401.5", "12401.1");
        tradingBot = new NonBlockingTradingBot(trader, quoteFeeder, settings);
        tradingBot.init();
    }

    @Test
    public void whenConnectionEstablishedSubscriptionShouldBeIssued(){
        tradingBot.processConnectionEstablished();
        verify(quoteFeeder, times(1)).subsribeTo(PRODUCT_ID);
    }

    @Test
    public void shouldIgnoreQuotesWhenOtherQuotes(){
        tradingBot.processQuote(buildTradingQuoteForProductAndPrice("1234", null));
    }

    @Test
    public void shouldOpenPositionAndSavePositionIdWhenPriceMatches(){
        BuyRequest buyRequest = new BuyRequest(settings.productId, settings.getInvestingAmount(), 2, "BUY");
        when(trader.openPositionForProduct(buyRequest)).thenReturn(Optional.of("position_id"));

        tradingBot.processQuote(buildTradingQuoteForProductAndPrice(settings.productId, settings.buyPrice.toString()));

        assertEquals("position ids do not match", "position_id", tradingBot.getPositionId());
        verify(trader, times(1)).openPositionForProduct(buyRequest);
    }

    @Test
    public void shouldClosePositionAndNullifyPositionIdWhenPriceMatchesUpperLimit(){
        tradingBot.setPositionId("position_id");
        when(trader.closePosition("position_id")).thenReturn(true);

        tradingBot.processQuote(buildTradingQuoteForProductAndPrice(settings.productId, settings.getUpperSellPrice().toString()));

        assertNull(tradingBot.getPositionId());
        verify(trader, times(1)).closePosition("position_id");
    }

    @Test
    public void shouldClosePositionAndNullifyPositionIdWhenPriceMatchesLowerLimit(){
        tradingBot.setPositionId("position_id");
        when(trader.closePosition("position_id")).thenReturn(true);

        tradingBot.processQuote(buildTradingQuoteForProductAndPrice(settings.productId, settings.getLowSellPrice().toString()));

        assertNull(tradingBot.getPositionId());
        verify(trader, times(1)).closePosition("position_id");
    }

    @Test
    public void shouldNotClosePositionWhenClosingFails(){
        tradingBot.setPositionId("position_id");
        when(trader.closePosition("position_id")).thenReturn(false);

        tradingBot.processQuote(buildTradingQuoteForProductAndPrice(settings.productId, settings.getLowSellPrice().toString()));

        assertEquals("position ids do not match", "position_id", tradingBot.getPositionId());
        verify(trader, times(1)).closePosition("position_id");
    }


    private static TradingQuote buildTradingQuoteForProductAndPrice(String productId, String price) {
        TradingQuote.Body body = new TradingQuote.Body(productId, price);
        return new TradingQuote("trading.quote", body);
    }

    @After
    public void tearDown() {
        tradingBot.stopReceivingQuoteFeed();
    }
}