import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class StockRequest {

    private String requestID;
    private String siteID;
    private String productSKU;
    private int quantity;
    private String requestStatus;
    private Date requestDate;

    private static StockRequest[] stockRequests = new StockRequest[20];
    private static int i = 0;

    public StockRequest(String orderID,String siteID, String productSKU, int quantity, String orderStatus, Date orderDate) {
        this.requestID = orderID;
        this.siteID = siteID;
        this.productSKU = productSKU;
        this.quantity = quantity;
        this.requestStatus = orderStatus;
        this.requestDate = orderDate;
    }


    public static void displayOrderHistory(Menu menu, String siteID) {
        JFrame frame = new JFrame("Stock Request History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Header Image (replace with your actual image path)
        ImageIcon imageIcon = new ImageIcon("header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH); // Adjust height as needed
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Stock Request for " + siteID);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        // Table setup
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Column headers
        model.addColumn("Select");
        model.addColumn("Order ID");
        model.addColumn("Supplier ID");
        model.addColumn("Status");
        model.addColumn("Order Date");

        // Read from file and populate table
        try (BufferedReader br = new BufferedReader(new FileReader("orderRequest.txt"))) {
            String line = br.readLine(); // skip header line

            while ((line = br.readLine()) != null) {
                String[] orderData = line.split("\\|");

                if (orderData[1].equals(siteID)) {
                    model.addRow(new Object[]{
                            false, // Selection (radio button)
                            orderData[0], // Order ID
                            orderData[2], // Supplier ID
                            orderData[3], // Order Status
                            orderData[4]  // Order Date
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Radio button selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    // Update all radio buttons in the first column to false except the selected row
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (i != selectedRow) {
                            model.setValueAt(false, i, 0);
                        }
                    }
                }
            }
        });

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // View Details Button
        JButton viewDetailsButton = new JButton("View Request Details");
        buttonPanel.add(viewDetailsButton);

        viewDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedOrderID = model.getValueAt(selectedRow, 1).toString();
                    frame.dispose();
                    showRequestDetails(menu, siteID, selectedOrderID);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an order to view details.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        // Add buttons panel to the frame's SOUTH
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void showRequestDetails(Menu menu, String siteID, String orderID) {
        JFrame frame = new JFrame("Request Stock History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon("header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Order details for " + orderID);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        // Table to display stock data
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add columns to the table model
        model.addColumn("No.");
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");
        model.addColumn("Quantity");

        Map<String, String[]> productDetails = Inventory.mapProductDetails();

        try(BufferedReader br = new BufferedReader(new FileReader("orderDetails.txt"))){
            String line = br.readLine(); //Skip the header line
            int numOfProduct = 0;
            while((line = br.readLine()) != null) {
                String[] orderDetails = line.split("\\|");
                if (orderDetails[0].equals(orderID)) {
                    String[] productInfo = productDetails.get(orderDetails[1]);
                    numOfProduct++;
                    if (productInfo != null) {
                        model.addRow(new Object[]{
                                numOfProduct, //product number
                                productInfo[0], //SKU
                                productInfo[1], //Model
                                productInfo[2], //RAM
                                productInfo[3], //ROM
                                productInfo[4], //Color
                                productInfo[5], //Price
                                productInfo[6], //Type
                                orderDetails[2], //Order Qty
                        });
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            PurchaseOrder.displayOrderHistory(menu, siteID);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
