package getbux.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import getbux.assignment.protocol.TradingQuote;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

public class TradingQuoteTest {

    @Test
    public void unmarshallingFromStringShouldWork() throws IOException {
        String quoteAsString = "{\"t\":\"trading.quote\",\"id\":\"17e8f50a-1265-11e8-9f79-9d052b3e1a24\",\"v\":2,\"body\":{\"securityId\":\"sb26493\",\"currentPrice\":\"12352.8\"}}";
        TradingQuote quote = new ObjectMapper().readValue(quoteAsString, TradingQuote.class);
        assertTrue(quote.isTradingQuote());
    }
}