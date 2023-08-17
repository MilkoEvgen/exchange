package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    public static void setResponse(HttpServletResponse resp, Object object) {
        try (PrintWriter writer = resp.getWriter()) {
            String result = objectMapper.writeValueAsString(object);
            writer.print(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
