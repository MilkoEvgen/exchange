package Validation;

public class Validator {
    public static void areRateParametersValid(String firstCode, String secondCode, String amount){

        if (firstCode == null || secondCode == null || amount == null ){
            throw new ValidationException("Одно из полей отстутсвует");
        }
        if (firstCode.length() != 3 || secondCode.length() != 3 || amount.isEmpty()){
            throw new ValidationException("Неверно введено одно из полей. Пример: from=RUB&to=BYN&amount=10");
        }
    }

    public static void areCurrencyParametersValid(String code, String name, String sign){
        if (code == null || name == null || sign == null) {
            throw new ValidationException("Одно из полей отстутсвует");
        }

        if (code.length() != 3 || name.isEmpty() || sign.isEmpty()) {
            throw new ValidationException("Неверно введено одно из полей. Пример: name=Brazilian Real&code=BRL&sign=R$");
        }
    }

    public static void areCodesValid(String codes){
        if (codes == null || codes.length() != 7) {
            throw new ValidationException("Неверно введено одно из полей. Пример: USDRUB");
        }
    }

    public static void areCodeValid(String code){
        if (code == null || code.length() != 4){
            throw new ValidationException("Неверно введено поле. Пример: USD");
        }
    }
}
