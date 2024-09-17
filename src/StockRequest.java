import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StockRequest {

    public static void displayStockRequest(Menu menu, String siteID) {
        JFrame frame = new JFrame("Stock Request");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Header Image (replace with your actual image path)
        ImageIcon imageIcon = new ImageIcon("aux_files/images/header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imageLabel, BorderLayout.NORTH);

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Stock request for " + siteID);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);

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

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Column headers
        model.addColumn("SKU");
        model.addColumn("Available Quantity");
        model.addColumn("Request Quantity"); // Editable quantity column with JSpinner

<<<<<<< Updated upstream
        // Read from file and populate table
        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/order_txt/orderRequest.txt"))) {
=======
        // Read from file and populate table (W1001.txt for stock)
        try (BufferedReader br = new BufferedReader(new FileReader("aux_files/warehouse_txt/W001.txt"))) {
>>>>>>> Stashed changes
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

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Generate Stock Summary Button
        JButton generateSummaryButton = new JButton("Generate Stock Summary");
        buttonPanel.add(generateSummaryButton);

        generateSummaryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<String[]> requestedStock = new ArrayList<>();

                // Gather all stock with requested quantities greater than 0
                for (int i = 0; i < model.getRowCount(); i++) {
                    int requestQty = (int) model.getValueAt(i, 2); // Get spinner value as integer
                    if (requestQty > 0) {
                        requestedStock.add(new String[]{
                                model.getValueAt(i, 0).toString(), // SKU
                                model.getValueAt(i, 1).toString(), // Available Quantity
                                String.valueOf(requestQty)         // Requested Quantity
                        });
                    }
                }

                // Show stock summary dialog
                if (requestedStock.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "No stock requested.", "Stock Request Summary", JOptionPane.WARNING_MESSAGE);
                } else {
                    displayStockSummary(requestedStock);
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

    public static void displayStockSummary(ArrayList<String[]> stockSummary) {
        JFrame frame = new JFrame("Stock Request Summary");
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        model.addColumn("SKU");
        model.addColumn("Available Quantity");
        model.addColumn("Requested Quantity");

<<<<<<< Updated upstream
        Map<String, String[]> productDetails = Inventory.mapProductDetails();

        try(BufferedReader br = new BufferedReader(new FileReader("aux_files/order_txt/orderDetails.txt"))){
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
=======
        // Add stock data to the summary table
        for (String[] stock : stockSummary) {
            model.addRow(stock);
>>>>>>> Stashed changes
        }

        JButton confirmButton = new JButton("Confirm");
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> frame.dispose());
        confirmButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Stock Request Sent !.", "Message", JOptionPane.INFORMATION_MESSAGE));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(confirmButton);
        buttonPanel.add(returnButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
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
