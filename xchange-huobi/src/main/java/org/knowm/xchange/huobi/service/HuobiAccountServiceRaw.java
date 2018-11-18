package org.knowm.xchange.huobi.service;

import java.io.IOException;
import java.math.BigDecimal;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.huobi.Huobi;
import org.knowm.xchange.huobi.HuobiUtils;
import org.knowm.xchange.huobi.dto.account.*;
import org.knowm.xchange.huobi.dto.account.results.HuobiAccountResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiBalanceResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiCreateWithdrawResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiDepositAddressResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiDepositAddressWithTagResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiFundingHistoryResult;

import static org.knowm.xchange.currency.CurrencyPair.BTC_USDT;

public class HuobiAccountServiceRaw extends HuobiBaseService {
  private HuobiAccount[] accountCache = null;

  HuobiAccountServiceRaw(Exchange exchange) {
    super(exchange);
  }

  public HuobiBalance getHuobiBalance(String accountID) throws IOException {
    HuobiBalanceResult huobiBalanceResult =
        huobi.getBalance(
            accountID,
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(huobiBalanceResult);
  }

  public HuobiAccount[] getAccounts() throws IOException {
    if (accountCache == null) {
      HuobiAccountResult huobiAccountResult =
          huobi.getAccount(
              exchange.getExchangeSpecification().getApiKey(),
              HuobiDigest.HMAC_SHA_256,
              2,
              HuobiUtils.createUTCDate(exchange.getNonceFactory()),
              signatureCreator);
      accountCache = checkResult(huobiAccountResult);
    }

    return accountCache;
  }

  public HuobiAccount getMarginAccount(CurrencyPair currencyPair) throws Exception {
    String base = currencyPair.base.getCurrencyCode();
    String counter = currencyPair.counter.getCurrencyCode();
    for (HuobiAccount account : this.getAccounts()) {
      if (!account.isMargin()) {
        continue;
      }
      int c = 0;
      HuobiBalance balance = getHuobiBalance(String.valueOf(account.getId()));
      for (HuobiBalanceRecord currencyBalance : balance.getList()) {
        if(!currencyBalance.getType().equals("trade")) {
          continue;
        }
        Currency currency = HuobiUtils.translateHuobiCurrencyCode(currencyBalance.getCurrency());

        if(currency.getCurrencyCode().equals(base) || currency.getCurrencyCode().equals(counter)) {
          c += 1;
        }
      }
      if (c >= 2) {
        return account;
      }
    }
    throw new Exception("Margin account not found for currency pair "+currencyPair.toString());
  }

  public String getDepositAddress(String currency) throws IOException {
    HuobiDepositAddressResult depositAddressResult =
        huobi.getDepositAddress(
            currency.toLowerCase(),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(depositAddressResult);
  }

  public HuobiDepositAddressWithTag getDepositAddressWithTag(String currency) throws IOException {
    HuobiDepositAddressWithTagResult depositAddressWithTagResult =
        huobi.getDepositAddressWithTag(
            currency.toLowerCase(),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(depositAddressWithTagResult);
  }

  public HuobiFundingRecord[] getDepositWithdrawalHistory(String currency, String type, String from)
      throws IOException {
    HuobiFundingHistoryResult fundingHistoryResult =
        huobi.getFundingHistory(
            currency.toLowerCase(),
            type.toLowerCase(),
            from,
            "100",
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(fundingHistoryResult);
  }

  public long createWithdraw(
      String currency, BigDecimal amount, BigDecimal fee, String address, String addressTag)
      throws IOException {
    HuobiCreateWithdrawRequest createWithdrawRequest =
        new HuobiCreateWithdrawRequest(address, amount, currency.toLowerCase(), fee, addressTag);
    HuobiCreateWithdrawResult createWithdrawResult =
        huobi.createWithdraw(
            createWithdrawRequest,
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(createWithdrawResult);
  }
}
