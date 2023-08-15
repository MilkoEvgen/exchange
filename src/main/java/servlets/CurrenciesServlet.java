package servlets;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> currencies = storage.getAllCurrencies();
            if (!currencies.isEmpty()){
                ResponseUtils.setResponse(resp, currencies);
            } else {
                resp.setStatus(404);
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");
        if (code == null || code.isEmpty() || name == null || name.isEmpty() || sign == null || sign.isEmpty()) {
            resp.setStatus(400);
            return;
        }
        try {
            Optional <Currency> currencyExists = storage.getCurrencyByCode(code);
            if (currencyExists.isPresent()){
                resp.setStatus(409);
                return;
            }
            Optional<Currency> optionalCurrency = storage.postCurrency(code, name, sign);
            if (optionalCurrency.isPresent()){
                ResponseUtils.setResponse(resp, optionalCurrency.get());
            } else {
                resp.setStatus(500);
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        }
    }
}
