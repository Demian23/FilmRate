package by.wtj.filmrate.bean;

import java.util.Objects;

public class NewUser{
    public NewUser(){}
    public NewUser(String name, String password, String newMail){
        userName = name; newPassword = password; mail = newMail;
    }
    private String userName;
    private String newPassword;
    private String mail;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public int hashCode(){
       return Objects.hash(userName, newPassword, mail);
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || o.getClass() != getClass()){
            return false;
        }

        NewUser newUser = (NewUser) o;
        return newUser.newPassword.equals(newPassword) && newUser.userName.equals(userName)
                && newUser.mail.equals(mail);
    }

    @Override
    public String toString(){
        return getClass().getName() +
                String.format("\nUser name: %s\nUser password: %s\nUser mail: %s", userName, newPassword, mail);
    }
}
