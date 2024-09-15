import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Inventory {

    private JFrame frame;

    public Inventory(Menu menu, String siteID) {
        if(siteID.charAt(0) == 'W') {
            frame = new JFrame("Warehouse Stock");
        }else{
            frame = new JFrame("Store Stock");
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
        // Header Image (replace with your actual image path)
        // Panel to hold both image and header
        JPanel topPanel = new JPanel(new BorderLayout());

        // Header Image
        ImageIcon imageIcon = new ImageIcon("header2.png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(frame.getWidth(), 200, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(imageIcon);
        topPanel.add(imageLabel, BorderLayout.NORTH); // Image at the top of topPanel

        // Create the header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel headerLabel = new JLabel("Stock list for " + siteID);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        topPanel.add(headerPanel, BorderLayout.SOUTH); // Header below the image in topPanel

        // Add the combined topPanel to the frame's NORTH
        frame.add(topPanel, BorderLayout.NORTH);

        // Table to display stock data
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add columns to the table model
        model.addColumn("SKU");
        model.addColumn("Model");
        model.addColumn("RAM");
        model.addColumn("ROM");
        model.addColumn("Color");
        model.addColumn("Price");
        model.addColumn("Type");
        model.addColumn("Amount");

        Map<String, String[]> productDetails = Inventory.mapProductDetails();

        String siteFileName = siteID + ".txt";
        // Read and populate table data
        try (BufferedReader br = new BufferedReader(new FileReader(siteFileName))) {
            String line = br.readLine(); // Skip header line

            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                String[] productInfo = productDetails.get(data[0]);
                if(productInfo != null) {
                    model.addRow(new Object[]{data[0], productInfo[1], productInfo[2], productInfo[3], productInfo[4],productInfo[5], productInfo[6], data[1]});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error reading warehouse stock data.");
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
    }

    public static Map<String, String[]> mapProductDetails(){
        Map<String, String[]> phoneDetails = new HashMap<>();
        String[] productsFileName = {"POCO.txt", "Apple.txt", "Xiaomi.txt"};

        for(String fileName : productsFileName){
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
                String line = br.readLine();//skip the header line

                while((line = br.readLine()) != null){
                    String[] details = line.split("\\|");
                    phoneDetails.put(details[0], details);
                }
            } catch(IOException e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Phone file not founded");
            }
        }

        return phoneDetails;
    }
}