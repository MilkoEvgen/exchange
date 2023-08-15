package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class ResponseUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    public static void setResponse(HttpServletResponse resp, Object object) throws IOException {
        String result = objectMapper.writeValueAsString(object);
        try (PrintWriter writer = resp.getWriter()) {
            writer.print(result);
        }
    }

    public static void setResponseWithAmount(HttpServletResponse resp, Object object, BigDecimal amount, BigDecimal convertedAmount) throws IOException {
        ObjectNode jsonNode = objectMapper.valueToTree(object);
        jsonNode.remove("id");
        jsonNode.put("amount", amount);
        jsonNode.put("convertedAmount", convertedAmount);
        String result = objectMapper.writeValueAsString(jsonNode);
        try (PrintWriter writer = resp.getWriter()) {
            writer.print(result);
        }
    }
}
