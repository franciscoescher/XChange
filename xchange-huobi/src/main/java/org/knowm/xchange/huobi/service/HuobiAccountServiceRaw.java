package org.knowm.xchange.huobi.service;

import java.io.IOException;
import java.math.BigDecimal;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.huobi.HuobiUtils;
import org.knowm.xchange.huobi.dto.account.*;
import org.knowm.xchange.huobi.dto.account.HuobiAccount;
import org.knowm.xchange.huobi.dto.account.HuobiBalance;
import org.knowm.xchange.huobi.dto.account.HuobiCreateWithdrawRequest;
import org.knowm.xchange.huobi.dto.account.HuobiDepositAddressWithTag;
import org.knowm.xchange.huobi.dto.account.HuobiFundingRecord;
import org.knowm.xchange.huobi.dto.account.HuobiWithdrawFeeRange;
import org.knowm.xchange.huobi.dto.account.results.HuobiAccountResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiBalanceResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiBorrowCurrencyResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiCreateWithdrawResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiDepositAddressResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiDepositAddressWithTagResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiFundingHistoryResult;
import org.knowm.xchange.huobi.dto.account.results.HuobiWithdrawFeeRangeResult;

public class HuobiAccountServiceRaw extends HuobiBaseService {
  private HuobiAccount[] accountCache = null;

  HuobiAccountServiceRaw(Exchange exchange) {
    super(exchange);
  }

  HuobiBalance getHuobiBalance(String accountID) throws IOException {
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

  public HuobiWithdrawFeeRange getWithdrawFeeRange(String currency) throws IOException {
    HuobiWithdrawFeeRangeResult result =
        huobi.getWithdrawFeeRange(
            currency.toLowerCase(),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
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

  public HuobiAccount getMarginAccount(String base, String counter) throws IOException {
    for (HuobiAccount account : this.getAccounts()) {
      if (!account.isMargin()) {
        continue;
      }
      int c = 0;
      HuobiBalance balance = getHuobiBalance(String.valueOf(account.getId()));
      for (HuobiBalanceRecord balanceRecord : balance.getList()) {
        if (!balanceRecord.getType().equals("trade")) {
          continue;
        }

        String balanceCurrency = balanceRecord.getCurrency();
        if (balanceCurrency.equals(base) || balanceCurrency.equals(counter)) {
          c += 1;
        }
      }
      if (c >= 2) {
        return account;
      }
    }
    throw new IOException("Margin account not found for currency pair " + base + counter);
  }

  long borrowCurrency(String symbol, String currency, BigDecimal amount) throws IOException {
    HuobiBorrowCurrencyResult borrowCurrencyResult =
        huobi.borrowCurrency(
            new HuobiBorrowCurrencyRequest(symbol, currency, String.valueOf(amount)),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(borrowCurrencyResult);
  }
}
