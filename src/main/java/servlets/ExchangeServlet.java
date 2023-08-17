package servlets;

import Validation.Validator;
import service.ExchangeService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService service = new ExchangeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String from = req.getParameter("from");
        String to = req.getParameter("to");
        String strAmount = req.getParameter("amount");
        Validator.areRateParametersValid(from, to, strAmount);
        BigDecimal amount = new BigDecimal(strAmount);
        try {
            service.getExchange(resp, from, to, amount);
        }
        catch (SQLException e) {
            resp.setStatus(500);
        }
    }
}
