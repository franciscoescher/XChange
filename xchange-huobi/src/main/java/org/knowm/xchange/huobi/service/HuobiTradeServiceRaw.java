package org.knowm.xchange.huobi.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.huobi.HuobiUtils;
import org.knowm.xchange.huobi.dto.trade.HuobiCreateOrderRequest;
import org.knowm.xchange.huobi.dto.trade.HuobiOrder;
import org.knowm.xchange.huobi.dto.trade.results.HuobiCancelOrderResult;
import org.knowm.xchange.huobi.dto.trade.results.HuobiOrderInfoResult;
import org.knowm.xchange.huobi.dto.trade.results.HuobiOrderResult;
import org.knowm.xchange.huobi.dto.trade.results.HuobiOrdersResult;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;

class HuobiTradeServiceRaw extends HuobiBaseService {

  HuobiTradeServiceRaw(Exchange exchange) {
    super(exchange);
  }

  HuobiOrder[] getHuobiTradeHistory(TradeHistoryParams tradeHistoryParams) throws IOException {
    String tradeStates = "partial-filled,partial-canceled,filled";
    HuobiOrdersResult result =
        huobi.getOpenOrders(
            tradeStates,
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
  }

  HuobiOrder[] getHuobiOpenOrders() throws IOException {
    String states = "pre-submitted,submitted,partial-filled";
    HuobiOrdersResult result =
        huobi.getOpenOrders(
            states,
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
  }

  String cancelHuobiOrder(String orderId) throws IOException {
    HuobiCancelOrderResult result =
        huobi.cancelOrder(
            orderId,
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
  }

  String placeHuobiLimitOrder(LimitOrder limitOrder) throws IOException {
    String type;
    if (limitOrder.getType() == OrderType.BID) {
      type = "buy-limit";
    } else if (limitOrder.getType() == OrderType.ASK) {
      type = "sell-limit";
    } else {
      throw new ExchangeException("Unsupported order type.");
    }

    HuobiOrderResult result =
        huobi.placeLimitOrder(
            new HuobiCreateOrderRequest(
                String.valueOf(
                    ((HuobiAccountServiceRaw) exchange.getAccountService())
                        .getAccounts()[0].getId()),
                limitOrder
                    .getOriginalAmount()
                    .setScale(
                        getCurrencyAmountPrecision(limitOrder.getCurrencyPair()),
                        BigDecimal.ROUND_DOWN)
                    .toString(),
                limitOrder
                    .getLimitPrice()
                    .setScale(
                        getCurrencyPricePrecision(limitOrder.getCurrencyPair()),
                        BigDecimal.ROUND_DOWN)
                    .toString(),
                HuobiUtils.createHuobiCurrencyPair(limitOrder.getCurrencyPair()),
                type),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);

    return checkResult(result);
  }

  String placeHuobiMarginLimitOrder(LimitOrder limitOrder, String accountID) throws IOException {
    String type;
    if (limitOrder.getType() == OrderType.BID) {
      type = "buy-limit";
    } else if (limitOrder.getType() == OrderType.ASK) {
      type = "sell-limit";
    } else {
      throw new ExchangeException("Unsupported order type.");
    }

    HuobiOrderResult result =
        huobi.placeLimitOrder(
            new HuobiCreateOrderRequest(
                accountID,
                limitOrder
                    .getOriginalAmount()
                    .setScale(
                        getCurrencyAmountPrecision(limitOrder.getCurrencyPair()),
                        BigDecimal.ROUND_DOWN)
                    .toString(),
                limitOrder
                    .getLimitPrice()
                    .setScale(
                        getCurrencyPricePrecision(limitOrder.getCurrencyPair()),
                        BigDecimal.ROUND_DOWN)
                    .toString(),
                HuobiUtils.createHuobiCurrencyPair(limitOrder.getCurrencyPair()),
                type,
                true),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);

    return checkResult(result);
  }

  String placeHuobiMarginLimitOrder(LimitOrder limitOrder) throws IOException {
    CurrencyPair pair = limitOrder.getCurrencyPair();
    String base = HuobiUtils.createHuobiAsset(pair.base);
    String counter = HuobiUtils.createHuobiAsset(pair.counter);

    long accountID =
        ((HuobiAccountServiceRaw) exchange.getAccountService())
            .getMarginAccount(base, counter)
            .getId();

    return placeHuobiMarginLimitOrder(limitOrder, String.valueOf(accountID));
  }

  int getCurrencyAmountPrecision(CurrencyPair pair) {
    if (pair.equals(CurrencyPair.XRP_BTC)) {
      return 0;
    } else if (pair.equals(CurrencyPair.TRX_USDT)
        || pair.equals(CurrencyPair.XRP_USDT)
        || pair.equals(CurrencyPair.EOS_BTC)
        || pair.equals(CurrencyPair.HT_BTC)
        || pair.equals(CurrencyPair.DAC_BTC)) {
      return 2;
    }
    return 4;
  }

  int getCurrencyPricePrecision(CurrencyPair pair) {
    if (pair.equals(CurrencyPair.ETC_USDT)
        || pair.equals(CurrencyPair.EOS_USDT)
        || pair.equals(CurrencyPair.XRP_USDT)
        || pair.equals(CurrencyPair.OMG_USDT)
        || pair.equals(CurrencyPair.STEEM_USDT)
        || pair.equals(CurrencyPair.WICC_USDT)) {
      return 4;
    } else if (pair.equals(CurrencyPair.TRX_USDT)
        || pair.equals(CurrencyPair.BCH_BTC)
        || pair.equals(CurrencyPair.ETH_BTC)
        || pair.equals(CurrencyPair.ZEC_BTC)
        || pair.equals(CurrencyPair.LTC_BTC)
        || pair.equals(CurrencyPair.DASH_BTC)) {
      return 6;
    } else if (pair.equals(CurrencyPair.EOS_BTC)
        || pair.equals(CurrencyPair.XRP_BTC)
        || pair.equals(CurrencyPair.HT_BTC)) {
      return 8;
    } else if (pair.equals(CurrencyPair.DAC_BTC)) {
      return 10;
    }
    return 2;
  }

  String placeHuobiMarketOrder(MarketOrder marketOrder) throws IOException {
    String type;
    if (marketOrder.getType() == OrderType.BID) {
      type = "buy-market";
    } else if (marketOrder.getType() == OrderType.ASK) {
      type = "sell-market";
    } else {
      throw new ExchangeException("Unsupported order type.");
    }
    HuobiOrderResult result =
        huobi.placeMarketOrder(
            new HuobiCreateOrderRequest(
                String.valueOf(
                    ((HuobiAccountServiceRaw) exchange.getAccountService())
                        .getAccounts()[0].getId()),
                marketOrder
                    .getOriginalAmount()
                    .setScale(
                        getCurrencyAmountPrecision(marketOrder.getCurrencyPair()),
                        BigDecimal.ROUND_DOWN)
                    .toString(),
                null,
                HuobiUtils.createHuobiCurrencyPair(marketOrder.getCurrencyPair()),
                type),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
  }

  String placeHuobiMarginMarketOrder(MarketOrder marketOrder, String accountID) throws IOException {
    String type;
    if (marketOrder.getType() == OrderType.BID) {
      type = "buy-market";
    } else if (marketOrder.getType() == OrderType.ASK) {
      type = "sell-market";
    } else {
      throw new ExchangeException("Unsupported order type.");
    }

    HuobiOrderResult result =
        huobi.placeMarketOrder(
            new HuobiCreateOrderRequest(
                accountID,
                marketOrder
                    .getOriginalAmount()
                    .setScale(
                        getCurrencyAmountPrecision(marketOrder.getCurrencyPair()),
                        BigDecimal.ROUND_DOWN)
                    .toString(),
                null,
                HuobiUtils.createHuobiCurrencyPair(marketOrder.getCurrencyPair()),
                type,
                true),
            exchange.getExchangeSpecification().getApiKey(),
            HuobiDigest.HMAC_SHA_256,
            2,
            HuobiUtils.createUTCDate(exchange.getNonceFactory()),
            signatureCreator);
    return checkResult(result);
  }

  String placeHuobiMarginMarketOrder(MarketOrder marketOrder) throws IOException {
    CurrencyPair pair = marketOrder.getCurrencyPair();
    String base = HuobiUtils.createHuobiAsset(pair.base);
    String counter = HuobiUtils.createHuobiAsset(pair.counter);

    long accountID =
        ((HuobiAccountServiceRaw) exchange.getAccountService())
            .getMarginAccount(base, counter)
            .getId();

    return placeHuobiMarginMarketOrder(marketOrder, String.valueOf(accountID));
  }

  List<HuobiOrder> getHuobiOrder(String... orderIds) throws IOException {
    List<HuobiOrder> orders = new ArrayList<>();
    for (String orderId : orderIds) {
      HuobiOrderInfoResult orderInfoResult =
          huobi.getOrder(
              orderId,
              exchange.getExchangeSpecification().getApiKey(),
              HuobiDigest.HMAC_SHA_256,
              2,
              HuobiUtils.createUTCDate(exchange.getNonceFactory()),
              signatureCreator);
      orders.add(checkResult(orderInfoResult));
    }
    return orders;
  }
}
