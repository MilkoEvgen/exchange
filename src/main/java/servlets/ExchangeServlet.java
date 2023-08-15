package servlets;

import Validation.ValidationException;
import Validation.Validator;
import dao.CurrencyDbStorage;
import dao.ExchangeRateDbStorage;
import model.Currency;
import model.ExchangeRate;
import utils.ResponseUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateDbStorage rateStorage = new ExchangeRateDbStorage();
    private final CurrencyDbStorage currencyStorage = new CurrencyDbStorage();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String strAmount = req.getParameter("amount");
        try {
            Validator.areParametersValid(from, to, strAmount);
            areCurrencyExists(resp, from);
            areCurrencyExists(resp, to);
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(strAmount));
            Optional<ExchangeRate> optionalRate = rateStorage.getRateByCodes(from, to);
            if (optionalRate.isPresent()) {
                ExchangeRate exchangeRate = optionalRate.get();
                BigDecimal converted = amount.multiply(exchangeRate.getRate()).setScale(2, RoundingMode.HALF_UP);
                ResponseUtils.setResponseWithAmount(resp, exchangeRate, amount, converted);
                return;
            }
            optionalRate = rateStorage.getRateByCodes(to, from);
            if (optionalRate.isPresent()) {
                BigDecimal rate = optionalRate.get().getRate();
                BigDecimal converted = amount.divide(rate, 2, RoundingMode.HALF_UP);
                ExchangeRate exchangeRate = createNewRate(from, to, rate);
                ResponseUtils.setResponseWithAmount(resp, exchangeRate, amount, converted);
                return;
            }
            Optional<ExchangeRate> usdFrom = rateStorage.getRateByCodes("USD", from);
            Optional<ExchangeRate> usdTo = rateStorage.getRateByCodes("USD", to);
            if (usdFrom.isPresent() && usdTo.isPresent()){
                BigDecimal rate = BigDecimal.ONE.divide(usdFrom.get().getRate(), 6, RoundingMode.HALF_UP)
                        .multiply(usdTo.get().getRate()).setScale(6, RoundingMode.HALF_UP);
                BigDecimal converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
                ExchangeRate exchangeRate = createNewRate(from, to, rate);
                ResponseUtils.setResponseWithAmount(resp, exchangeRate, amount, converted);
            } else {
                resp.setStatus(404);
                ResponseUtils.setResponse(resp, "Курс валют " + from + "/" + to + " не найден");
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        } catch (ValidationException e) {
            resp.setStatus(400);
            ResponseUtils.setResponse(resp, e.getMessage());
        }
    }

    private void areCurrencyExists(HttpServletResponse resp, String code) throws SQLException, IOException {
        Optional<Currency> optionalCurrency = currencyStorage.getCurrencyByCode(code);
        if (optionalCurrency.isEmpty()){
            resp.setStatus(404);
            ResponseUtils.setResponse(resp, code + " : Валюта не найдена");
        }
    }

    private ExchangeRate createNewRate(String from, String to, BigDecimal rate) throws SQLException {
        Optional<Currency> currencyFrom = currencyStorage.getCurrencyByCode(from);
        Optional<Currency> currencyTo = currencyStorage.getCurrencyByCode(to);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setBaseCurrency(currencyFrom.get());
        exchangeRate.setTargetCurrency(currencyTo.get());
        exchangeRate.setRate(rate);
        return exchangeRate;
    }
}
