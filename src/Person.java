public class Person {
    protected String name;
    protected String email;
    protected String birthDay;
    protected String phoneNum;

    public Person(){

    }

    public Person(String name, String email, String birthDay, String phoneNum) {
        this.name = name;
        this.email = email;
        this.birthDay = birthDay;
        this.phoneNum = phoneNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

}
