package getbux.assignment;

import getbux.assignment.protocol.BuyConfirmation;
import getbux.assignment.protocol.BuyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
public class RestTrader {
    private final RestTemplate tradingRestTemplate = new RestTemplate();

    @Value("${buy.url}")
    String buyUrl;

    @Value("${sell.url}")
    String sellUrl;

    private final HttpHeaders headers;

    RestTrader() {
        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " +
                "eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWE" +
                "xMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInN" +
                "jcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE" +
                "1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiI" +
                "sImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg");
        headers.add(HttpHeaders.ACCEPT_LANGUAGE, "nl-NL,en;q=0.8");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT, "application/json");
    }

    public Optional<String> openPositionForProduct(BuyRequest buyRequest) {
        HttpEntity<BuyRequest> httpEntity = new HttpEntity<>(buyRequest, headers);
        try {
            BuyConfirmation buyConfirmation = tradingRestTemplate.postForObject(buyUrl, httpEntity, BuyConfirmation.class);
            log.debug("received buy confirmation: {}", buyConfirmation);
            return Optional.of(buyConfirmation.getPositionId());
        } catch (RestClientException e) {
            log.error("failed to open position with buy request " + buyRequest, e);
            return Optional.empty();
        }
    }

    public boolean closePosition(String positionId) {
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Void> deleteResponse = tradingRestTemplate.exchange(sellUrl + positionId, HttpMethod.DELETE, httpEntity, Void.class);
            log.debug("received sell confirmation: {}",  deleteResponse);
            return deleteResponse.getStatusCode() == HttpStatus.OK;
        } catch (RestClientException e) {
            log.error("failed to close position " + positionId, e);
            return false;
        }
    }
}
