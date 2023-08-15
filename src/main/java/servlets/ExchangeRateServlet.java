package servlets;

import Validation.ValidationException;
import Validation.Validator;
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
    private final ExchangeRateDbStorage storage = new ExchangeRateDbStorage();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, ServletException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codes = req.getPathInfo();
        if (codes == null || codes.length() != 7) {
            resp.setStatus(400);
            return;
        }
        String baseCode = codes.substring(1, 4);
        String targetCode = codes.substring(4, 7);
        try {
            Optional<ExchangeRate> rateOptional = storage.getRateByCodes(baseCode, targetCode);
            if (rateOptional.isPresent()){
                ResponseUtils.setResponse(resp, rateOptional.get());
            } else {
                resp.setStatus(404);
            }
        } catch (SQLException e) {
            resp.setStatus(500);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codes = req.getPathInfo();
        if (codes == null || codes.length() != 7) {
            resp.setStatus(400);
            return;
        }
        String baseCode = codes.substring(1, 4);
        String targetCode = codes.substring(4, 7);
        String rateCurrency = req.getParameter("rate");
        try {
            Validator.areParametersValid(baseCode, targetCode, rateCurrency);
            BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateCurrency));
            Optional<ExchangeRate> optionalRate = storage.patchRate(baseCode, targetCode, rate);
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
