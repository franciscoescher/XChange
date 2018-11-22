package org.knowm.xchange.huobi.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HuobiBorrowCurrencyRequest {

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("amount")
  private String amount;

  public HuobiBorrowCurrencyRequest(String symbol, String currency, String amount) {
    this.symbol = symbol;
    this.currency = currency;
    this.amount = amount;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getCurrency() {
    return currency;
  }

  public String getAmount() {
    return amount;
  }

  @Override
  public String toString() {
    return String.format(
        "HuobiBorrowCurrency [symbol = %s, currency = %s, amount = %s]",
        getSymbol(), getCurrency(), getAmount());
  }
}
