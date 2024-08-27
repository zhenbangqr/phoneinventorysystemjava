import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CustomFileReader {
    private String fileName;

    public CustomFileReader(String fileName) {
        this.fileName = fileName;
    }

    public List<Staff> readStaff() {
        List<Staff> staffList = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equalsIgnoreCase("Staff") && data.length == 8) {
                    String staffID = data[1];
                    String password = data[2];
                    String site = data[3];
                    String name = data[4];
                    String email = data[5];
                    String birthDay = data[6];
                    String phoneNum = data[7];

                    Staff staff = new Staff(name, email, birthDay, phoneNum, staffID, password, site);
                    staffList.add(staff);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the staff file: " + e.getMessage());
        }

        return staffList;
    }
}
