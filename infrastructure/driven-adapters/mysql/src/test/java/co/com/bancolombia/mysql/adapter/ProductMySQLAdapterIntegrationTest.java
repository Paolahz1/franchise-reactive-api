package co.com.bancolombia.mysql.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.mysql.config.DatabaseConfiguration;
import reactor.test.StepVerifier;

@SpringBootTest(classes = {ProductMySQLAdapter.class, DatabaseConfiguration.class})
@EnableAutoConfiguration
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:franchises_db}",
        "spring.r2dbc.username=${DB_USERNAME:root}",
        "spring.r2dbc.password=${DB_PASSWORD:}"
})
@DisplayName("ProductMySQLAdapter - Integration Test (Local MySQL)")
class ProductMySQLAdapterIntegrationTest {

    @Autowired
    private ProductMySQLAdapter productAdapter;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        databaseClient.sql("DELETE FROM products").fetch().rowsUpdated().block();
        databaseClient.sql("DELETE FROM branches").fetch().rowsUpdated().block();
        databaseClient.sql("DELETE FROM franchises").fetch().rowsUpdated().block();
    }

    @Test
    @DisplayName("Should save product successfully")
    void shouldSaveProductSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Franchise")
                .fetch()
                .rowsUpdated()
                .block();

        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Franchise")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO branches (name, franchise_id) VALUES (:name, :franchiseId)")
                .bind("name", "Branch")
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .block();

        Long branchId = databaseClient
                .sql("SELECT id FROM branches WHERE name = :name")
                .bind("name", "Branch")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        Product newProduct = Product.builder()
                .name("Product A")
                .stock(100)
                .branchId(branchId)
                .build();

        // When & Then
        StepVerifier.create(productAdapter.save(newProduct))
                .expectNextMatches(savedProduct ->
                        savedProduct.getId() != null &&
                        "Product A".equals(savedProduct.getName()) &&
                        savedProduct.getStock().equals(100) &&
                        savedProduct.getBranchId().equals(branchId)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find product by ID")
    void shouldFindProductByIdSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Franchise")
                .fetch()
                .rowsUpdated()
                .block();

        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Franchise")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO branches (name, franchise_id) VALUES (:name, :franchiseId)")
                .bind("name", "Branch")
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .block();

        Long branchId = databaseClient
                .sql("SELECT id FROM branches WHERE name = :name")
                .bind("name", "Branch")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO products (name, stock, branch_id) VALUES (:name, :stock, :branchId)")
                .bind("name", "Test Product")
                .bind("stock", 50)
                .bind("branchId", branchId)
                .fetch()
                .rowsUpdated()
                .block();

        Long productId = databaseClient
                .sql("SELECT id FROM products WHERE name = :name")
                .bind("name", "Test Product")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // When & Then
        StepVerifier.create(productAdapter.findById(productId))
                .expectNextMatches(product ->
                        product.getId().equals(productId) &&
                        "Test Product".equals(product.getName()) &&
                        product.getStock().equals(50)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update product name")
    void shouldUpdateProductNameSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Franchise")
                .fetch()
                .rowsUpdated()
                .block();

        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Franchise")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO branches (name, franchise_id) VALUES (:name, :franchiseId)")
                .bind("name", "Branch")
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .block();

        Long branchId = databaseClient
                .sql("SELECT id FROM branches WHERE name = :name")
                .bind("name", "Branch")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO products (name, stock, branch_id) VALUES (:name, :stock, :branchId)")
                .bind("name", "Old Product")
                .bind("stock", 100)
                .bind("branchId", branchId)
                .fetch()
                .rowsUpdated()
                .block();

        Long productId = databaseClient
                .sql("SELECT id FROM products WHERE name = :name")
                .bind("name", "Old Product")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // When
        StepVerifier.create(productAdapter.updateName(productId, "New Product"))
                .verifyComplete();

        // Then
        String updatedName = databaseClient
                .sql("SELECT name FROM products WHERE id = :id")
                .bind("id", productId)
                .map(row -> row.get("name", String.class))
                .one()
                .block();

        assert "New Product".equals(updatedName);
    }

    @Test
    @DisplayName("Should update product stock")
    void shouldUpdateProductStockSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Franchise")
                .fetch()
                .rowsUpdated()
                .block();

        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Franchise")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO branches (name, franchise_id) VALUES (:name, :franchiseId)")
                .bind("name", "Branch")
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .block();

        Long branchId = databaseClient
                .sql("SELECT id FROM branches WHERE name = :name")
                .bind("name", "Branch")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO products (name, stock, branch_id) VALUES (:name, :stock, :branchId)")
                .bind("name", "Product")
                .bind("stock", 100)
                .bind("branchId", branchId)
                .fetch()
                .rowsUpdated()
                .block();

        Long productId = databaseClient
                .sql("SELECT id FROM products WHERE name = :name")
                .bind("name", "Product")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // When
        StepVerifier.create(productAdapter.updateStock(productId, 200))
                .verifyComplete();

        // Then
        Integer updatedStock = databaseClient
                .sql("SELECT stock FROM products WHERE id = :id")
                .bind("id", productId)
                .map(row -> row.get("stock", Integer.class))
                .one()
                .block();

        assert updatedStock != null && updatedStock.equals(200);
    }

    @Test
    @DisplayName("Should delete product by ID")
    void shouldDeleteProductByIdSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Franchise")
                .fetch()
                .rowsUpdated()
                .block();

        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Franchise")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO branches (name, franchise_id) VALUES (:name, :franchiseId)")
                .bind("name", "Branch")
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .block();

        Long branchId = databaseClient
                .sql("SELECT id FROM branches WHERE name = :name")
                .bind("name", "Branch")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        databaseClient
                .sql("INSERT INTO products (name, stock, branch_id) VALUES (:name, :stock, :branchId)")
                .bind("name", "Product to Delete")
                .bind("stock", 50)
                .bind("branchId", branchId)
                .fetch()
                .rowsUpdated()
                .block();

        Long productId = databaseClient
                .sql("SELECT id FROM products WHERE name = :name")
                .bind("name", "Product to Delete")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // When
        StepVerifier.create(productAdapter.deleteById(productId))
                .verifyComplete();

        // Then
        Long count = databaseClient
                .sql("SELECT COUNT(*) FROM products WHERE id = :id")
                .bind("id", productId)
                .map(row -> row.get(0, Long.class))
                .one()
                .block();

        assert count != null && count == 0L;
    }
}
