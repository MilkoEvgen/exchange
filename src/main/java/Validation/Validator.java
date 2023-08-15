package Validation;

public class Validator {
    public static void areParametersValid(String firstCode, String secondCode, String amount){

        if (firstCode == null || firstCode.length() != 3 || secondCode == null || secondCode.length() != 3 ||
            amount == null || amount.isEmpty()){
            throw new ValidationException("Отсутствует или неверно введено нужное поле формы");
        }
    }

}
