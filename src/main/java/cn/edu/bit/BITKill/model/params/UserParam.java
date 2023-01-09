package cn.edu.bit.BITKill.model.params;

// 用于register和login接口
public class UserParam {
    private String username;

    private String password;

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

    public UserParam() {
        this.username = "default user";
        this.password = "123456";
    }

    public UserParam(String username, String password) {
        this.username = username;
        this.password = password;
    }
}