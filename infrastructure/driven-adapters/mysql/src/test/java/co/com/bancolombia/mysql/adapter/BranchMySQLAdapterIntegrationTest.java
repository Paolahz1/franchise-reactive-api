package co.com.bancolombia.mysql.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.mysql.config.DatabaseConfiguration;
import reactor.test.StepVerifier;

@SpringBootTest(classes = {BranchMySQLAdapter.class, DatabaseConfiguration.class})
@EnableAutoConfiguration
@TestPropertySource(properties = {
        "spring.r2dbc.url=r2dbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:franchises_db}",
        "spring.r2dbc.username=${DB_USERNAME:root}",
        "spring.r2dbc.password=${DB_PASSWORD:}"
})
@DisplayName("BranchMySQLAdapter - Integration Test (Local MySQL)")
class BranchMySQLAdapterIntegrationTest {

    @Autowired
    private BranchMySQLAdapter branchAdapter;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        databaseClient.sql("DELETE FROM branches").fetch().rowsUpdated().block();
        databaseClient.sql("DELETE FROM franchises").fetch().rowsUpdated().block();
    }

    @Test
    @DisplayName("Should save branch successfully")
    void shouldSaveBranchSuccessfully() {
        // Given
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Test Franchise")
                .fetch()
                .rowsUpdated()
                .block();

        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Test Franchise")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        Branch newBranch = Branch.builder()
                .name("Downtown Branch")
                .franchiseId(franchiseId)
                .build();

        // When & Then
        StepVerifier.create(branchAdapter.save(newBranch))
                .expectNextMatches(savedBranch ->
                        savedBranch.getId() != null &&
                        "Downtown Branch".equals(savedBranch.getName()) &&
                        savedBranch.getFranchiseId().equals(franchiseId)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find branch by ID")
    void shouldFindBranchByIdSuccessfully() {
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
                .bind("name", "Test Branch")
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .block();

        Long branchId = databaseClient
                .sql("SELECT id FROM branches WHERE name = :name")
                .bind("name", "Test Branch")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // When & Then
        StepVerifier.create(branchAdapter.findById(branchId))
                .expectNextMatches(branch ->
                        branch.getId().equals(branchId) &&
                        "Test Branch".equals(branch.getName())
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find branch by name and franchise ID")
    void shouldFindBranchByNameAndFranchiseIdSuccessfully() {
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
                .bind("name", "Unique Branch")
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .block();

        // When & Then
        StepVerifier.create(branchAdapter.findByNameAndFranchiseId("Unique Branch", franchiseId))
                .expectNextMatches(branch ->
                        "Unique Branch".equals(branch.getName()) &&
                        branch.getFranchiseId().equals(franchiseId)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update branch name")
    void shouldUpdateBranchNameSuccessfully() {
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
                .bind("name", "Old Branch Name")
                .bind("franchiseId", franchiseId)
                .fetch()
                .rowsUpdated()
                .block();

        Long branchId = databaseClient
                .sql("SELECT id FROM branches WHERE name = :name")
                .bind("name", "Old Branch Name")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // When
        StepVerifier.create(branchAdapter.updateName(branchId, "New Branch Name"))
                .verifyComplete();

        // Then
        String updatedName = databaseClient
                .sql("SELECT name FROM branches WHERE id = :id")
                .bind("id", branchId)
                .map(row -> row.get("name", String.class))
                .one()
                .block();

        assert "New Branch Name".equals(updatedName);
    }
}
