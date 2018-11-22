package org.knowm.xchange.huobi.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;

public final class HuobiKLine {

  private final long id;
  private final BigDecimal amount;
  private final long count;
  private final BigDecimal open;
  private final BigDecimal close;
  private final BigDecimal low;
  private final BigDecimal high;
  private final BigDecimal vol;
  private Date ts;

  public HuobiKLine(
      @JsonProperty("id") long id,
      @JsonProperty("amount") BigDecimal amount,
      @JsonProperty("count") long count,
      @JsonProperty("open") BigDecimal open,
      @JsonProperty("close") BigDecimal close,
      @JsonProperty("low") BigDecimal low,
      @JsonProperty("high") BigDecimal high,
      @JsonProperty("vol") BigDecimal vol) {
    this.id = id;
    this.amount = amount;
    this.count = count;
    this.open = open;
    this.close = close;
    this.low = low;
    this.high = high;
    this.vol = vol;
  }

  public long getId() {
    return id;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public long getCount() {
    return count;
  }

  public BigDecimal getOpen() {
    return open;
  }

  public BigDecimal getClose() {
    return close;
  }

  public BigDecimal getLow() {
    return low;
  }

  public BigDecimal getHigh() {
    return high;
  }

  public BigDecimal getVol() {
    return vol;
  }

  public Date getTs() {
    return ts;
  }

  public void setTs(Date ts) {
    this.ts = ts;
  }

  @Override
  public String toString() {
    return "HuobiKLine [id="
        + getId()
        + ", amount="
        + getAmount()
        + ", count="
        + getCount()
        + ", open="
        + getOpen()
        + ", close="
        + getClose()
        + ", low="
        + getLow()
        + ", high="
        + getHigh()
        + ", vol="
        + getVol()
        + ", ts="
        + getTs()
        + "]";
  }
}
