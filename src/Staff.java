public class Staff extends Person {
    private String ID;
    private String password;
    private String site;//Staff for Warehouse or Store

    public Staff(String name, String email, String birthDay, String phoneNum, String ID, String password, String site) {
        super(name, email, birthDay, phoneNum);
        this.ID = ID;
        this.password = password;
        this.site = site;
    }


}
