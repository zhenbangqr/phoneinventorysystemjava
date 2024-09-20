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


public class StockRequest{
    private String requestID;
    private String storeID;
    private String warehouseID;
    private String requestStatus;
    private Date requestDate;
    private ArrayList<Stock> stockList = new ArrayList<>();

    public StockRequest(String storeID,String warehouseID,ArrayList<Stock> stockList) {
        this.storeID = storeID;
        this.warehouseID  = warehouseID;
        this.stockList = stockList;
    }

    public StockRequest(ArrayList<Stock> stockList, String requestID, String storeID, String requestStatus, Date requestDate) {
        this.stockList = stockList;
        this.requestID = requestID;
        this.storeID = storeID;
        this.requestStatus = requestStatus;
        this.requestDate = requestDate;
    }

    public StockRequest(String requestID, String storeID, String requestStatus, Date requestDate){
        this.requestID = requestID;
        this.storeID = storeID;
        this.requestStatus = requestStatus;
        this.requestDate = requestDate;
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

    public static void displayStockRequestMenu(Menu menu, Staff loggedInStaff, Branch currentBranch, Person[] people, Branch[] branches) {

        JFrame frame = new JFrame("Stock Request");
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
        frame.add(scrollPane, BorderLayout.CENTER);

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
                    JOptionPane.showMessageDialog(frame, "No stock requested, please check the requested quantity.", "Stock Request Summary", JOptionPane.WARNING_MESSAGE);
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
                displayOrderRequestFile();
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
    public static void displayOrderRequestFile() {
        JFrame orderFrame = new JFrame("Order Requests");
        orderFrame.setSize(800, 600);
        orderFrame.setLayout(new BorderLayout());

        JTextArea orderTextArea = new JTextArea();
        orderTextArea.setEditable(false); // Make the text area read-only
        JScrollPane scrollPane = new JScrollPane(orderTextArea);
        orderFrame.add(scrollPane, BorderLayout.CENTER);

        // Read the stockHistory.txt file and display the contents in the JTextArea
        try (BufferedReader reader = new BufferedReader(new FileReader("aux_files/order_txt/stockHistory.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                orderTextArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(orderFrame, "Failed to load order requests.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> orderFrame.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);

        orderFrame.add(buttonPanel, BorderLayout.SOUTH);

        orderFrame.setVisible(true);
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

    public static void addOrderToFile(String requestID, String siteID, String warehouseID, HashMap<String, Integer> requestDetails) {
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Define the formatter for DD/MM/YY format
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        // Format the date to the desired pattern
        String formattedDate = currentDate.format(dateFormatter);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("aux_files/order_txt/stockDetails.txt", true))) { // true to append to file
            for (Map.Entry<String, Integer> entry : requestDetails.entrySet()) {
                String sku = entry.getKey();
                int quantity = entry.getValue();

                // Write the order details in the format: OrderID|SKU|Qty
                writer.write(requestID + "|" + sku + "|" + quantity);
                writer.newLine(); // Add a new line after each entry
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("aux_files/order_txt/orderRequest.txt", true))) { // true to append to file
            // Write the order details in the format: OrderID|SKU|Qty
            writer.write(requestID + "|" + warehouseID + "|" + siteID + "|" + "Pending" + "|" + formattedDate);
            writer.newLine(); // Add a new line after each entry
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //done by fattsiong
    //for warehouse site to view and approve/reject the stock request

    //displayThe
    public static void displayStockRequestHistory(Menu menu, Branch currentBranch){
        JFrame frame = new JFrame("Pending Stock Request");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Header Image (replace with your actual image path)
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH); // Adjust height as needed
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Pending Stock Request for "); //////////////need to add + warehouseID
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
        model.addColumn("Request ID");
        model.addColumn("Store ID");
        model.addColumn("Status");
        model.addColumn("Request Date");

        // Read from file and populate table
        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/stock_txt/stockHistory.txt"))) {
            String line = br.readLine(); // skip header line

            while ((line = br.readLine()) != null) {
                String[] stockRequestData = line.split("\\|");

                if (stockRequestData[1].equals("W001")) {////////need to change after this
                    model.addRow(new Object[]{
                            false, // Selection (radio button)
                            stockRequestData[0], // Request ID
                            stockRequestData[2], // Store ID
                            stockRequestData[3], // Request Status
                            stockRequestData[4]  // Request Date
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
                    String requestID = (String) model.getValueAt(selectedRow, 1);
                    String storeID = (String) model.getValueAt(selectedRow, 2);
                    String status = (String) model.getValueAt(selectedRow, 3);
                    String requestDateStr = (String) model.getValueAt(selectedRow, 4);

                    Date requestDate = null;
                    try {
                        // Correct format string for dd/MM/yy
                        requestDate = new SimpleDateFormat("dd/MM/yy").parse(requestDateStr);
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    PurchaseOrder currentStockRequest = new PurchaseOrder(requestID, storeID, status, requestDate);
                    frame.dispose();
                    displayRequestDetails(menu, currentBranch, currentStockRequest);
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

    public static void displayRequestDetails(Menu menu, Branch currentBranch, PurchaseOrder currentStockRequest) {
        JFrame frame = new JFrame("Purchase Order History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Stock Request details for " + currentStockRequest.getOrderID());
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

        Map<String, String[]> productDetails = Branch.mapProductDetails();
        List<PurchaseOrder> currentStock = Stock.createCurrentStockArrayList(currentBranch.getId());
        boolean checkStockEnough = true;

        try(BufferedReader br = new BufferedReader(new FileReader("aux_files/stock_txt/stockDetails.txt"))){
            String line = br.readLine(); //Skip the header line
            int numOfProduct = 0;
            while((line = br.readLine()) != null) {
                String[] requestDetails = line.split("\\|");
                if (requestDetails[0].equals(currentStockRequest.getOrderID())) {
                    int requestedQuantity = Integer.parseInt(requestDetails[2]);

                    String[] productInfo = productDetails.get(requestDetails[1]);
                    int availableStock = getAvailableStock(requestDetails[1], currentStock);
                    numOfProduct++;
                    if (productInfo != null) {
                        checkStockEnough = requestedQuantity <= availableStock;

                        model.addRow(new Object[]{
                                numOfProduct, //product number
                                productInfo[0], //SKU
                                productInfo[1], //Model
                                productInfo[2], //RAM
                                productInfo[3], //ROM
                                productInfo[4], //Color
                                productInfo[5], //Price
                                productInfo[6], //Type
                                requestDetails[2], //Request Qty
                        });
                    }

                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        if(currentStockRequest.getOrderStatus().equals("Pending")) {
            // Select Supplier Button
            JButton approveStockRequestButtom = new JButton("Approve Stock Request");
            buttonPanel.add(approveStockRequestButtom);

            boolean finalCheckStockEnough = checkStockEnough;

            approveStockRequestButtom.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (finalCheckStockEnough) {
                        currentStockRequest.setOrderStatus("Approved");
                        JOptionPane.showMessageDialog(frame, "Successfully Approve");
                        frame.dispose();
                        updateStockRequestFile(currentStockRequest);
                        updateBranchStockFile(currentStockRequest, currentBranch);
                        menu.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Stock Not Enough");
                    }
                }
            });

            // Select Supplier Button
            JButton rejectStockRequestButton = new JButton("Reject Stock Request");
            buttonPanel.add(rejectStockRequestButton);

            rejectStockRequestButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    currentStockRequest.setOrderStatus("Rejected");
                    JOptionPane.showMessageDialog(frame, "Successfully Reject");
                    frame.dispose();
                    updateStockRequestFile(currentStockRequest);
                    menu.setVisible(true);
                }
            });
        }

        // Back Button
        JButton backButton = new JButton("Back");
        buttonPanel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                StockRequest.displayStockRequestHistory(menu, currentBranch);
            }
        });

        // Add buttons panel to the frame's SOUTH
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public static void updateStockRequestFile(PurchaseOrder currentStockRequest) {
        String filePath = "aux_files/stock_txt/stockHistory.txt";
        StringBuilder fileContent = new StringBuilder();

        // format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String formattedDate = dateFormat.format(currentStockRequest.getOrderDate());

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            fileContent.append(line).append("\n");
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                // If this is the line for the staff with the matching ID, update the information
                if (data[0].equals(currentStockRequest.getOrderID())) {
                    // Construct new data for this staff
                    line = String.join("|", currentStockRequest.getOrderID(), data[1], currentStockRequest.getSiteID(), currentStockRequest.getOrderStatus(), formattedDate);
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

    public static void updateBranchStockFile(PurchaseOrder currentStockRequest, Branch currentBranch) {
        String warehouseID = currentBranch.getId(); // Get the warehouse ID from the current branch
        String filePath = "aux_files/branchStock_txt/" + warehouseID + ".txt";
        StringBuilder fileContent = new StringBuilder();

        Map<String, Integer> requestedProducts = getRequestedProducts(currentStockRequest.getOrderID());

        // Read current stock data and update it
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Read header line if necessary
            fileContent.append(line).append("\n"); // Keep header

            while ((line = reader.readLine()) != null) {
                String[] stockData = line.split("\\|");
                String sku = stockData[0]; // SKU of the product
                int availableStock = Integer.parseInt(stockData[1]);

                if (requestedProducts.containsKey(sku)) {
                    // Update stock if the request was approved
                    int requestedQty = requestedProducts.get(sku);
                    if (currentStockRequest.getOrderStatus().equals("Approved")) {
                        availableStock -= requestedQty; // Add requested quantity
                    }
                }

                // Append updated line
                fileContent.append(sku).append("|").append(availableStock).append("\n");
            }

        } catch (IOException ex) {
            System.err.println("Error reading stock data: " + ex.getMessage());
        }

        // Write the updated content back to the file
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            writer.write(fileContent.toString());
        } catch (IOException ex) {
            System.err.println("Error writing updated stock data: " + ex.getMessage());
        }
    }

    public static Map<String, Integer> getRequestedProducts(String orderID) {
        Map<String, Integer> requestedProducts = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/stock_txt/stockDetails.txt"))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] requestDetails = line.split("\\|");
                if (requestDetails[0].equals(orderID)) {
                    String sku = requestDetails[1];  // SKU
                    int requestedQty = Integer.parseInt(requestDetails[2]);  // Requested Quantity
                    requestedProducts.put(sku, requestedQty);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return requestedProducts;
    }

    // Helper method to find available stock for a product SKU
    private static int getAvailableStock(String productSKU, List<PurchaseOrder> currentStock) {
        for (PurchaseOrder order : currentStock) {
            if (order.getProductSKU().equals(productSKU)) {
                return order.getQuantity();
            }
        }
        return 0; // If SKU is not found, assume 0 stock
    }
}