public class Main {
    public static void main(String[] args) {
        Person person = new Person();
        Inventory inventory = new Inventory();

        Person[] people = person.createPersonArray();
        Branch[] branches = inventory.createBranchArray();
        Staff.loginPage(people, branches);
        //ReadFromFile.main(args);
    }
}