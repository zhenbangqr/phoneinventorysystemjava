import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Staff extends Person {
    private String ID;
    private String password;
    private String site; // Staff for Warehouse or Store
    private JRadioButton warehouseRadioButton;
    private JRadioButton storeRadioButton;
    private ButtonGroup siteGroup;

    public Staff(String name, String email, String birthDay, String phoneNum, String ID, String password, String site) {
        super(name, email, birthDay, phoneNum);
        this.ID = ID;
        this.password = password;
        this.site = site;
    }

    public void createAndShowGUI() {
        // Create a frame
        JFrame frame = new JFrame("Workplace Selection");
        frame.setSize(600, 400);  // Frame size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Create radio buttons
        warehouseRadioButton = new JRadioButton("Warehouse");
        storeRadioButton = new JRadioButton("Store");

        // Group the radio buttons
        siteGroup = new ButtonGroup();
        siteGroup.add(warehouseRadioButton);
        siteGroup.add(storeRadioButton);

        // Set radio button bounds (position and size)
        warehouseRadioButton.setBounds(150, 100, 120, 50);
        storeRadioButton.setBounds(330, 100, 120, 50);

        // Add action listeners
        warehouseRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                site = "Warehouse";
                JOptionPane.showMessageDialog(null, "User works at Warehouse.");
            }
        });

        storeRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                site = "Store";
                JOptionPane.showMessageDialog(null, "User works at Store.");
            }
        });

        // Add radio buttons to the frame
        frame.add(warehouseRadioButton);
        frame.add(storeRadioButton);

        // Display the frame
        frame.setVisible(true);
    }
}
