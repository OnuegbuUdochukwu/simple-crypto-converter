package com.codewithudo.simplecryptoconverter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Ticker {
    private String low;
    private String high;
    private String open;

    @JsonProperty("vol")
    private String volume;

    @JsonProperty("last")
    private String price;

    @JsonProperty("sell")
    private String ask;

    @JsonProperty("buy")
    private String bid;
}
