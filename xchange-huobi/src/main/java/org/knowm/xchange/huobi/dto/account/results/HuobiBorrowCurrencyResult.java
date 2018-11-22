package org.knowm.xchange.huobi.dto.account.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.knowm.xchange.huobi.dto.HuobiResult;

public class HuobiBorrowCurrencyResult extends HuobiResult<Long> {
  @JsonCreator
  public HuobiBorrowCurrencyResult(
      @JsonProperty("status") String status,
      @JsonProperty("data") long data,
      @JsonProperty("err-code") String errCode,
      @JsonProperty("err-msg") String errMsg) {
    super(status, errCode, errMsg, data);
  }
}
