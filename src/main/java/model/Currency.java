package model;

import lombok.Data;

@Data
public class Currency {
    private int id;
    private String name;
    private String code;
    private String sign;
}
