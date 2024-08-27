import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

    public void createAndShowGUI() {
        // Create a frame
        JFrame frame = new JFrame("Staff Login");
        frame.setSize(800, 600);  // Increased size of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

            ImageIcon imageIcon = new ImageIcon("header2.png");
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBounds(0, 0, frame.getWidth(), 200);
            frame.add(imageLabel);

        // Create login components
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("Login");

        // Position login components
        idLabel.setBounds(250, 250, 80, 25);
        idField.setBounds(330, 250, 160, 25);
        passwordLabel.setBounds(250, 280, 80, 25);
        passwordField.setBounds(330, 280, 160, 25);
        loginButton.setBounds(330, 320, 80, 25);

        // Add login components to the frame
        frame.add(idLabel);
        frame.add(idField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(loginButton);

        // Login button action listener
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String enteredID = idField.getText();
                String enteredPassword = new String(passwordField.getPassword());

                try {
                    BufferedReader reader = new BufferedReader(new FileReader("Person.txt"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.split(",");
                        // Check if the entered ID and password match
                        if (data.length >= 4 && data[1].equals(enteredID) && data[2].equals(enteredPassword)) {
                            site = data[3];
                            frame.dispose(); // Close the login window
                            new Menu(data[1], data[4]); // Open the menu window, pass staffID(data[1]) and staffName(data[4])
                            return;
                        }
                    }
                    reader.close();
                    JOptionPane.showMessageDialog(null, "Invalid ID or password.");
                } catch (IOException ex) {
                    System.err.println("Error reading staff data: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Error occurred during login.");
                }
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Staff staff = new Staff("John Doe", "john@example.com", "01/01/1980", "123-456-7890", "ID123", "password", "");
        staff.createAndShowGUI();
    }
}