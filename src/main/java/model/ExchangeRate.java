package model;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ExchangeRate {
    private int id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}
