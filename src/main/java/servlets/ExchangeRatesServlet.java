package servlets;

import Validation.ValidationException;
import Validation.Validator;
import dao.ExchangeRateDbStorage;
import model.ExchangeRate;
import utils.ResponseUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateDbStorage storage = new ExchangeRateDbStorage();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCode = req.getParameter("baseCurrencyCode");
        String targetCode = req.getParameter("targetCurrencyCode");
        String rateString = req.getParameter("rate");
        try {
            Validator.areRateParametersValid(baseCode, targetCode, rateString);
            BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateString));
            Optional<ExchangeRate> rateExists = storage.getRateByCodes(baseCode, targetCode);
            if (rateExists.isPresent()){
                resp.setStatus(409);
                return;
            }
            Optional<ExchangeRate> optionalRate = storage.postRate(baseCode, targetCode, rate);
            if (optionalRate.isPresent()){
                ResponseUtils.setResponse(resp, optionalRate.get());
            } else {
                resp.setStatus(500);
            }
        } catch (ValidationException e) {
            resp.setStatus(400);
            ResponseUtils.setResponse(resp, e.getMessage());
        } catch (SQLException e) {
            resp.setStatus(500);
        }
    }
}
