import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PurchaseOrder {
    private String orderID;
    private String siteID;
    private String productSKU;
    private int quantity;
    private String orderStatus;
    private Date orderDate;

    private static PurchaseOrder[] orders = new PurchaseOrder[20];
    private static int i = 0;

    public PurchaseOrder(String orderID,String siteID, String productSKU, int quantity, String orderStatus, Date orderDate) {
        this.orderID = orderID;
        this.siteID = siteID;
        this.productSKU = productSKU;
        this.quantity = quantity;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
    }


    public static PurchaseOrder[] displayOrderHistory(Menu menu, String siteID){
        JFrame frame = new JFrame("Purchase Order History");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Header Image (replace with your actual image path)
        ImageIcon imageIcon = new ImageIcon("header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH); // Adjust height as needed
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        frame.add(imageLabel, BorderLayout.NORTH);

        // Table to display stock data
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add columns to the table model
        model.addColumn("Order ID");
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");
        model.addColumn("Quantity");
        model.addColumn("Status");
        model.addColumn("Order Date");

        Map<String, String[]> productDetails = Inventory.mapProductDetails();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

        try(BufferedReader br = new BufferedReader(new FileReader("orderRequest.txt"))){
            String line = br.readLine(); //Skip the header line

            while((line = br.readLine()) != null){
                String[] orderData = line.split("\\|");

                if(orderData[1].equals(siteID)){
                    String[] productInfo = productDetails.get(orderData[2]);

                    if(productInfo != null) {

                        Date orderDate = null;
                        try {
                            orderDate = dateFormat.parse(orderData[5]);  // Parse string into Date
                        } catch (ParseException e) {
                            System.err.println("Failed to parse date: " + orderData[5]);
                            e.printStackTrace();
                        }

                        model.addRow(new Object[]{
                                orderData[0], //Order ID
                                productInfo[0], //SKU
                                productInfo[1], //Model
                                productInfo[2], //RAM
                                productInfo[3], //ROM
                                productInfo[4], //Color
                                productInfo[5], //Price
                                productInfo[6], //Type
                                orderData[3], //Order Qty
                                orderData[4], //Order Status
                                orderData[5]  //Order Date
                        });

                        orders[i++] = new PurchaseOrder(orderData[0], orderData[1], orderData[2], Integer.parseInt(orderData[3]), orderData[4], orderDate);
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                menu.setVisible(true);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(backButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        return java.util.Arrays.copyOf(orders, i); //return only the filled part of the array
    }


    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getProductSKU() {
        return productSKU;
    }

    public void setProductSKU(String productSKU) {
        this.productSKU = productSKU;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
