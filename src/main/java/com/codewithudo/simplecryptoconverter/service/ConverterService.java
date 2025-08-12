package com.codewithudo.simplecryptoconverter.service;

import com.codewithudo.simplecryptoconverter.dto.ConversionResult;
import com.codewithudo.simplecryptoconverter.dto.SingleTickerResponse;
import com.codewithudo.simplecryptoconverter.dto.Ticker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ConverterService {

    private final RestTemplate restTemplate;

    public ConverterService() {
        this.restTemplate = new RestTemplate();
    }

    public ConversionResult convert(String from, String to, BigDecimal amount) {
        // Construct the market pair, e.g., "btc" + "ngn" -> "btcngn"
        String marketPair = from + to;
        Ticker ticker = getTickerForMarket(marketPair);

        BigDecimal rate;
        BigDecimal targetAmount;

        if (ticker != null) {
            // Direct conversion (e.g., BTC -> NGN)
            rate = new BigDecimal(ticker.getPrice());
            targetAmount = amount.multiply(rate);
        } else {
            // Try inverse conversion (e.g., NGN -> BTC)
            String inverseMarketPair = to + from;
            Ticker inverseTicker = getTickerForMarket(inverseMarketPair);

            if (inverseTicker == null) {
                throw new IllegalArgumentException("Conversion market not supported: " + marketPair);
            }

            rate = new BigDecimal(inverseTicker.getPrice());
            // For inverse, we divide: amount / rate
            targetAmount = amount.divide(rate, 8, RoundingMode.HALF_UP);
        }

        ConversionResult result = new ConversionResult();
        result.setSourceCurrency(from.toUpperCase());
        result.setSourceAmount(amount);
        result.setTargetCurrency(to.toUpperCase());
        result.setTargetAmount(targetAmount);
        result.setRate(rate);

        return result;
    }

    private Ticker getTickerForMarket(String market) {
        String url = "https://api.quidax.com/api/v1/markets/" + market + "/ticker";
        try {
            SingleTickerResponse response = restTemplate.getForObject(url, SingleTickerResponse.class);
            if (response != null && "success".equals(response.getStatus()) && response.getData() != null) {
                return response.getData().getTicker();
            }
        } catch (HttpClientErrorException.NotFound e) {
            // If the market is not found, the API returns a 404, which is expected.
            return null;
        }
        return null;
    }
}

