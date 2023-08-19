package servlets;

import Validation.CurrencyNotFoundException;
import Validation.ValidationException;
import Validation.Validator;
import model.ExchangeDto;
import service.ExchangeService;
import utils.ResponseUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange/*")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeService service = new ExchangeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

        try {
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            String strAmount = req.getParameter("amount");
            Validator.areRateParametersValid(from, to, strAmount);
            BigDecimal amount = new BigDecimal(strAmount);
            Optional<ExchangeDto> exchangeDto = service.getExchange(from, to, amount);
            if (exchangeDto.isEmpty()){
                ResponseUtils.setResponse(resp, "Курс валют " + from + "/" + to + " не найден", 404);
            } else {
                ResponseUtils.setResponse(resp, exchangeDto.get());
            }
        }
        catch (SQLException e) {
            resp.setStatus(500);
        } catch (CurrencyNotFoundException e){
            ResponseUtils.setResponse(resp, e.getMessage(), 404);
        } catch (ValidationException e){
            ResponseUtils.setResponse(resp, e.getMessage(), 400);
        }
    }
}
