package org.knowm.xchange.huobi.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HuobiBorrowCurrency {
    private final String symbol;
    private final String currency;
    private final String amount;

    public HuobiBorrowCurrency(
            @JsonProperty("symbol") String symbol,
            @JsonProperty("currency") String currency,
            @JsonProperty("amount") String amount) {
        this.symbol = symbol;
        this.currency = currency;
        this.amount = amount;
    }

    private String getSymbol() {
        return symbol;
    }

    private String getCurrency() {
        return currency;
    }

    private String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format(
                "HuobiBorrowCurrency [symbol = %s, currency = %s, amount = %s",
                getSymbol(), getCurrency(), getAmount());
    }
}
