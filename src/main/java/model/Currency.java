package model;

public class Currency {
    private int id;
    private String code;
    private String full_name;
    private String sign;

    public Currency() {
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getSign() {
        return sign;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
