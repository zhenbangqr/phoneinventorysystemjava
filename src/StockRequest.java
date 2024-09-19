//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableCellEditor;
//import javax.swing.table.TableCellRenderer;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.*;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//class Stock{
//    String SKU;
//    int quantityAvailable,quantityRequested;
//
//    public Stock(String SKU, int quantityAvailable, int quantityRequested) {
//        this.SKU = SKU;
//        this.quantityAvailable = quantityAvailable;
//        this.quantityRequested = quantityRequested;
//    }
//
//    public String getSKU() {
//        return SKU;
//    }
//
//    public void setSKU(String SKU) {
//        this.SKU = SKU;
//    }
//
//    public int getQuantityAvailable() {
//        return quantityAvailable;
//    }
//
//    public void setQuantityAvailable(int quantityAvailable) {
//        this.quantityAvailable = quantityAvailable;
//    }
//
//    public int getQuantityRequested() {
//        return quantityRequested;
//    }
//
//    public void setQuantityRequested(int quantityRequested) {
//        this.quantityRequested = quantityRequested;
//    }
//}
//
//public class StockRequest extends Warehouse extends Store{
//
//    private String requestID;
//    private String siteID;
//    private String branchID;
//    private String requestStatus;
//    private Date requestDate;
//    private ArrayList<Stock> stockList = new ArrayList<>();
//
//    public StockRequest(String siteID,String branchID,ArrayList<Stock> stockList) {
//        this.branchID = branchID;
//        this.siteID  = siteID;
//        this.stockList = stockList;
//    }
//
//    public StockRequest(ArrayList<Stock> stockList, String requestID, String siteID, String requestStatus, Date requestDate) {
//        this.stockList = stockList;
//        this.requestID = requestID;
//        this.siteID = siteID;
//        this.requestStatus = requestStatus;
//        this.requestDate = requestDate;
//    }
//
//    public String getRequestID() {
//        return requestID;
//    }
//
//    public void setRequestID(String requestID) {
//        this.requestID = requestID;
//    }
//
//    public String getSiteID() {
//        return siteID;
//    }
//
//    public void setSiteID(String siteID) {
//        this.siteID = siteID;
//    }
//
//    public String getRequestStatus() {
//        return requestStatus;
//    }
//
//    public void setRequestStatus(String requestStatus) {
//        this.requestStatus = requestStatus;
//    }
//
//    public Date getRequestDate() {
//        return requestDate;
//    }
//
//    public void setRequestDate(Date requestDate) {
//        this.requestDate = requestDate;
//    }
//
//    public ArrayList<Stock> getStockList() {
//        return stockList;
//    }
//
//    public static void displayStockRequest(Menu menu, String siteID , String branchID) {
//
//        JFrame frame = new JFrame("Stock Request");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 600);
//        frame.setLayout(new BorderLayout());
//
//        // Header Image (replace with your actual image path)
//        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
//        Image image = imageIcon.getImage();
//        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
//        imageIcon = new ImageIcon(scaledImage);
//        JLabel imageLabel = new JLabel(imageIcon);
//        JPanel topPanel = new JPanel(new BorderLayout());
//        topPanel.add(imageLabel, BorderLayout.NORTH);
//
//        // Create the header panel
//        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        JLabel headerLabel = new JLabel("Stock request for " + siteID);
//        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        headerPanel.add(headerLabel);
//        topPanel.add(headerPanel, BorderLayout.SOUTH);
//
//        frame.add(topPanel, BorderLayout.NORTH);
//
//        // Table setup
//        DefaultTableModel model = new DefaultTableModel() {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 2; // Only allow editing the request quantity column
//            }
//        };
//
//        JTable table = new JTable(model) {
//            @Override
//            public TableCellEditor getCellEditor(int row, int column) {
//                if (column == 2) { // Request Quantity column
//                    return new SpinnerEditor(); // Use JSpinner editor for this column
//                }
//                return super.getCellEditor(row, column);
//            }
//
//            @Override
//            public TableCellRenderer getCellRenderer(int row, int column) {
//                if (column == 2) { // Request Quantity column
//                    return new SpinnerRenderer(); // Use JSpinner renderer for this column
//                }
//                return super.getCellRenderer(row, column);
//            }
//        };
//
//        JScrollPane scrollPane = new JScrollPane(table);
//        frame.add(scrollPane, BorderLayout.CENTER);
//
//        // Column headers
//        model.addColumn("SKU");
//        model.addColumn("Available Quantity");
//        model.addColumn("Request Quantity"); // Editable quantity column with JSpinner
//
//        // Read from file and populate table (W1001.txt for stock)
//        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/branchStock_txt/W001.txt"))) {
//            String line = br.readLine(); // skip header line
//
//            while ((line = br.readLine()) != null) {
//                String[] stockData = line.split("\\|");
//                model.addRow(new Object[]{
//                        stockData[0], // SKU
//                        stockData[1], // Available Quantity
//                        0            // Default request quantity set to 0
//                });
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Buttons panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//
//        // Generate Stock Summary Button
//        JButton generateSummaryButton = new JButton("Generate Stock Summary");
//        buttonPanel.add(generateSummaryButton);
//
//        generateSummaryButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//
//                ArrayList<Stock> stockList = new ArrayList<>();
//
//                // Gather all stock with requested quantities greater than 0
//                for (int i = 0; i < model.getRowCount(); i++) {
//                    int requestQty = (int) model.getValueAt(i, 2); // JSpinner returns Integer, this should be safe
//
//                    if (requestQty > 0) {
//                        String stockSKU = (String) model.getValueAt(i, 0);
//                        String stockAvailableString = (String) model.getValueAt(i, 1); // Get value as String
//
//                        // Safely convert available quantity from String to int
//                        int stockAvailable = 0;
//
//                        try {
//                            stockAvailable = Integer.parseInt(stockAvailableString);
//                        } catch (NumberFormatException numberFormatException) {
//                            System.err.println("Invalid number format for available quantity: " + stockAvailableString);
//                        }
//
//                        stockList.add(new Stock(stockSKU, stockAvailable, requestQty));
//                    }
//                }
//                StockRequest stockRequest = new StockRequest(siteID,stockList);
//
//                // Show stock summary dialog
//                if (stockList.isEmpty()) {
//                    JOptionPane.showMessageDialog(frame, "No stock requested,please check the requested quantity.", "Stock Request Summary", JOptionPane.WARNING_MESSAGE);
//                } else {
//                    displayStockSummary(siteID,stockRequest.getStockList());
//                }
//            }
//        });
//
//        // Back Button
//        JButton backButton = new JButton("Back");
//        buttonPanel.add(backButton);
//
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                frame.dispose();
//                menu.setVisible(true);
//            }
//        });
//
//        // Add buttons panel to the frame's SOUTH
//        frame.add(buttonPanel, BorderLayout.SOUTH);
//
//        frame.setVisible(true);
//    }
//
//    public static void displayStockSummary(String siteID, ArrayList<Stock> stockSummary) {
//        JFrame frame = new JFrame("Stock Request Summary");
//        frame.setSize(800, 600);
//        frame.setLayout(new BorderLayout());
//
//        DefaultTableModel model = new DefaultTableModel();
//        JTable table = new JTable(model);
//        JScrollPane scrollPane = new JScrollPane(table);
//        frame.add(scrollPane, BorderLayout.CENTER);
//
//        model.addColumn("No.");
//        model.addColumn("SKU");
//        model.addColumn("Model");
//        model.addColumn("RAM");
//        model.addColumn("ROM");
//        model.addColumn("Color");
//        model.addColumn("Price");
//        model.addColumn("Type");
//        model.addColumn("Available Quantity");
//        model.addColumn("Requested Quantity");
//
//        Map<String, String[]> stockDetails = Inventory.mapProductDetails();
//
//        int rowCount = 1;
//        for (Stock stocks : stockSummary) {
//            String[] productInfo = stockDetails.get(stocks.getSKU());
//            if (productInfo != null) {
//                model.addRow(new Object[]{
//                        rowCount++,
//                        productInfo[0], //SKU
//                        productInfo[1], //Model
//                        productInfo[2], //RAM
//                        productInfo[3], //ROM
//                        productInfo[4], //Color
//                        productInfo[5], //Price
//                        productInfo[6], //Type
//                        stocks.quantityAvailable,
//                        stocks.quantityRequested
//                });
//            }
//        }
//
//        JPanel buttonPanel = getButtonPanel(siteID,frame);
//        frame.add(buttonPanel, BorderLayout.SOUTH);
//        frame.setVisible(true);
//
//    }
//
//    private static JPanel getButtonPanel(String siteID,JFrame frame) {
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//
//        JButton confirmButton = new JButton("Confirm");
//        buttonPanel.add(confirmButton);
//
//        confirmButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(frame, "Request has been sent to Warehouse", "Stock Request Confirmation", JOptionPane.INFORMATION_MESSAGE);
//                String newRequestID = PurchaseOrder.getNextOrderID("aux_files/stock_txt/stockHistory.txt");
//                if(siteID.charAt(0) == 'W') {
//                PurchaseOrder.addOrderToFile(newRequestID,siteID,);
//                }
//                frame.dispose();
//
//            }
//        });
//
//
//        JButton closeButton = new JButton("Close");
//        closeButton.addActionListener(e -> frame.dispose());
//
//        buttonPanel.add(confirmButton);
//        buttonPanel.add(closeButton);
//        return buttonPanel;
//    }
//
//    // SpinnerEditor to allow editing in JSpinner
//    static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
//        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
//
//        @Override
//        public Object getCellEditorValue() {
//            return spinner.getValue();
//        }
//
//        @Override
//        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
//            spinner.setValue(value);
//            return spinner;
//        }
//    }
//
//    // SpinnerRenderer to render the spinner in the table
//    static class SpinnerRenderer extends JSpinner implements TableCellRenderer {
//        public SpinnerRenderer() {
//            super(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
//        }
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            setValue(value);
//            return this;
//        }
//    }
//
//    public static void addRequestToFile(String requestID, String siteID, String warehouseID, HashMap<String, Integer> requestDetails) {
//        // Get the current date
//        LocalDate currentDate = LocalDate.now();
//
//        // Define the formatter for DD/MM/YY format
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
//
//        // Format the date to the desired pattern
//        String formattedDate = currentDate.format(dateFormatter);
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("aux_files/stock_txt/stockHistory.txt", true))) { // true to append to file
//            for (Map.Entry<String, Integer> entry : requestDetails.entrySet()) {
//                String sku = entry.getKey();
//                int quantity = entry.getValue();
//
//                // Write the order details in the format: OrderID|SKU|Qty
//                writer.write(requestID + "|" + sku + "|" + quantity);
//                writer.newLine(); // Add a new line after each entry
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
////        try (BufferedWriter writer = new BufferedWriter(new FileWriter("aux_files/stock_txt/stockRequest.txt", true))) { // true to append to file
////            // Write the order details in the format: OrderID|SKU|Qty
////            writer.write(warehouseID + "|" + storeID + "|" + sku + "|" + "Pending" + "|" + formattedDate);
////            writer.newLine(); // Add a new line after each entry
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//   }
//}