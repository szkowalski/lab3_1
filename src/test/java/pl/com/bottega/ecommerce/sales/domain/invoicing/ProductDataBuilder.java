package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

public class ProductDataBuilder {

    private Id productId;
    private Money price;

    private String name;

    private Date snapshotDate;

    private ProductType type;

    public ProductDataBuilder productId(Id productId) {
        this.productId = productId;
        return this;
    }

    public ProductDataBuilder price(Money price) {
        this.price = price;
        return this;
    }

    public ProductDataBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductDataBuilder snapshotDate(Date snapshotDate) {
        this.snapshotDate = snapshotDate;
        return this;
    }

    public ProductDataBuilder type(ProductType type) {
        this.type = type;
        return this;
    }

    public ProductData build() {
        ProductData productData = new ProductData(productId, price, name, type, snapshotDate);
        return productData;
    }
}