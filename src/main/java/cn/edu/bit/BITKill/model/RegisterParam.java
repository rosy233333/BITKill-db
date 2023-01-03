package cn.edu.bit.BITKill.model;

public class RegisterParam {

    private final String type = "register";

    private String username;

    private String password;

    public RegisterParam() {
    }

    public RegisterParam(String name, String password, String sex, String age) {
        this.username = name;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
