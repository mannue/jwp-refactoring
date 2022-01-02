package kitchenpos.product.dto;

import kitchenpos.product.domain.Price;
import kitchenpos.product.domain.Product;

import java.math.BigDecimal;

public class ProductResponse {
    private Long id;

    private String name;

    private BigDecimal price;

    public ProductResponse(String name, Price price) {
        this.name = name;
        this.price = price.toBigDecimal();
    }

    public static ProductResponse from(Product savedProduct) {
        return new ProductResponse(savedProduct.getName(), savedProduct.getPrice());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}