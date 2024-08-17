import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Product {
    String SKU, Model, RAM, ROM, Color;
    int Price;

    Product(String SKU, String Model, String RAM, String ROM, String Color, int Price) {
        this.SKU = SKU;
        this.Model = Model;
        this.RAM = RAM;
        this.ROM = ROM;
        this.Color = Color;
        this.Price = Price;
    }

    @Override
    public String toString() {
        return "SKU: " + SKU + ", Model: " + Model + ", RAM: " + RAM + ", ROM: " + ROM + ", Color: " + Color + ", Price: " + Price;
    }
}

public class ReadFromFile {
    public static void main(String[] args) {
        String fileName = "POCO.txt"; // Replace with your actual file name
        ArrayList<Product> productList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // Skip the header line

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    Product product = new Product(parts[0], parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5]));
                    productList.add(product);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Display the products
        for (Product product : productList) {
            System.out.println(product);
        }
    }
}