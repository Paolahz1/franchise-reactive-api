package co.com.bancolombia.mysql.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.mysql.config.DatabaseConfiguration;
import reactor.test.StepVerifier;

/**
 * ============================================================================
 * TEST DE INTEGRACIÓN - FranchiseMySQLAdapter
 * ============================================================================
 *
 * Se conecta al MySQL LOCAL instalado en tu máquina.
 * No requiere Docker ni Testcontainers.
 *
 * Las credenciales se leen de variables de entorno definidas en el
 * application.yaml del proyecto (DB_HOST, DB_PORT, DB_NAME, DB_USERNAME,
 * DB_PASSWORD). Los valores por defecto apuntan a MySQL local.
 *
 * PRE-REQUISITOS:
 * 1. MySQL corriendo en localhost:3306
 * 2. Base de datos creada:
 *      mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS franchise_test_db"
 * 3. Schema cargado:
 *      mysql -u root -p franchise_test_db < infrastructure/driven-adapters/mysql/src/test/resources/schema.sql
 *
 * Si tus credenciales difieren de los defaults, crea un archivo .env en la
 * raíz del proyecto (ver .env.example) o exporta las variables:
 *      export DB_PASSWORD=TuPassword
 *
 * ============================================================================
 */
@SpringBootTest(classes = {FranchiseMySQLAdapter.class, DatabaseConfiguration.class})
@EnableAutoConfiguration
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:franchises_db}",
        "spring.r2dbc.username=${DB_USERNAME:root}",
        "spring.r2dbc.password=${DB_PASSWORD:}"
})
@DisplayName("FranchiseMySQLAdapter - Integration Test (Local MySQL)")
class FranchiseMySQLAdapterIntegrationTest {

    @Autowired
    private FranchiseMySQLAdapter franchiseAdapter;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        databaseClient.sql("DELETE FROM franchises")
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    @DisplayName("Should save franchise successfully to real database")
    void shouldSaveFranchiseSuccessfully() {
        // Given
        Franchise newFranchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        // When & Then
        StepVerifier.create(franchiseAdapter.save(newFranchise))
                .expectNextMatches(savedFranchise ->
                        savedFranchise.getId() != null &&
                        "Test Franchise".equals(savedFranchise.getName())
                )
                .verifyComplete();

        // Verify it actually exists in the database
        Long count = databaseClient
                .sql("SELECT COUNT(*) FROM franchises WHERE name = :name")
                .bind("name", "Test Franchise")
                .map(row -> row.get(0, Long.class))
                .one()
                .block();

        assert count != null && count == 1L : "Franchise should exist in database";
    }

    @Test
    @DisplayName("Should find franchise by ID from real database")
    void shouldFindFranchiseByIdSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Existing Franchise")
                .fetch()
                .rowsUpdated()
                .block();

        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Existing Franchise")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // When & Then
        StepVerifier.create(franchiseAdapter.findById(franchiseId))
                .expectNextMatches(franchise ->
                        franchise.getId().equals(franchiseId) &&
                        "Existing Franchise".equals(franchise.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find franchise by name from real database")
    void shouldFindFranchiseByNameSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Unique Franchise Name")
                .fetch()
                .rowsUpdated()
                .block();

        // When & Then
        StepVerifier.create(franchiseAdapter.findByName("Unique Franchise Name"))
                .expectNextMatches(franchise ->
                        "Unique Franchise Name".equals(franchise.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update franchise name in real database")
    void shouldUpdateFranchiseNameSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Old Name")
                .fetch()
                .rowsUpdated()
                .block();

        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Old Name")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // When
        StepVerifier.create(franchiseAdapter.updateName(franchiseId, "New Name"))
                .verifyComplete();

        // Then
        String updatedName = databaseClient
                .sql("SELECT name FROM franchises WHERE id = :id")
                .bind("id", franchiseId)
                .map(row -> row.get("name", String.class))
                .one()
                .block();

        assert "New Name".equals(updatedName) : "Name should be updated in database";
    }

    @Test
    @DisplayName("Should return empty when franchise not found")
    void shouldReturnEmptyWhenFranchiseNotFound() {
        StepVerifier.create(franchiseAdapter.findById(99999L))
                .expectNextCount(0)
                .verifyComplete();
    }
}
