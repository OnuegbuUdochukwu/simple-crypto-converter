package com.codewithudo.simplecryptoconverter.dto;

import lombok.Data;

@Data
public class MarketData {
    private long at;
    private Ticker ticker;
    private String market;
}
