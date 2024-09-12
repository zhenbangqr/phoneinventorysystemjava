import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Staff extends Person {
    private String ID;
    private String password;
    private String site;
    private JButton warehouseButton;
    private JButton storeButton;

    public Staff(){

    }

    public Staff(String ID, String password, String site, String name, String email, String birthDay, String phoneNum) {
        super(name, email, birthDay, phoneNum);
        this.ID = ID;
        this.password = password;
        this.site = site;
    }

    public static void updateProfileInFile(Staff loggedInStaff) {
        String filePath = "Person.txt";
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                // If this is the line for the staff with the matching ID, update the information
                if (data[1].equals(loggedInStaff.getID())) {
                    // Construct new data for this staff
                    line = String.join(",", data[0], loggedInStaff.getID(), loggedInStaff.getPassword(), loggedInStaff.getSite(), loggedInStaff.getName(), loggedInStaff.getEmail(), loggedInStaff.getBirthDay(), loggedInStaff.getPhoneNum());
                }
                fileContent.append(line).append("\n");
            }
        } catch (IOException ex) {
            System.err.println("Error reading staff data: " + ex.getMessage());
        }

        // Write the updated content back to the file
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            writer.write(fileContent.toString());
        } catch (IOException ex) {
            System.err.println("Error writing updated staff data: " + ex.getMessage());
        }
    }


    public void checkBirthday() {

        // Cut the date to DD/MM only
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        String todayDM = dateFormat.format(new Date());
        String birthdayDM = getBirthDay().substring(0, 5);

        // Check birthday
        if (todayDM.equals(birthdayDM)) {
            JOptionPane pane = new JOptionPane("Happy Birthday, " + getName() + "!", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, null, null);
            JDialog dialog = pane.createDialog(null, "Birthday");

            // Not modal = Not hiding menu page
            dialog.setModal(false);
            dialog.setVisible(true);
        }
    }

    public static void loginPage() {
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
                        // Check if the entered ID and password match, considering the new format
                        if (data.length >= 7 && data[1].equals(enteredID) && data[2].equals(enteredPassword)) {
                            frame.dispose(); // Close the login window
                            Staff loggedInStaff = new Staff(data[1], data[2], data[3], data[4], data[5], data[6], data[7]);
                            loggedInStaff.checkBirthday(); // Check for birthday after successful login
                            new Menu(loggedInStaff); // Open the menu window
                            return;
                        }
                    }
                    reader.close();
                    JOptionPane.showMessageDialog(null, "Invalid ID or password. Try harder.");
                } catch (IOException ex) {
                    System.err.println("Error reading staff data: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "Error occurred during login.");
                }
            }
        });

        // Click on the login button after press enter
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });

        frame.setVisible(true);
    }

    public static void profilePage(Menu menu, Staff loggedInStaff) {
        JFrame frame = new JFrame("Staff Profile");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Header image
        ImageIcon imageIcon = new ImageIcon("header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBounds(0, 0, frame.getWidth(), 200);
        frame.add(imageLabel);

        // Info Panel with editable fields
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columns, spacing
        infoPanel.setBounds(100, 250, 600, 200); // Adjust as needed

        // Labels and editable fields
        infoPanel.add(new JLabel("Staff ID:"));
        JTextField idField = new JTextField(loggedInStaff.getID());
        idField.setEditable(false); // ID shouldn't be edited
        infoPanel.add(idField);

        infoPanel.add(new JLabel("Name:"));
        JTextField nameField = new JTextField(loggedInStaff.getName());
        infoPanel.add(nameField);

        infoPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField(loggedInStaff.getEmail());
        infoPanel.add(emailField);

        infoPanel.add(new JLabel("Date of Birth:"));
        JTextField dobField = new JTextField(loggedInStaff.getBirthDay());
        infoPanel.add(dobField);

        infoPanel.add(new JLabel("Phone:"));
        JTextField phoneField = new JTextField(loggedInStaff.getPhoneNum());
        infoPanel.add(phoneField);

        infoPanel.add(new JLabel("Site:"));
        JTextField siteField = new JTextField(loggedInStaff.getSite());
        siteField.setEditable(false); // Site shouldn't be edited
        infoPanel.add(siteField);

        frame.add(infoPanel);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(200, 500, 100, 30);
        frame.add(saveButton);

        // Add action listener to Save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String dob = dobField.getText();
                String phone = phoneField.getText();

                // Validate name
                if (name.isEmpty() || !name.matches("[a-zA-Z\\s]+")) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid name (letters only).");
                    return;
                }

                // Validate email using a simple regex pattern
                if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid email.");
                    return;
                }

                // Validate date of birth (e.g., dd/MM/yyyy format)
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                try {
                    LocalDate parsedDob = LocalDate.parse(dob, dateFormatter);

                    // Check if the year is within a reasonable range (e.g., not in the future)
                    if (parsedDob.isAfter(LocalDate.now())) {
                        JOptionPane.showMessageDialog(frame, "Date of birth cannot be in the future.");
                        return;
                    }

                    // Check if the parsed date matches the original input to catch invalid dates like 29/02/2023
                    String formattedDob = parsedDob.format(dateFormatter);
                    if (!formattedDob.equals(dob)) {
                        JOptionPane.showMessageDialog(frame, "Please enter a valid date of birth (dd/MM/yyyy).");
                        return;
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid date of birth (dd/MM/yyyy).");
                    return;
                }

                // Validate phone number (digits only, length check)
                if (!phone.matches("\\d{10,11}")) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid phone number (10/11 digits).");
                    return;
                }

                loggedInStaff.setName(nameField.getText());
                loggedInStaff.setEmail(emailField.getText());
                loggedInStaff.setBirthDay(dobField.getText());
                loggedInStaff.setPhoneNum(phoneField.getText());

                // Call method to update the file
                updateProfileInFile(loggedInStaff);

                JOptionPane.showMessageDialog(frame, "Profile updated successfully!");
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setBounds(500, 500, 100, 30);
        frame.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        frame.setVisible(true);
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSite(){
        return site;
    }

    public void setSite(String site){
        this.site = site;
    }
}