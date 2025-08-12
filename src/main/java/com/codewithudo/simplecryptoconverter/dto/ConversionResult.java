package com.codewithudo.simplecryptoconverter.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConversionResult {
    private BigDecimal sourceAmount;
    private String sourceCurrency;
    private BigDecimal targetAmount;
    private String targetCurrency;
    private BigDecimal rate;
}