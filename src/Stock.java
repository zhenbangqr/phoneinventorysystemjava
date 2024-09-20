import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Stock{
    String productSKU;
    int quantityAvailable,quantityRequested;

    public Stock(String productSKU, int quantityAvailable, int quantityRequested) {
        this.productSKU = productSKU;
        this.quantityAvailable = quantityAvailable;
        this.quantityRequested = quantityRequested;
    }

    public Stock(String productSKU, int quantityAvailable) {
        this.productSKU = productSKU;
        this.quantityAvailable = quantityAvailable;
    }

    public String getSKU() {
        return productSKU;
    }

    public void setSKU(String SKU) {
        this.productSKU = SKU;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public int getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(int quantityRequested) {
        this.quantityRequested = quantityRequested;
    }

    //done by fattsiong, for creating an array list for current available stock in branches
    public static List<PurchaseOrder> createCurrentStockArrayList(String warehouseID) {
        List<PurchaseOrder> currentStock = new ArrayList<>();

        String fileName = "aux_files/branchStock_txt/" + warehouseID + ".txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // skip the header line

            while ((line = br.readLine()) != null) {
                String[] details = line.split("\\|");

                // Assuming the file contains productSKU and quantity
                String productSKU = details[0];
                int quantity = Integer.parseInt(details[1]);

                // Create a PurchaseOrder object for the product
                PurchaseOrder purchaseOrder = new PurchaseOrder(productSKU, quantity);

                // Add to the list
                currentStock.add(purchaseOrder);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "File not found.");
        }
        return currentStock;
    }
}
