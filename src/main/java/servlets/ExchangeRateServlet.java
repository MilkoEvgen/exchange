package servlets;

import Validation.CurrencyNotFoundException;
import Validation.ValidationException;
import Validation.Validator;
import dao.CurrencyDbStorage;
import dao.ExchangeRateDbStorage;
import model.ExchangeRate;
import utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name="ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final CurrencyDbStorage currencyStorage = new CurrencyDbStorage();
    private final ExchangeRateDbStorage exchangeRateStorage = new ExchangeRateDbStorage();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String codes = req.getPathInfo();
            Validator.areCodesValid(codes);
            String baseCode = codes.substring(1, 4);
            String targetCode = codes.substring(4, 7);
            currencyStorage.getCurrencyByCode(baseCode);
            currencyStorage.getCurrencyByCode(targetCode);
            Optional<ExchangeRate> rateOptional = exchangeRateStorage.getRateByCodes(baseCode, targetCode);
            if (rateOptional.isPresent()){
                ResponseUtils.setResponse(resp, rateOptional.get());
            } else {
                ResponseUtils.setResponse(resp, "Обменный курс " + baseCode + "/" + targetCode +
                        " не найден", 404);
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        } catch (ValidationException e) {
            ResponseUtils.setResponse(resp, e.getMessage(), 400);
        } catch (CurrencyNotFoundException e){
            ResponseUtils.setResponse(resp, e.getMessage(), 404);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String codes = req.getPathInfo();
            Validator.areCodesValid(codes);
            String baseCode = codes.substring(1, 4);
            String targetCode = codes.substring(4, 7);
            currencyStorage.getCurrencyByCode(baseCode);
            currencyStorage.getCurrencyByCode(targetCode);
            String rateCurrency = req.getParameter("rate");
            Validator.areRateParametersValid(baseCode, targetCode, rateCurrency);
            BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateCurrency));
            Optional<ExchangeRate> optionalRate = exchangeRateStorage.getRateByCodes(baseCode, targetCode);
            if (optionalRate.isEmpty()){
                ResponseUtils.setResponse(resp, "Валютная пара отсутствует в базе данных", 404);
                return;
            }
            optionalRate = exchangeRateStorage.patchRate(baseCode, targetCode, rate);
            if (optionalRate.isPresent()){
                ResponseUtils.setResponse(resp, optionalRate.get());
            } else {
                resp.setStatus(500);
            }
        } catch (ValidationException e) {
            ResponseUtils.setResponse(resp, e.getMessage(), 400);
        } catch (SQLException e) {
            resp.setStatus(500);
        } catch (CurrencyNotFoundException e){
            ResponseUtils.setResponse(resp, e.getMessage(), 404);
        }
    }
}
