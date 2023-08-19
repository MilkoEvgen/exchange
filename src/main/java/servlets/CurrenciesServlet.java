package servlets;

import Validation.ValidationException;
import Validation.Validator;
import dao.CurrencyDbStorage;
import model.Currency;
import utils.ResponseUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "Currencies", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyDbStorage storage = new CurrencyDbStorage();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<Currency> currencies = storage.getAllCurrencies();
            if (!currencies.isEmpty()) {
                ResponseUtils.setResponse(resp, currencies);
            } else {
                resp.setStatus(404);
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");
        try {
            Validator.areCurrencyParametersValid(code, name, sign);
            Optional<Currency> optionalCurrency = storage.postCurrency(code, name, sign);
            if (optionalCurrency.isPresent()) {
                ResponseUtils.setResponse(resp, optionalCurrency.get());
            } else {
                resp.setStatus(500);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                ResponseUtils.setResponse(resp, "Валюта с кодом " + code + " уже существует", 409);
            } else {
                resp.setStatus(500);
            }
        } catch (ValidationException e) {
            ResponseUtils.setResponse(resp, e.getMessage(), 400);
        }
    }
}
