package getbux.assignment.tradingbot;

import getbux.assignment.protocol.BuyRequest;
import getbux.assignment.RestTrader;
import getbux.assignment.protocol.TradingQuote;
import getbux.assignment.WebSocketQuoteFeeder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class NonBlockingTradingBot {

    private final RestTrader restTrader;
    private final WebSocketQuoteFeeder quoteFeeder;

    private final TradingBotSettings settings;

    private String positionId;

    final BlockingQueue<TradingQuote> quoteQueue;

    public NonBlockingTradingBot(RestTrader restTrader, WebSocketQuoteFeeder quoteFeeder, TradingBotSettings settings) {
        this.restTrader = restTrader;
        this.quoteFeeder = quoteFeeder;
        this.settings = settings;
        quoteQueue = new LinkedBlockingDeque<>();
        Executors.newSingleThreadExecutor().execute(this::processMessagesFromQueue);
        log.info("started trading with {}", settings);
    }

    @PostConstruct
    void init() {
        quoteFeeder.setTradingBot(this);
    }

    private void processMessagesFromQueue() {
        while (true) {
            try{
                ArrayList<TradingQuote> messages = new ArrayList<>();
                if(quoteQueue.drainTo(messages) == 0) {
                    messages.add(quoteQueue.take());
                }
                for(TradingQuote quote : messages)
                    if(quote instanceof PoisonPill) {
                        log.debug("received poison pill, stopping receiving quotes");
                        break;
                    } else if(quote instanceof ConnectionEstablished) {
                        processConnectionEstablished();
                    } else {
                        processQuote(quote);
                    }
            } catch (Exception e) {
               log.error("exception while taking from quoteQueue", e);
            }
        }
    }

    public void onTradingQuote(TradingQuote quote){
        quoteQueue.offer(quote);
    }

    public void onQuoteFeedConnected(){
        quoteQueue.offer(new ConnectionEstablished());
    }

    void processConnectionEstablished(){
        quoteFeeder.subsribeTo(settings.productId);
    }

    private boolean shouldOpenPosition(TradingQuote quote) {
        BigDecimal quotePrice = new BigDecimal(quote.getCurrentPrice());
        boolean shouldOpen = quotePrice.compareTo(settings.buyPrice) == 0;
        if(shouldOpen) {
            log.debug("quote's price  {} equals to {}, opening position", quotePrice.toString(), settings.buyPrice.toString());
        } else {
            log.debug("ignoring quote as quote's price  {} does not equal to {}", quotePrice.toString(), settings.buyPrice.toString());
        }
        return shouldOpen;
    }

    private boolean shouldClosePosition(TradingQuote quote){
        BigDecimal quotePrice = new BigDecimal(quote.getCurrentPrice());
        return quotePrice.compareTo(settings.getUpperSellPrice()) >= 0 ||
               quotePrice.compareTo(settings.getLowSellPrice()) <= 0;
    }

    public void stopReceivingQuoteFeed(){
        quoteQueue.offer(new PoisonPill());
    }

    void processQuote(TradingQuote quote) {
        if(!quote.hasProductId(settings.productId)) {
            log.info("ignored quote {}", quote);
            return;
        }
        if (positionId == null && shouldOpenPosition(quote)) {
            BuyRequest buyRequest = new BuyRequest(settings.productId, settings.getInvestingAmount(), 2, "BUY");
            restTrader.openPositionForProduct(buyRequest).ifPresent( p -> {
                positionId = p;
                log.info("successfully opened position {}", positionId);
            });
            return;
        }
        if (positionId != null && shouldClosePosition(quote)) {
            if(restTrader.closePosition(positionId)) {
                log.info("successfully closed position {}", positionId);
                positionId = null;
            } else {
                log.error("failed to close position {}", positionId);
            }
        }
    }

    //used only by testing
    String getPositionId() {
        return positionId;
    }

    //used only by testing
    void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    class PoisonPill extends TradingQuote {
    }

    class ConnectionEstablished extends TradingQuote {
    }
}
