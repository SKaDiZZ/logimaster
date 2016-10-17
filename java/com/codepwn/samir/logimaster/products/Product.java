package com.codepwn.samir.logimaster.products;

/**
 * Definisi klasu product
 * @author Samir Kahvedzic * akirapowered@gmail.com
 */
public class Product {

    private String productID;
    private String productBarcode;
    private String productName;
    private String productUnit;
    private Double productPrice;

    // Product konstruktor
    public Product(String id, String barcode, String name, String unit, Double price) {
        this.setProductID(id);
        this.setProductBarcode(barcode);
        this.setProductName(name);
        this.setProductUnit(unit);
        this.setProductPrice(price);
    }

    // Vrati vrijednost productID -> id
    public String getProductID() {
        return productID;
    }

    // Postavi vrijednost productID -> id
    public void setProductID(String productID) {
        this.productID = productID;
    }

    // Vrati vrijednost productBarcode -> barkod
    public String getProductBarcode() {
        return productBarcode;
    }

    // Postavi vrijednost productBarcode -> barkod
    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    // Vrati vrijednost productName -> ime proizvoda
    public String getProductName() {
        return productName;
    }

    // Postavi vrijednost productName -> ime proizvoda
    public void setProductName(String productName) {
        this.productName = productName;
    }

    // Vrati vrijednost productUnit -> jedinicu mjere
    public String getProductUnit() {
        return productUnit;
    }

    // Postavi vrijednost productUnit -> jedinicu mjere
    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    // Vrati vrijednost productPrice -> cijenu
    public Double getProductPrice() {
        return productPrice;
    }

    // Postavi vrijednost productPrice -> cijene
    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }
}
