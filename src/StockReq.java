import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockReq {
    private JFrame parentFrame;
    private String requestID;
    private String storeID;
    private String warehouseID;
    private String requestStatus;
    private Date requestDate;
    private ArrayList<Stock> stockList = new ArrayList<>();

    public StockReq(String title,JFrame parentFrame,Staff loggedInStaff, Branch currentBranch, Person[] people, Branch[] branches){
        this.parentFrame = parentFrame;
        parentFrame.dispose();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());

            // Main container to hold image and topPanel
            JPanel mainTopContainer = new JPanel();
            mainTopContainer.setLayout(new BoxLayout(mainTopContainer, BoxLayout.Y_AXIS));
            frame.add(mainTopContainer, BorderLayout.NORTH);

            // Image header
            ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH); // Adjust height as needed
            imageIcon = new ImageIcon(scaledImage);
            JLabel imageLabel = new JLabel(imageIcon);

            // Add image to the main container
            mainTopContainer.add(imageLabel);

            displayStockRequestMenu(mainTopContainer,frame,loggedInStaff, currentBranch, people, branches);

            ////////////////////////////////////////////
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            ////////////////////////////////////////////
            JButton viewOrdersButton = new JButton("View Orders");
            buttonPanel.add(viewOrdersButton);

            viewOrdersButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    displayStockHistory(mainTopContainer,frame,loggedInStaff,currentBranch,people,branches);
                }
            });
        });
    }


    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(String warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public ArrayList<Stock> getStockList() {
        return stockList;
    }

    public static void displayStockRequestMenu(JPanel mainTopContainer,JFrame menuframe, Staff loggedInStaff, Branch currentBranch, Person[] people, Branch[] branches){
        // Panel for current store and toolbar

        JPanel topPanel = new JPanel(new BorderLayout());
        mainTopContainer.add(topPanel); // Add topPanel to the main container below the image

        // Fixed shaded container for the current store at the top left
        JPanel currentStorePanel = new JPanel();
        currentStorePanel.setBackground(new Color(230, 230, 250)); // Light lavender color for the shaded container
        currentStorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JLabel currentStoreLabel = new JLabel("Current Store: " + currentBranch.getId());
        currentStoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentStorePanel.add(currentStoreLabel);

        // Add current store panel to the left of topPanel
        topPanel.add(currentStorePanel, BorderLayout.WEST);

        // Toolbar to the right
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false); // Disable dragging of the toolbar
        JLabel branchLabel = new JLabel("Select Branch ID: ");
        branchLabel.setFont(new Font("Arial", Font.BOLD, 16));
        branchLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> branchSelector = new JComboBox<>();
        branchSelector.setFont(new Font("Arial", Font.BOLD, 16));
        branchSelector.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Branch branch : branches) {
            if (branch != null && branch instanceof Branch && branch.getId().charAt(0) == 'W') {
                branchSelector.addItem(branch.getId()); // Add branch IDs to the JComboBox
            }
        }

        toolBar.add(branchLabel);
        toolBar.add(branchSelector);

        // Add toolbar to the right side of topPanel
        topPanel.add(toolBar, BorderLayout.EAST);

        // Table setup
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only allow editing the request quantity column
            }
        };

        JTable table = new JTable(model) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 2) { // Request Quantity column
                    return new StockRequest.SpinnerEditor(); // Use JSpinner editor for this column
                }
                return super.getCellEditor(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 2) { // Request Quantity column
                    return new StockRequest.SpinnerRenderer(); // Use JSpinner renderer for this column
                }
                return super.getCellRenderer(row, column);
            }
        };

        // Add column headers
        model.addColumn("SKU");
        model.addColumn("Available Quantity");
        model.addColumn("Request Quantity");
        // Listener to load stock data when branch is selected
        branchSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedBranchID = (String) branchSelector.getSelectedItem();
                loadStockData(selectedBranchID, model);

            }
        });

        // Initial stock load for the currentBranch
        loadStockData(currentBranch.getId(), model);

        JScrollPane scrollPane = new JScrollPane(table);
        menuframe.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Generate Stock Summary Button
        JButton generateSummaryButton = new JButton("Generate Stock Summary");
        buttonPanel.add(generateSummaryButton);

        generateSummaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Stock> stockList = new ArrayList<>();

                // Gather all stock with requested quantities greater than 0
                for (int i = 0; i < model.getRowCount(); i++) {
                    int requestQty = (int) model.getValueAt(i, 2); // JSpinner returns Integer, this should be safe

                    if (requestQty > 0) {
                        String stockSKU = (String) model.getValueAt(i, 0);
                        String stockAvailableString = (String) model.getValueAt(i, 1); // Get value as String

                        // Safely convert available quantity from String to int
                        int stockAvailable = 0;
                        try {
                            stockAvailable = Integer.parseInt(stockAvailableString);
                        } catch (NumberFormatException numberFormatException) {
                            System.err.println("Invalid number format for available quantity: " + stockAvailableString);
                        }

                        stockList.add(new Stock(stockSKU, stockAvailable, requestQty));
                    }
                }

                // Show stock summary dialog
                if (stockList.isEmpty()) {
                    JOptionPane.showMessageDialog(menuframe, "No stock requested, please check the requested quantity.", "Stock Request Summary", JOptionPane.WARNING_MESSAGE);
                } else {
                    displayStockSummary(currentBranch.getId(), (String) branchSelector.getSelectedItem(), stockList);
                }
            }
        });

// View Order Request Button
        JButton viewOrdersButton = new JButton("View Orders");
        buttonPanel.add(viewOrdersButton);

        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayStockHistory(mainTopContainer,menuframe,loggedInStaff,currentBranch,people,branches);
            }
        });

// Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuframe.dispose();
            }
        });

// Add buttons panel to the frame's SOUTH
        menuframe.add(buttonPanel, BorderLayout.SOUTH);
    }
    public static void displayStockSummary(String siteID,String warehouseID, ArrayList<Stock> stockSummary) {

        JFrame frame = new JFrame("Stock Request Summary");
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        model.addColumn("No.");
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");
        model.addColumn("Available Quantity");
        model.addColumn("Requested Quantity");

        Map<String, String[]> stockDetails = Branch.mapProductDetails();

        int rowCount = 1;
        for (Stock stocks : stockSummary) {
            String[] productInfo = stockDetails.get(stocks.getSKU());
            if (productInfo != null) {
                model.addRow(new Object[]{
                        rowCount++,
                        productInfo[0], //SKU
                        productInfo[1], //Model
                        productInfo[2], //RAM
                        productInfo[3], //ROM
                        productInfo[4], //Color
                        productInfo[5], //Price
                        productInfo[6], //Type
                        stocks.quantityAvailable,
                        stocks.quantityRequested
                });
            }
        }

        JPanel buttonPanel = getButtonPanel(siteID,warehouseID,frame);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

    }
    private static JPanel getButtonPanel(String siteID,String warehouseID,JFrame frame) {

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton confirmButton = new JButton("Confirm");
        buttonPanel.add(confirmButton);

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Request has been sent to Warehouse", "Stock Request Confirmation", JOptionPane.INFORMATION_MESSAGE);
                String newRequestID = PurchaseOrder.getNextOrderID("aux_files/stock_txt/stockHistory.txt");
                if(siteID.charAt(0) == 'S') {
                    //addOrderToFile(newRequestID,siteID,warehouseID,);
                }
                frame.dispose();

            }
        });


        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }

    private static void displayStockHistory(JPanel mainTopContainer,JFrame menuframe, Staff loggedInStaff, Branch currentBranch, Person[] people, Branch[] branches){

        JFrame frame = new JFrame("Stock History");
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

        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/order_txt/orderRequest.txt"))) {
            String line = br.readLine(); // skip header line

            while ((line = br.readLine()) != null) {
                String[] orderData = line.split("\\|");

                if (orderData[1].equals(loggedInStaff.getSiteID())) {
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
        JButton viewDetailsButton = new JButton("View Order Details");
        buttonPanel.add(viewDetailsButton);

        viewDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedOrderID = model.getValueAt(selectedRow, 1).toString();
                    frame.dispose();
                    displayStockRequestMenu(mainTopContainer,menuframe,loggedInStaff,currentBranch,people,branches);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select an request to view details.", "No Request Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    // Method to load stock data from branch file
    private static void loadStockData(String branchID, DefaultTableModel model) {
        model.setRowCount(0); // Clear the table first

        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/branchStock_txt/" + branchID + ".txt"))) {
            String line = br.readLine(); // skip header line

            while ((line = br.readLine()) != null) {
                String[] stockData = line.split("\\|");
                model.addRow(new Object[]{
                        stockData[0], // SKU
                        stockData[1], // Available Quantity
                        0            // Default request quantity set to 0
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
