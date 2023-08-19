package servlets;

import Validation.CurrencyNotFoundException;
import Validation.ValidationException;
import Validation.Validator;
import dao.CurrencyDbStorage;
import model.Currency;
import utils.ResponseUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyDbStorage storage = new CurrencyDbStorage();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String code = req.getPathInfo();
            Validator.areCodeValid(code);
            code = code.substring(1);
            Currency currency = storage.getCurrencyByCode(code);
            ResponseUtils.setResponse(resp, currency);
        } catch (SQLException e) {
            resp.setStatus(500);
        } catch (ValidationException e) {
            ResponseUtils.setResponse(resp, e.getMessage(), 400);
        } catch (CurrencyNotFoundException e) {
            ResponseUtils.setResponse(resp, e.getMessage(), 404);
        }
    }
}
