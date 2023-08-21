package servlets;

import Validation.CurrencyNotFoundException;
import Validation.ValidationException;
import Validation.Validator;
import dao.CurrencyDbStorage;
import dao.ExchangeRateDbStorage;
import model.ExchangeRate;
import utils.ResponseUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateDbStorage storage = new ExchangeRateDbStorage();
    private final CurrencyDbStorage currencyStorage = new CurrencyDbStorage();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<ExchangeRate> rates = storage.getAllRates();
            if (!rates.isEmpty()){
                ResponseUtils.setResponse(resp, rates);
            } else {
                resp.setStatus(404);
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rateString = req.getParameter("rate");
        try {
            Validator.areRateParametersValid(baseCode, targetCode, rateString);
            currencyStorage.getCurrencyByCode(baseCode);
            currencyStorage.getCurrencyByCode(targetCode);
            BigDecimal rate = new BigDecimal(rateString);
            Optional<ExchangeRate> optionalRate = storage.postRate(baseCode, targetCode, rate);
            if (optionalRate.isPresent()){
                ResponseUtils.setResponse(resp, optionalRate.get());
            } else {
                resp.setStatus(500);
            }
        } catch (ValidationException e) {
            ResponseUtils.setResponse(resp, e.getMessage(), 400);
        } catch (CurrencyNotFoundException e){
            ResponseUtils.setResponse(resp, e.getMessage(), 404);
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                ResponseUtils.setResponse(resp, "Обменный курс " + baseCode + "/" + targetCode
                        + " уже существует", 409);
            } else {
                resp.setStatus(500);
            }
        }
    }
}
