package service;

import dao.CurrencyDbStorage;
import dao.ExchangeRateDbStorage;
import model.Currency;
import model.ExchangeDto;
import model.ExchangeRate;
import utils.ResponseUtils;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {
    private final ExchangeRateDbStorage rateStorage = new ExchangeRateDbStorage();
    private final CurrencyDbStorage currencyStorage = new CurrencyDbStorage();

    public void getExchange(HttpServletResponse resp, String from, String to, BigDecimal amount) throws SQLException {
        Currency currencyFrom = getCurrency(resp, from);
        Currency currencyTo = getCurrency(resp, to);

        Optional<ExchangeRate> optionalRate = rateStorage.getRateByCodes(from, to);
        if (optionalRate.isPresent()) {
            BigDecimal rate = optionalRate.get().getRate();
            BigDecimal converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            ExchangeDto exchangeDto = new ExchangeDto(currencyFrom, currencyTo, rate, amount, converted);
            ResponseUtils.setResponse(resp, exchangeDto);
            return;
        }
        Optional<ExchangeRate> InverseOptionalRate = rateStorage.getRateByCodes(to, from);
        if (InverseOptionalRate.isPresent()) {
            BigDecimal rate = InverseOptionalRate.get().getRate();
            BigDecimal converted = amount.divide(rate, 2, RoundingMode.HALF_UP);
            ExchangeDto exchangeDto = new ExchangeDto(currencyFrom, currencyTo, rate, amount, converted);
            ResponseUtils.setResponse(resp, exchangeDto);
            return;
        }
        Optional<ExchangeRate> usdFrom = rateStorage.getRateByCodes("USD", from);
        Optional<ExchangeRate> usdTo = rateStorage.getRateByCodes("USD", to);
        if (usdFrom.isPresent() && usdTo.isPresent()){
            BigDecimal rate = BigDecimal.ONE.divide(usdFrom.get().getRate(), 6, RoundingMode.HALF_UP)
                    .multiply(usdTo.get().getRate()).setScale(6, RoundingMode.HALF_UP);
            BigDecimal converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            ExchangeDto exchangeDto = new ExchangeDto(currencyFrom, currencyTo, rate, amount, converted);
            ResponseUtils.setResponse(resp, exchangeDto);
        } else {
            resp.setStatus(404);
            ResponseUtils.setResponse(resp, "Курс валют " + from + "/" + to + " не найден");
        }
    }

    private Currency getCurrency(HttpServletResponse resp, String code){
        try {
            Optional<Currency> optionalCurrency = currencyStorage.getCurrencyByCode(code);
            if (optionalCurrency.isEmpty()){
                resp.setStatus(404);
                ResponseUtils.setResponse(resp, code + " : Валюта не найдена");
            } else {
                return optionalCurrency.get();
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        }
        return new Currency();
    }
}
