public class Supplier extends Person {
    private String ID;
    private String Address;
    private String productBrand;

    public Supplier(String name, String email, String birthDay, String phoneNum, String ID, String Address, String productBrand){
        super(name, email, birthDay, phoneNum);
        this.ID = ID;
        this.Address = Address;
        this.productBrand = productBrand;
    }

}
