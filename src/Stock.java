public class Stock{
    String productSKU;
    int quantityAvailable,quantityRequested;

    public Stock(String productSKU, int quantityAvailable, int quantityRequested) {
        this.productSKU = productSKU;
        this.quantityAvailable = quantityAvailable;
        this.quantityRequested = quantityRequested;
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
}
