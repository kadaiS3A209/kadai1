package model; // パッケージは適宜合わせてください

import java.io.Serializable;

public class DiseaseBean implements Serializable{
    private String code;
    private String name;

    public DiseaseBean(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
}
