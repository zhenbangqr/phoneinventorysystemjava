import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Staff extends Person {
    private String ID;
    private String password;
    private String site; // Staff for Warehouse or Store
    private JButton warehouseButton;
    private JButton storeButton;

    public Staff(String name, String email, String birthDay, String phoneNum, String ID, String password, String site) {
        super(name, email, birthDay, phoneNum);
        this.ID = ID;
        this.password = password;
        this.site = site;
    }

    public void chooseWorkPlace() {
        String[] options = {"Warehouse", "Store"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Where do you work?",
                "Workplace Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            site = "Warehouse";
        } else if (choice == 1) {
            site = "Store";
        } else {
            System.out.println("No Selection made.");
        }
    }

    public void createAndShowGUI() {
        // Create a frame
        JFrame frame = new JFrame("Workplace Selection");
        frame.setSize(600, 400);  // Increased size of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Create buttons
        warehouseButton = new JButton("Warehouse");
        storeButton = new JButton("Store");

        // Set button bounds (position and size)
        warehouseButton.setBounds(150, 100, 120, 50);  // Increased button size and adjusted position
        storeButton.setBounds(330, 100, 120, 50);      // Increased button size and adjusted position

        // Add action listeners
        warehouseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "User works at Warehouse.");
                site = "Warehouse";
            }
        });

        storeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "User works at Store.");
                site = "Store";
            }
        });

        // Add buttons to the frame
        frame.add(warehouseButton);
        frame.add(storeButton);

        // Display the frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Create a Staff instance and show the GUI
        Staff staff = new Staff("John Doe", "john@example.com", "01/01/1980", "123-456-7890", "ID123", "password", "");
        staff.createAndShowGUI();
    }
}
