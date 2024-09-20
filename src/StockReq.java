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

    public StockReq(String title) {

        JFrame frame = new JFrame(title);
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


    }

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

            displayStockRequestMenu(mainTopContainer, loggedInStaff, currentBranch, people, branches);

            ////////////////////////////////////////////
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            ////////////////////////////////////////////
            JButton viewOrdersButton = new JButton("View Orders");
            buttonPanel.add(viewOrdersButton);

            viewOrdersButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    displayStockHistory(frame, loggedInStaff);
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

    public static void displayStockRequestMenu(JPanel mainTopContainer, Staff loggedInStaff, Branch currentBranch, Person[] people, Branch[] branches){
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
            if (loggedInStaff.getSiteID().charAt(0) == 'S' && branch != null && branch instanceof Branch && branch.getId().charAt(0) == 'W') {
                branchSelector.addItem(branch.getId()); // Add branch IDs to the JComboBox
            }
        }

        toolBar.add(branchLabel);
        toolBar.add(branchSelector);

        // Add toolbar to the right side of topPanel
        topPanel.add(toolBar, BorderLayout.EAST);
        displayStockSummaryFromBranch();
    }

    private static void displayStockSummaryFromBranch(){

    }

    private static void displayStockHistory(JFrame frame,Staff loggedInStaff){
        StockReq stockHistoryGUI = new StockReq("Stock Request History");

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
        readStockHistoryFile(model,loggedInStaff);

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
    }

    private static void readStockHistoryFile(DefaultTableModel model,Staff loggedInStaff){
        // Read from file and populate table

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
}
}
