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
import java.util.Scanner;

public class Staff extends Person {
    private String password;
    private String siteID;

    private JButton warehouseButton;
    private JButton storeButton;

    public Staff(){

    }

    public Staff(String password, String siteID, String id, String name, String email, String birthDay, String phoneNum) {
        super(id, name, email, birthDay, phoneNum);
        this.password = password;
        this.siteID = siteID;
    }

    public static void updateProfileInFile(Staff loggedInStaff) {
        String filePath = "aux_files/person_txt/Person.txt";
        StringBuilder fileContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                // If this is the line for the staff with the matching ID, update the information
                if (data[1].equals(loggedInStaff.getId())) {
                    // Construct new data for this staff
                    line = String.join("|", data[0], loggedInStaff.getId(), loggedInStaff.getPassword(), loggedInStaff.getSiteID(), loggedInStaff.getName(), loggedInStaff.getEmail(), loggedInStaff.getBirthDay(), loggedInStaff.getPhoneNum());
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

    public static void loginPage(Staff[] people, Branch[] branches) {
        // Create a frame
        JFrame frame = new JFrame("Staff Login");
        frame.setSize(800, 600);  // Increased size of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
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

                boolean loginSuccess = false;  // To check if login is successful

                for (Person person : people) {
                    if (person != null && person instanceof Staff) {
                        Staff staffMember = (Staff) person;

                        // Check if the entered ID and password match
                        if (staffMember.getId().equals(enteredID) && staffMember.getPassword().equals(enteredPassword)) {
                            new Menu(staffMember, people, branches); // Open the menu window
                            loginSuccess = true;
                            break;  // Exit the loop once login is successful
                        }
                    }
                }

                if (!loginSuccess) {
                    JOptionPane.showMessageDialog(null, "Invalid ID or password. Try harder.");
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

    public static void changePassword(Menu menu, Staff loggedInStaff) {
        Scanner sc = new Scanner(System.in);

        JFrame frame = new JFrame("Staff Change Password");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        //header image
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBounds(0, 0, frame.getWidth(), 200);
        frame.add(imageLabel);

        // Info Panel with editable fields
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columns, spacing
        infoPanel.setBounds(100, 250, 600, 30); // Adjust as needed

        infoPanel.add(new JLabel("Enter the current password:"));
        JTextField passwordField = new JTextField("");
        infoPanel.add(passwordField);

        frame.add(infoPanel);

        JButton saveButton = new JButton("Enter");
        saveButton.setBounds(200, 500, 100, 30);
        frame.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (loggedInStaff.getPassword().equals(passwordField.getText())) {
                    frame.dispose();
                    Staff.enterNewPassword(menu, loggedInStaff);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Password. Try again.");
                }
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setBounds(500, 500, 100, 30);
        frame.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                Staff.profilePage(menu, loggedInStaff);
            }
        });

        frame.setVisible(true);
    }

    public static void enterNewPassword(Menu menu, Staff loggedInStaff){
        JFrame frame = new JFrame("Staff Change Password");
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        //header image
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setBounds(0, 0, frame.getWidth(), 200);
        frame.add(imageLabel);

        // Info Panel with editable fields
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(0, 2, 10, 10));
        infoPanel.setBounds(100, 250, 600, 70);

        infoPanel.add(new JLabel("Enter new password:"));
        JTextField newPwField1 = new JTextField("");
        infoPanel.add(newPwField1);

        infoPanel.add(new JLabel("Enter new password again:"));
        JTextField newPwField2 = new JTextField("");
        infoPanel.add(newPwField2);

        frame.add(infoPanel);

        JButton saveButton = new JButton("Enter");
        saveButton.setBounds(200, 500, 100, 30);
        frame.add(saveButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(newPwField1.getText().equals(newPwField2.getText())){
                    if(!newPwField1.getText().equals(loggedInStaff.getPassword())) {
                        if (Staff.validatePasswordFormat(newPwField1.getText())) {
                            loggedInStaff.setPassword(newPwField1.getText());
                            Staff.updateProfileInFile(loggedInStaff);
                            JOptionPane.showMessageDialog(frame, "Password changed successfully! Please login again");
                            frame.dispose();
                            Staff.loginPage();
                            return;
                        } else {
                            JOptionPane.showMessageDialog(frame, "Invalid format. Password must contain at least one upper character, one lower character, one special character, and one digit with no space.");
                        }
                    }else{
                        JOptionPane.showMessageDialog(frame, "New password cannot same with current password");
                    }
                }else{
                    JOptionPane.showMessageDialog(frame, "Please enter the same new password");
                }
            }
        });

        JButton backButton = new JButton("Back");
        backButton.setBounds(500, 500, 100, 30);
        frame.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                Staff.profilePage(menu, loggedInStaff);
            }
        });

        frame.setVisible(true);
    }

    private static boolean validatePasswordFormat(String pw){
        char[] pwArray = pw.toCharArray();

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for(char c : pwArray){
            if(Character.isUpperCase(c)){
                hasUpperCase = true;
            }else if(Character.isLowerCase(c)){
                hasLowerCase = true;
            }else if(Character.isDigit(c)){
                hasDigit = true;
            }else if(!Character.isLetterOrDigit(c)){
                hasSpecialChar = true;
            }else if(Character.isWhitespace(c)){
                return false;
            }
        }
        if (hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar) {
            return true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    public static void profilePage(Menu menu, Staff loggedInStaff) {
        JFrame frame = new JFrame("Staff Profile");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Header image
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
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
        JTextField idField = new JTextField(loggedInStaff.getId());
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

        infoPanel.add(new JLabel("Site ID:"));
        JTextField siteField = new JTextField(loggedInStaff.getSiteID());
        siteField.setEditable(false); // Site shouldn't be edited
        infoPanel.add(siteField);

        frame.add(infoPanel);

        // Save Button
        JButton saveButton = new JButton("Save");
        saveButton.setBounds(200, 500, 100, 30);
        frame.add(saveButton);

        JButton editPWButton = new JButton("Change Password");
        editPWButton.setBounds(320, 500, 150, 30);
        frame.add(editPWButton);

        editPWButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                Staff.changePassword(menu, loggedInStaff);
            }
        });

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }
}