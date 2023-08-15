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
import java.util.Optional;

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyDbStorage storage = new CurrencyDbStorage();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getPathInfo();
        if (code == null || code.length() != 4) {
            resp.setStatus(400);
            return;
        }
        code = code.substring(1);
        try {
            Optional <Currency> currencyOptional = storage.getCurrencyByCode(code);
            if (currencyOptional.isPresent()){
                ResponseUtils.setResponse(resp, currencyOptional.get());
            } else {
                resp.setStatus(404);
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        }
    }
}
