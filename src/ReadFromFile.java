import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Product {
    String SKU, Model, RAM, ROM, Color, Type;
    int Price;

    Product(String SKU, String Model, String RAM, String ROM, String Color, int Price, String Type) {
        this.SKU = SKU;
        this.Model = Model;
        this.RAM = RAM;
        this.ROM = ROM;
        this.Color = Color;
        this.Price = Price;
        this.Type = Type;
    }

    @Override
    public String toString() {
        return "SKU: " + SKU + ", Model: " + Model + ", RAM: " + RAM + ", ROM: " + ROM + ", Color: " + Color + ", Price: " + Price + ", Type: " + Type;
    }
}

public class ReadFromFile {

    private static JTable table;
    private static DefaultTableModel tableModel;
    private static Map<String, String> manufacturerFiles = new HashMap<>();
    private static JComboBox<String> typeFilterComboBox;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Product Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Create table model
            tableModel = new DefaultTableModel();
            tableModel.addColumn("SKU");
            tableModel.addColumn("Model");
            tableModel.addColumn("RAM");
            tableModel.addColumn("ROM");
            tableModel.addColumn("Color");
            tableModel.addColumn("Price");
            tableModel.addColumn("Type");

            // Create table and add to frame
            table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Create menu bar and menu
            JMenuBar menuBar = new JMenuBar();
            JMenu manufacturerMenu = new JMenu("Manufacturer");
            menuBar.add(manufacturerMenu);

            // Add manufacturer options to menu (replace with your actual file names)
            addManufacturerMenuItem(manufacturerMenu, "Apple", "Apple.txt");
            addManufacturerMenuItem(manufacturerMenu, "Samsung", "Samsung.txt");
            addManufacturerMenuItem(manufacturerMenu, "POCO", "POCO.txt");
            addManufacturerMenuItem(manufacturerMenu, "Nothing", "nothing.txt");
            // Add more manufacturers as needed

            // Create type filter JComboBox
            typeFilterComboBox = new JComboBox<>();
            typeFilterComboBox.addItem("All"); // Default option to show all types

            frame.setJMenuBar(menuBar);
            frame.setVisible(true);

            // Load initial data (you can choose a default manufacturer here)
            loadProductsFromFile("Apple.txt");

            // Get unique types from productList after initial load
            Set<String> uniqueTypes = getUniqueTypes(readProductsFromFile("Apple.txt"));
            for (String type : uniqueTypes) {
                typeFilterComboBox.addItem(type);
            }

            typeFilterComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedType = (String) typeFilterComboBox.getSelectedItem();
                    filterTableByType(selectedType);
                }
            });

            // Add JComboBox to frame
            JPanel filterPanel = new JPanel();
            filterPanel.add(new JLabel("Filter by Type:"));
            filterPanel.add(typeFilterComboBox);
            frame.add(filterPanel, BorderLayout.NORTH);
        });
    }

    private static void addManufacturerMenuItem(JMenu menu, String manufacturerName, String fileName) {
        JMenuItem menuItem = new JMenuItem(manufacturerName);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProductsFromFile(fileName);

                // Update typeFilterComboBox with new types
                Set<String> uniqueTypes = getUniqueTypes(readProductsFromFile(fileName));
                typeFilterComboBox.removeAllItems();
                typeFilterComboBox.addItem("All");
                for (String type : uniqueTypes) {
                    typeFilterComboBox.addItem(type);
                }

                // Set the selected item to "All" after updating the items
                typeFilterComboBox.setSelectedItem("All");

                // Now you can safely filter
                filterTableByType("All");
            }
        });
        menu.add(menuItem);
        manufacturerFiles.put(manufacturerName, fileName);
    }

    private static void loadProductsFromFile(String fileName) {
        tableModel.setRowCount(0); // Clear existing data

        ArrayList<Product> productList = readProductsFromFile(fileName);
        for (Product product : productList) {
            tableModel.addRow(new Object[]{product.SKU, product.Model, product.RAM, product.ROM, product.Color, product.Price, product.Type});
        }
    }

    private static ArrayList<Product> readProductsFromFile(String fileName) {
        ArrayList<Product> productList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    Product product = new Product(parts[0], parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]), parts[6]);
                    productList.add(product);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return productList;
    }

    private static void filterTableByType(String selectedType) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        if (selectedType.equals("All")) {
            sorter.setRowFilter(null); // Show all rows
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(selectedType, 6)); // Filter by Type column (index 6)
        }
    }

    // Implement getUniqueTypes method to extract unique types from productList
    private static Set<String> getUniqueTypes(ArrayList<Product> productList) {
        Set<String> uniqueTypes = new HashSet<>();
        for (Product product : productList) {
            uniqueTypes.add(product.Type);
        }
        return uniqueTypes;
    }
}