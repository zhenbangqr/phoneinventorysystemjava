public class Main {
    public static void main(String[] args) {
        Person person = new Person();
        Person[] people = person.createPersonArray();

        Inventory inventory = new Inventory();
        Branch[] branches = inventory.createBranchArray();
        Staff.loginPage(people, branches);
        //ReadFromFile.main(args);
    }
}