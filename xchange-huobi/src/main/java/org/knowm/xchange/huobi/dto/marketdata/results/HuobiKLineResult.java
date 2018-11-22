package org.knowm.xchange.huobi.dto.marketdata.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Calendar;
import java.util.Date;
import org.knowm.xchange.huobi.dto.HuobiResult;
import org.knowm.xchange.huobi.dto.marketdata.HuobiKLine;

public class HuobiKLineResult extends HuobiResult<HuobiKLine[]> {

  private final Date ts;
  private final String ch;

  @JsonCreator
  public HuobiKLineResult(
      @JsonProperty("status") String status,
      @JsonProperty("ts") Date ts,
      @JsonProperty("data") HuobiKLine[] data,
      @JsonProperty("ch") String ch,
      @JsonProperty("err-code") String errCode,
      @JsonProperty("err-msg") String errMsg) {
    super(status, errCode, errMsg, data);

    Date tempTs = ts;

    int field = 0;
    int amount = 0;

    String[] split = ch.split("\\.");
    String period = split[split.length - 1];
    switch (period) {
      case "1min":
        field = Calendar.MINUTE;
        amount = -1;
        break;
      case "15min":
        field = Calendar.MINUTE;
        amount = -15;
        break;
      case "30min":
        field = Calendar.MINUTE;
        amount = -30;
        break;
      case "60min":
        field = Calendar.HOUR;
        amount = -1;
        break;
      case "1day":
        field = Calendar.DAY_OF_YEAR;
        amount = -1;
        break;
      case "1week":
        field = Calendar.WEEK_OF_YEAR;
        amount = -1;
        break;
      case "1month":
        field = Calendar.MONTH;
        amount = -1;
        break;
      case "1year":
        field = Calendar.YEAR;
        amount = -1;
        break;
    }

    int resultLenght = getResult().length;
    for (int i = 1; i <= resultLenght; i++) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(tempTs);
      cal.add(field, amount);
      tempTs = cal.getTime();
      getResult()[resultLenght - i].setTs(tempTs);
    }
    this.ts = ts;
    this.ch = ch;
  }

  public Date getTs() {
    return ts;
  }

  public String getCh() {
    return ch;
  }
}
