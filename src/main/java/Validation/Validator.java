package Validation;

import javax.servlet.http.HttpServletResponse;

public class Validator {
    public static void areRateParametersValid(String firstCode, String secondCode, String amount){

        if (firstCode == null || secondCode == null || amount == null ){
            throw new ValidationException("Отсутствует или неверно введено нужное поле формы");
        }
        if (firstCode.length() != 3 || secondCode.length() != 3 || amount.isEmpty()){
            throw new ValidationException("Отсутствует или неверно введено нужное поле формы");
        }
    }

    public static void areCurrencyParametersValid(String code, String name, String sign){
        if (code == null || name == null || sign == null) {
            throw new ValidationException("Отсутствует или неверно введено нужное поле формы");
        }

        if (code.isEmpty() || name.isEmpty() || sign.isEmpty()) {
            throw new ValidationException("Отсутствует или неверно введено нужное поле формы");
        }
    }

    public static void areCodesValid(HttpServletResponse resp, String codes){
        if (codes == null || codes.length() != 7) {
            resp.setStatus(400);
        }
    }
}
