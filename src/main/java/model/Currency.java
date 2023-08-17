package model;

import lombok.Data;

@Data
public class Currency {
    private int id;
    private String code;
    private String full_name;
    private String sign;
}
