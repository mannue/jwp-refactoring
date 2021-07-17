package kitchenpos.acceptance;

import static kitchenpos.acceptance.ProductAcceptanceTestMethod.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Product;

public class ProductAcceptanceTest extends AcceptanceTest {
	@DisplayName("상품 등록 및 조회 시나리오")
	@Test
	void createProductAndFindProductScenario() {
		// Scenario
		// When
		ExtractableResponse<Response> productCreatedResponse = createProduct(new Product("매운 라면", new BigDecimal(8000)));
		Product createdProduct = productCreatedResponse.as(Product.class);
		// Then
		assertThat(productCreatedResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
		assertThat(createdProduct.getName()).isEqualTo("매운 라면");

		// When
		ExtractableResponse<Response> findProductResponse = findProduct();
		// Then
		String productName = findProductResponse.jsonPath().getList(".", Product.class).stream()
			.filter(product -> product.getId() == createdProduct.getId())
			.map(Product::getName)
			.findFirst()
			.get();
		assertThat(productName).isEqualTo("매운 라면");
	}

	@DisplayName("상품 오류 시나리오")
	@Test
	void productErrorScenario() {
		// Scenario
		// When
		ExtractableResponse<Response> productWithMunusPriceResponse = createProduct(new Product("매운 라면", new BigDecimal(-1000)));
		// Then
		assertThat(productWithMunusPriceResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}