package getbux.assignment;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import getbux.assignment.protocol.SubscriptionMessage;
import getbux.assignment.protocol.TradingQuote;
import getbux.assignment.tradingbot.NonBlockingTradingBot;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

/**
 * Basic Echo Client Socket
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
@Slf4j
public class WebSocketQuoteFeeder {
    private boolean isConnectionConfirmed;
    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    WebSocketClient client = new WebSocketClient();

    @Value("${quote.feed.url}")
    String quoteFeedUrl;

    private Session session;

    private NonBlockingTradingBot tradingBot;

    public void setTradingBot(NonBlockingTradingBot tradingBot) {
        this.tradingBot = tradingBot;
    }

    public void startFeeding() {
        try {
            client.start();

            URI quoteFeedUri = new URI(quoteFeedUrl);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " +
                    "eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWE" +
                    "xMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInN" +
                    "jcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE" +
                    "1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiI" +
                    "sImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg");
            request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "nl-NL,en;q=0.8");
            client.connect(this,quoteFeedUri,request);
            log.info("Connected  to : {}", quoteFeedUri);

        } catch (Throwable t) {
            log.error("failed to connect to " + quoteFeedUrl, t);
        }
    }

    public  void stopFeeding() {
        log.info("disconnecting from {}", session.getRemoteAddress().getHostString());
        try  {
            client.stop();
        } catch (Exception e){
            log.error("failed to stop client", e);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        log.info("Connection closed: {} - {}", statusCode, reason);
        this.session = null;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        log.info("Got connect: {}", session);
        this.session = session;
    }

    private boolean isConnectionConfirmationMessage(String msg){
        try {
            JsonNode root = OBJECT_MAPPER.readTree(msg);
            JsonNode connectionStatus = root.path("t");
            if("connect.connected".equalsIgnoreCase(connectionStatus.asText())) {
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            log.error("failed to parse " + msg, e);
            return false;
        }
    }

    public void subsribeTo(String productId) {
        try {
            session.getRemote().sendString(new SubscriptionMessage(productId).asJsonString());
            log.info("subscribed to quote feed for {}", productId);
        } catch (IOException e) {
            log.error("failed to subscribe to  " + productId, e);
        }
    }

    static Optional<TradingQuote> parseTradingQuote(String msg) {
        try {
            TradingQuote quote = OBJECT_MAPPER.readValue(msg, TradingQuote.class);
            if(quote.isTradingQuote()) {
                return Optional.of(quote);
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            log.debug("failed to parse message", e);
            return Optional.empty();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        log.debug("Got: {}", msg);
        if(isConnectionConfirmationMessage(msg)){
            isConnectionConfirmed = true;
            tradingBot.onQuoteFeedConnected();
        } else if(isConnectionConfirmed) {
            parseTradingQuote(msg).ifPresent(q -> tradingBot.onTradingQuote(q));
        }
    }
}

