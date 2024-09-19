import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    public Menu(Staff loggedInStaff, Branch currentBranch, Person[] people, Branch[] branches) {
        setTitle("Staff Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Banner
        ImageIcon imageIcon = new ImageIcon("aux_files/images/warehouseheader.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        add(imageLabel, BorderLayout.NORTH);

        // Welcome message box
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInStaff.getId() + " " + loggedInStaff.getName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel welcomePanel = new JPanel(new FlowLayout());
        welcomePanel.add(welcomeLabel);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(welcomePanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 20, 20));

        JButton menuButton4;
        JButton menuButton5;
        JButton menuButton6 = null;

        JButton menuButton1 = new JButton("Display available stock");
        JButton menuButton2 = new JButton("Display all SKU");
        JButton menuButton3 = new JButton("Generate Report");
        if(loggedInStaff.getSiteID().charAt(0) == 'W') {
            menuButton4 = new JButton("View Stock Request");
            menuButton5 = new JButton("Make Order");
            menuButton6 = new JButton("Order History");
        }else{
            menuButton4 = new JButton("Generate Stock Request");
            menuButton5 = new JButton("View Stock Request History");
        }

        JButton menuButton7 = new JButton("Profile");
        JButton menuButton8 = new JButton("Logout");

        buttonPanel.add(menuButton1);
        buttonPanel.add(menuButton2);
        buttonPanel.add(menuButton3);
        buttonPanel.add(menuButton4);
        buttonPanel.add(menuButton5);
        if(menuButton6 != null) {
            buttonPanel.add(menuButton6);
        }
        buttonPanel.add(menuButton7);
        buttonPanel.add(menuButton8);

        menuButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current Menu frame
                new Branch(Menu.this, loggedInStaff);
            }
        });

        menuButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current Menu frame
                new DisplayAllSKU(Menu.this, loggedInStaff, currentBranch, people, branches); // Pass the Menu frame reference
            }
        });

        menuButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current Menu frame
                Branch.ReportMenu(Menu.this, loggedInStaff); // Pass the Menu frame reference
            }
        });

//        menuButton4.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                dispose(); // Close the current Menu frame
//                if(loggedInStaff.getSiteID().charAt(0) == 'W') {
//                    //////////////////////haven done this method, after done change it
//                    StockRequest.displayStockRequest(Menu.this, loggedInStaff.getSiteID()); // this need to do
//                }else{
//                    StockRequest.displayStockRequest(Menu.this, loggedInStaff.getSiteID()); // Call the method correctly
//                }
//            }
//        });

        menuButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current Menu frame
                if(loggedInStaff.getSiteID().charAt(0) == 'W') {
                    PurchaseOrder.makeOrder(Menu.this, loggedInStaff, people);
                }else{
                    PurchaseOrder.makeOrder(Menu.this, loggedInStaff, people);
                }
            }
        });

        if (menuButton6 != null) {
            menuButton6.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    PurchaseOrder.displayOrderHistory(Menu.this, loggedInStaff);
                }
            });
        }

        menuButton7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Staff.profilePage(Menu.this, loggedInStaff, people, branches);
            }
        });

        menuButton8.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current Menu frame
                Staff.loginPage(people, branches);
            }
        });

        add(buttonPanel, BorderLayout.SOUTH);

        buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, 300));

        setVisible(true);
    }
}