package co.com.bancolombia.mysql.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Spring Boot Test - Para levantar contexto de Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

// Testcontainers - Para levantar base de datos real en Docker
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// Reactor Test - Para testing reactivo
import reactor.test.StepVerifier;

// Clases del dominio
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.mysql.config.DatabaseConfiguration;

/**
 * ============================================================================
 * TEST DE INTEGRACIÓN vs TEST UNITARIO
 * ============================================================================
 * 
 * TEST UNITARIO (domain/usecase):
 * ✅ Prueba LÓGICA DE NEGOCIO aislada
 * ✅ USA MOCKS (objetos falsos simulados)
 * ✅ NO accede a base de datos real
 * ✅ Rápido (milisegundos)
 * ✅ Verifica: "¿El use case funciona correctamente?"
 * 
 * TEST DE INTEGRACIÓN (infrastructure/mysql):
 * ✅ Prueba ACCESO A DATOS real
 * ✅ USA BASE DE DATOS REAL (MySQL en Docker vía Testcontainers)
 * ✅ SÍ ejecuta queries SQL reales
 * ✅ Más lento (segundos - levanta Docker container)
 * ✅ Verifica: "¿El adapter guarda/recupera datos correctamente?"
 * 
 * ============================================================================
 * ¿QUÉ ES TESTCONTAINERS?
 * ============================================================================
 * 
 * Testcontainers es una librería Java que:
 * 1. Levanta automáticamente un contenedor Docker con MySQL REAL
 * 2. Ejecuta tus tests contra esa base de datos temporal
 * 3. Al terminar el test, DESTRUYE el contenedor (limpieza automática)
 * 
 * Ventajas:
 * ✅ Base de datos IDÉNTICA a producción (no H2 in-memory)
 * ✅ Tests más confiables (detectan problemas de SQL real)
 * ✅ No ensucias tu MySQL local
 * ✅ Cada test empieza con BD limpia
 * 
 * Requisitos:
 * - Docker Desktop instalado y corriendo
 * - Dependencia: testcontainers:mysql:1.19.3
 * 
 * ============================================================================
 */

/**
 * ANOTACIONES CLAVE:
 * 
 * @Testcontainers
 * Activa la integración de Testcontainers con JUnit.
 * Permite usar @Container para manejar contenedores Docker.
 * 
 * @SpringBootTest
 * Levanta el contexto COMPLETO de Spring (como si fuera la app real).
 * Carga todos los beans: repositories, mappers, configuration, etc.
 * DIFERENCIA con test unitario: En unitario NO levantamos Spring.
 * 
 * @Import(DatabaseConfiguration.class)
 * Importa la configuración de R2DBC necesaria para conectar a MySQL.
 */
@Testcontainers
@SpringBootTest(classes = {FranchiseMySQLAdapter.class, DatabaseConfiguration.class})
@EnableAutoConfiguration
@DisplayName("FranchiseMySQLAdapter - Integration Test (Real Database)")
class FranchiseMySQLAdapterIntegrationTest {

    // ========================================================================
    // TESTCONTAINERS - Configuración de MySQL en Docker
    // ========================================================================

    /**
     * @Container
     * Define un contenedor Docker que será:
     * 1. Iniciado ANTES de los tests
     * 2. Detenido y eliminado DESPUÉS de los tests
     * 
     * MySQLContainer<?> es una implementación específica para MySQL.
     * - Usa la imagen oficial de MySQL 8.0
     * - Configura automáticamente usuario/password/database
     * - Expone puerto aleatorio para evitar conflictos
     * 
     * withReuse(true):
     * - Reutiliza el mismo contenedor entre tests (más rápido)
     * - Requiere configurar Testcontainers en ~/.testcontainers.properties
     */
    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("schema.sql");

    /**
     * @DynamicPropertySource
     * Inyecta propiedades dinámicas al contexto de Spring.
     * 
     * ¿Por qué dinámicas?
     * Porque el puerto del contenedor Docker es ALEATORIO.
     * No sabemos el puerto hasta que el contenedor se levante.
     * 
     * Este método se ejecuta DESPUÉS de iniciar el contenedor
     * pero ANTES de crear los beans de Spring.
     * 
     * Sobrescribe las propiedades de application.yaml con valores reales:
     * - spring.r2dbc.url → jdbc:mysql://localhost:PUERTO_ALEATORIO/testdb
     * - spring.r2dbc.username → test
     * - spring.r2dbc.password → test
     */
    @DynamicPropertySource
    static void configureDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://" + mysqlContainer.getHost() + ":" + mysqlContainer.getMappedPort(3306) + "/testdb");
        registry.add("spring.r2dbc.username", mysqlContainer::getUsername);
        registry.add("spring.r2dbc.password", mysqlContainer::getPassword);
    }

    // ========================================================================
    // INYECCIÓN DE DEPENDENCIAS - Spring hace el trabajo
    // ========================================================================

    /**
     * @Autowired
     * Spring inyecta automáticamente el adapter REAL (no mock).
     * 
     * Este adapter está conectado a la base de datos MySQL
     * que corre en el contenedor Docker de Testcontainers.
     * 
     * DIFERENCIA con test unitario:
     * - Unitario: @Mock (objeto falso)
     * - Integración: @Autowired (objeto real con BD real)
     */
    @Autowired
    private FranchiseMySQLAdapter franchiseAdapter;

    /**
     * DatabaseClient de Spring R2DBC.
     * Permite ejecutar SQL directo para:
     * - Preparar datos de prueba
     * - Limpiar la base de datos entre tests
     * - Verificar resultados con queries custom
     */
    @Autowired
    private DatabaseClient databaseClient;

    // ========================================================================
    // SETUP - Preparar el ambiente antes de cada test
    // ========================================================================

    /**
     * @BeforeEach
     * Se ejecuta ANTES de cada método @Test.
     * 
     * Limpia la tabla 'franchises' para garantizar:
     * - Cada test empieza con BD limpia
     * - Tests son INDEPENDIENTES entre sí
     * - No hay "contaminación" de datos entre tests
     * 
     * IMPORTANTE:
     * En test de integración es CRÍTICO limpiar datos.
     * Si no lo haces, un test puede afectar a otro (flaky tests).
     */
    @BeforeEach
    void setUp() {
        // Ejecutar SQL para limpiar la tabla
        databaseClient.sql("DELETE FROM franchises")
                .fetch()
                .rowsUpdated()
                .block(); // block() porque setup es síncrono
    }

    // ========================================================================
    // TESTS - Casos de éxito
    // ========================================================================

    /**
     * TEST 1: Guardar una franquicia en la base de datos real
     * 
     * Este test verifica el flujo COMPLETO:
     * 1. Crear objeto Franchise (dominio)
     * 2. Adapter lo convierte a FranchiseEntity (con Mapper)
     * 3. R2DBC ejecuta INSERT en MySQL (query SQL real)
     * 4. MySQL devuelve el ID generado
     * 5. Adapter convierte Entity de vuelta a Franchise (dominio)
     * 6. Test verifica que el ID se generó correctamente
     * 
     * IMPORTANTE:
     * Este test REALMENTE escribe en MySQL.
     * Si falla, puede ser por:
     * - Error en el Mapper (toEntity/toDomain)
     * - Error en el schema de BD (columnas incorrectas)
     * - Error en R2DBC configuration
     * - Error en la query SQL generada por Spring Data
     */
    @Test
    @DisplayName("Should save franchise successfully to real database")
    void shouldSaveFranchiseSuccessfully() {
        // GIVEN - Preparar datos de entrada
        Franchise newFranchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        // WHEN - Ejecutar el método bajo test
        // save() ejecuta un INSERT SQL real en MySQL
        
        // THEN - Verificar resultados con StepVerifier
        StepVerifier.create(franchiseAdapter.save(newFranchise))
                // Verificar que recibimos 1 elemento
                .expectNextMatches(savedFranchise -> {
                    // Verificaciones:
                    // 1. El ID fue generado por MySQL (auto_increment)
                    boolean hasId = savedFranchise.getId() != null;
                    
                    // 2. El nombre se guardó correctamente
                    boolean nameMatches = "Test Franchise".equals(savedFranchise.getName());
                    
                    // Si ambas son true, el test pasa
                    return hasId && nameMatches;
                })
                // Verificar que el flujo completó sin errores
                .verifyComplete();
        
        // VERIFICACIÓN ADICIONAL: Consultar BD directamente
        // Esto confirma que el registro REALMENTE está en MySQL
        Long count = databaseClient
                .sql("SELECT COUNT(*) FROM franchises WHERE name = :name")
                .bind("name", "Test Franchise")
                .map(row -> row.get(0, Long.class))
                .one()
                .block();
        
        // Debe haber exactamente 1 registro
        assert count != null && count == 1L : "Franchise should exist in database";
    }

    /**
     * TEST 2: Buscar franquicia por ID
     * 
     * Verifica el flujo:
     * 1. Insertar datos de prueba en MySQL
     * 2. Buscar por ID usando el adapter
     * 3. Verificar que se recuperó correctamente
     */
    @Test
    @DisplayName("Should find franchise by ID from real database")
    void shouldFindFranchiseByIdSuccessfully() {
        // GIVEN - Preparar datos de prueba insertando directamente en BD
        // Primero insertamos
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Existing Franchise")
                .fetch()
                .rowsUpdated()
                .block();

        // Luego obtenemos el ID
        Long franchiseId = databaseClient
                .sql("SELECT id FROM franchises WHERE name = :name")
                .bind("name", "Existing Franchise")
                .map(row -> row.get("id", Long.class))
                .one()
                .block();

        // WHEN - Ejecutar búsqueda por ID
        // findById() ejecuta un SELECT SQL real
        
        // THEN - Verificar resultados
        StepVerifier.create(franchiseAdapter.findById(franchiseId))
                .expectNextMatches(franchise -> 
                    franchise.getId().equals(franchiseId) &&
                    "Existing Franchise".equals(franchise.getName())
                )
                .verifyComplete();
    }

    /**
     * TEST 3: Buscar franquicia por nombre
     * 
     * Verifica queries con WHERE personalizado
     */
    @Test
    @DisplayName("Should find franchise by name from real database")
    void shouldFindFranchiseByNameSuccessfully() {
        // GIVEN
        databaseClient
                .sql("INSERT INTO franchises (name) VALUES (:name)")
                .bind("name", "Unique Franchise Name")
                .fetch()
                .rowsUpdated()
                .block();

        // WHEN
        StepVerifier.create(franchiseAdapter.findByName("Unique Franchise Name"))
                .expectNextMatches(franchise -> 
                    "Unique Franchise Name".equals(franchise.getName())
                )
                .verifyComplete();
    }

    /**
     * TEST 4: Actualizar nombre de franquicia
     * 
     * Verifica UPDATE SQL real
     */
    @Test
    @DisplayName("Should update franchise name in real database")
    void shouldUpdateFranchiseNameSuccessfully() {
        // GIVEN - Insertar franquicia inicial
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

        // WHEN - Actualizar nombre
        StepVerifier.create(franchiseAdapter.updateName(franchiseId, "New Name"))
                .verifyComplete();

        // THEN - Verificar que el cambio se persistió en BD
        String updatedName = databaseClient
                .sql("SELECT name FROM franchises WHERE id = :id")
                .bind("id", franchiseId)
                .map(row -> row.get("name", String.class))
                .one()
                .block();

        assert "New Name".equals(updatedName) : "Name should be updated in database";
    }

    // ========================================================================
    // TEST CASO BORDE: Buscar franquicia que no existe
    // ========================================================================

    /**
     * TEST 5: Buscar franquicia inexistente
     * 
     * Verifica que retorna Mono.empty() cuando no encuentra resultados
     */
    @Test
    @DisplayName("Should return empty when franchise not found")
    void shouldReturnEmptyWhenFranchiseNotFound() {
        // WHEN - Buscar ID que no existe
        StepVerifier.create(franchiseAdapter.findById(99999L))
                // THEN - Debe completar sin emitir elementos
                .expectNextCount(0)
                .verifyComplete();
    }
}

/**
 * ============================================================================
 * RESUMEN: Diferencias TEST UNITARIO vs TEST DE INTEGRACIÓN
 * ============================================================================
 * 
 * | Aspecto              | Test Unitario (UseCase)        | Test Integración (Adapter)      |
 * |----------------------|--------------------------------|---------------------------------|
 * | Objetivo             | Lógica de negocio             | Acceso a datos                  |
 * | Dependencias         | Mocks (@Mock)                 | Reales (@Autowired)             |
 * | Base de datos        | NO                            | SÍ (MySQL en Docker)            |
 * | Spring Context       | NO (@ExtendWith(Mockito))     | SÍ (@SpringBootTest)            |
 * | Velocidad            | Muy rápido (ms)               | Lento (segundos)                |
 * | Aislamiento          | Total (sin infraestructura)   | Parcial (con BD real)           |
 * | Verifica             | "¿Funciona la lógica?"        | "¿Funciona con BD real?"        |
 * | Falla si             | Bug en lógica de negocio      | Bug en SQL/Mapper/Schema        |
 * 
 * ============================================================================
 * ¿CUÁNDO USAR CADA UNO?
 * ============================================================================
 * 
 * TEST UNITARIO:
 * ✅ Lógica de negocio compleja (validaciones, cálculos)
 * ✅ Casos de error (BusinessException)
 * ✅ Flujos reactivos con múltiples operadores
 * ✅ Desarrollo TDD (escribir test antes que código)
 * ✅ Ejecución rápida en CI/CD (miles de tests en segundos)
 * 
 * TEST DE INTEGRACIÓN:
 * ✅ Verificar queries SQL complejas (JOINs, subqueries)
 * ✅ Validar que el schema de BD es correcto
 * ✅ Probar transacciones y rollbacks
 * ✅ Detectar problemas de performance (N+1 queries)
 * ✅ Antes de deployar a producción (smoke tests)
 * 
 * PIRÁMIDE DE TESTING:
 * 
 *           /\
 *          /E2E\        <- Pocos tests end-to-end (muy lentos)
 *         /------\
 *        /Integra.\     <- Algunos tests de integración
 *       /----------\
 *      /  Unitarios \   <- MUCHOS tests unitarios (base de la pirámide)
 *     /--------------\
 * 
 * REGLA: 70% unitarios, 20% integración, 10% E2E
 * 
 * ============================================================================
 */
