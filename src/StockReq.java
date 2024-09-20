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

    public StockReq(String storeID) {
        this.storeID = storeID;
    }

    public StockReq(String title, JFrame parentFrame, Staff loggedInStaff, Branch currentBranch, Person[] people, Branch[] branches){
        this.parentFrame = parentFrame;
        parentFrame.dispose();

        SwingUtilities.invokeLater(() -> {
            JFrame menuFrame = new JFrame(title);
            menuFrame.setVisible(true);
            menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menuFrame.setSize(800, 600);
            menuFrame.setLayout(new BorderLayout());

            // Main container to hold image and topPanel
            JPanel mainTopContainer = new JPanel();
            mainTopContainer.setLayout(new BoxLayout(mainTopContainer, BoxLayout.Y_AXIS));
            menuFrame.add(mainTopContainer, BorderLayout.NORTH);

            // Image header
            ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(menuFrame.getWidth(), 200, Image.SCALE_SMOOTH); // Adjust height as needed
            imageIcon = new ImageIcon(scaledImage);
            JLabel imageLabel = new JLabel(imageIcon);

            // Add image to the main container
            mainTopContainer.add(imageLabel);

            displayStockRequestMenu(mainTopContainer,menuFrame,loggedInStaff, currentBranch, people, branches);

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

    public static void displayStockRequestMenu(JPanel mainTopContainer,JFrame menuFrame, Staff loggedInStaff, Branch currentBranch, Person[] people, Branch[] branches){
        // Panel for current store and toolbar

        JFrame requestFrame = new JFrame();
        requestFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel topPanel = new JPanel(new BorderLayout());
        mainTopContainer.add(topPanel); // Add topPanel to the main container below the image

        // Fixed shaded container for the current store at the top left
        JPanel currentStorePanel = new JPanel();
        currentStorePanel.setBackground(new Color(230, 230, 250)); // Light lavender color for the shaded container
        currentStorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        JLabel currentStoreLabel = new JLabel("Current Store: " + loggedInStaff.getSiteID());
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
                    return new SpinnerEditor(); // Use JSpinner editor for this column
                }
                return super.getCellEditor(row, column);
            }

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 2) { // Request Quantity column
                    return new SpinnerRenderer(); // Use JSpinner renderer for this column
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
        menuFrame.add(scrollPane, BorderLayout.CENTER);

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
                    JOptionPane.showMessageDialog(menuFrame, "No stock requested, please check the requested quantity.", "Stock Request Summary", JOptionPane.WARNING_MESSAGE);
                } else {
                    displayStockSummary(loggedInStaff.getSiteID(), (String) branchSelector.getSelectedItem(), stockList);
                }
            }
        });

// View Stock Request Button
        JButton viewRequestButton = new JButton("View Request History");
        buttonPanel.add(viewRequestButton);
        viewRequestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayStockHistory(requestFrame,loggedInStaff);
            }
        });

// Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                menuFrame.dispose();
                new Menu(loggedInStaff,currentBranch,people,branches);
            }
        });

// Add buttons panel to the frame's SOUTH
        menuFrame.add(buttonPanel, BorderLayout.SOUTH);
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

        JPanel buttonPanel = getButtonPanel(siteID,warehouseID,frame,stockSummary);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);

    }

    private static JPanel getButtonPanel(String siteID,String warehouseID,JFrame frame,ArrayList<Stock> stockSummary) {

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton confirmButton = new JButton("Confirm");
        buttonPanel.add(confirmButton);

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Request has been sent to Warehouse", "Stock Request Confirmation", JOptionPane.INFORMATION_MESSAGE);
                String newRequestID = PurchaseOrder.getNextOrderID("aux_files/stock_txt/stockHistory.txt");
                newRequestID = newRequestID.replace('O','R');
                if(siteID.charAt(0) == 'S') {
                    addRequestToFile(newRequestID,siteID,warehouseID,stockSummary);
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

    private static void displayStockHistory(JFrame requestFrame, Staff loggedInStaff){

        JFrame historyFrame = new JFrame("Stock History");
        historyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        historyFrame.setSize(800, 600);
        historyFrame.setLayout(new BorderLayout());

        // Header Image (replace with your actual image path)
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(historyFrame.getWidth(), 200, Image.SCALE_SMOOTH); // Adjust height as needed
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Order history for " + loggedInStaff.getSiteID());
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        historyFrame.add(topPanel, BorderLayout.NORTH);

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
        historyFrame.add(scrollPane, BorderLayout.CENTER);


        // Column headers
        model.addColumn("Select");
        model.addColumn("Request ID");
        model.addColumn("Site ID");
        model.addColumn("Status");
        model.addColumn("Order Date");

        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/stock_txt/stockHistory.txt"))) {
            String line = br.readLine(); // skip header line

            while ((line = br.readLine()) != null) {
                String[] orderData = line.split("\\|");

                if (orderData[2].equals(loggedInStaff.getSiteID())) {
                    model.addRow(new Object[]{
                            false, // Selection (radio button)
                            orderData[0], // Order ID
                            orderData[2], // From SiteID
                            orderData[3], // Request Status
                            orderData[4]  // Request Date
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
        JButton viewDetailsButton = new JButton("View Request History");
        buttonPanel.add(viewDetailsButton);

        viewDetailsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String selectedRequestID = model.getValueAt(selectedRow, 1).toString();
                    historyFrame.dispose();
                    displayDetailedHistory(historyFrame,loggedInStaff,selectedRequestID);
                } else {
                    JOptionPane.showMessageDialog(requestFrame, "Please select an request to view details.", "No Request Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                historyFrame.dispose();
            }
        });
        // Add buttons panel to the frame's SOUTH
        historyFrame.add(buttonPanel, BorderLayout.SOUTH);
        historyFrame.setVisible(true);
    }

    private static void displayDetailedHistory(JFrame historyFrame, Staff loggedInStaff, String selectedRequestID) {
        JFrame detailFrame = new JFrame("Detailed Stock History");
        detailFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        detailFrame.setSize(800, 600);
        detailFrame.setLayout(new BorderLayout());

        // Top image and header
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(detailFrame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Request details for " + selectedRequestID);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH);

        detailFrame.add(topPanel, BorderLayout.NORTH);

        // Table to display stock data
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        detailFrame.add(scrollPane, BorderLayout.CENTER);

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

        // Mapping product details
        Map<String, String[]> productDetails = Branch.mapProductDetails();

        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/stock_txt/stockDetails.txt"))) {
            String line = br.readLine(); // Skip the header line
            int numOfProduct = 0;
            while ((line = br.readLine()) != null) {
                String[] orderDetails = line.split("\\|");
                if (orderDetails[0].equals(selectedRequestID)) {
                    String[] productInfo = productDetails.get(orderDetails[1]);
                    numOfProduct++;
                    if (productInfo != null) {
                        model.addRow(new Object[]{
                                numOfProduct, // Product number
                                productInfo[0], // SKU
                                productInfo[1], // Model
                                productInfo[2], // RAM
                                productInfo[3], // ROM
                                productInfo[4], // Color
                                productInfo[5], // Price
                                productInfo[6], // Type
                                orderDetails[2], // Order Qty
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            detailFrame.dispose();
            displayStockHistory(historyFrame,loggedInStaff);
        });

        // Ensure back button is properly added at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        detailFrame.add(buttonPanel, BorderLayout.SOUTH);  // Add the button panel in the SOUTH region
        detailFrame.setVisible(true);
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

    public static void addRequestToFile(String requestID, String storeID, String warehouseID,ArrayList<Stock> StockSummary) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Define the formatter for DD/MM/YY format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        // Format the date to the desired pattern
        String formattedDate = currentDate.format(dateFormatter);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("aux_files/stock_txt/stockDetails.txt", true))) { // true to append to file
            // Iterate over the StockSummary ArrayList
            for (Stock stock : StockSummary) {
                String sku = stock.getSKU();       // Assuming getSKU() retrieves the SKU from the Stock object
                int quantity = stock.getQuantityRequested(); // Assuming getQuantity() retrieves the quantity from the Stock object

                // Write the stock details in the format: RequestID|SKU|Qty
                writer.write(requestID + "|" + sku + "|" + quantity);
                writer.newLine(); // Add a new line after each entry
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("aux_files/stock_txt/stockHistory.txt", true))) { // true to append to file
            // Write the order details in the format: OrderID|SKU|Qty
            writer.write(requestID + "|" + warehouseID + "|" + storeID + "|" + "Pending" + "|" + formattedDate);
            writer.newLine(); // Add a new line after each entry
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SpinnerEditor to allow editing in JSpinner
    static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            spinner.setValue(value);
            return spinner;
        }
    }

    // SpinnerRenderer to render the spinner in the table
    static class SpinnerRenderer extends JSpinner implements TableCellRenderer {
        public SpinnerRenderer() {
            super(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setValue(value);
            return this;
        }
    }
}
