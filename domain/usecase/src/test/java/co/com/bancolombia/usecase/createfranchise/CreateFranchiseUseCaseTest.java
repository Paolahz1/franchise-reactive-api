package co.com.bancolombia.usecase.createfranchise;

// ============================================================================
// IMPORTS - Librerías necesarias para el test
// ============================================================================

// JUnit 5 - Framework de testing
import org.junit.jupiter.api.BeforeEach;           // Para setup antes de cada test
import org.junit.jupiter.api.DisplayName;          // Para nombres descriptivos en reportes
import org.junit.jupiter.api.Test;                 // Para marcar métodos como tests
import org.junit.jupiter.api.extension.ExtendWith; // Para extensiones de JUnit

// Mockito - Framework para crear mocks (objetos simulados)
import org.mockito.InjectMocks;  // Para inyectar mocks en la clase bajo test
import org.mockito.Mock;          // Para crear objetos mock
import org.mockito.junit.jupiter.MockitoExtension; // Integración Mockito + JUnit 5
import static org.mockito.ArgumentMatchers.any;    // Matchers para argumentos
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;               // Métodos estáticos de Mockito

// Reactor Test - Para testing de código reactivo
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier; // Herramienta para verificar flujos reactivos

// Clases del dominio
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;

/**
 * ============================================================================
 * TEST UNITARIO vs TEST DE INTEGRACIÓN - Diferencias clave
 * ============================================================================
 * 
 * TEST UNITARIO (este archivo):
 * ✅ Prueba UNA SOLA unidad de código aislada (un use case)
 * ✅ NO accede a recursos externos (base de datos, APIs, archivos)
 * ✅ USA MOCKS para simular dependencias (repositorios, servicios)
 * ✅ Es RÁPIDO (milisegundos)
 * ✅ Se ejecuta en MEMORIA
 * ✅ Verifica LÓGICA DE NEGOCIO
 * 
 * TEST DE INTEGRACIÓN:
 * ❌ Prueba MÚLTIPLES componentes juntos
 * ❌ SÍ accede a recursos reales (base de datos H2/Testcontainers, APIs)
 * ❌ NO usa mocks (o muy pocos)
 * ❌ Es MÁS LENTO (segundos/minutos)
 * ❌ Requiere INFRAESTRUCTURA (contenedores, BD temporal)
 * ❌ Verifica INTEGRACIÓN entre componentes
 * 
 * Ejemplo de test de integración:
 * - Levantar base de datos real con Testcontainers
 * - Ejecutar queries reales contra la BD
 * - Verificar transacciones completas end-to-end
 * 
 * ============================================================================
 */

/**
 * Clase de test para CreateFranchiseUseCase
 * 
 * @ExtendWith(MockitoExtension.class)
 * Esta anotación activa Mockito en JUnit 5, permitiendo usar @Mock y @InjectMocks
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateFranch  iseUseCase - Tests Unitarios")
class CreateFranchiseUseCaseTest {

    // ========================================================================
    // CONFIGURACIÓN DEL TEST - Mocks e inyección
    // ========================================================================

    /**
     * @Mock
     * Crea un objeto SIMULADO (mock) del repositorio.
     * 
     * ¿Qué es un Mock?
     * - Es un objeto "falso" que simula el comportamiento de una dependencia real
     * - NO hace llamadas reales a la base de datos
     * - Nosotros PROGRAMAMOS cómo debe comportarse con when().thenReturn()
     * 
     * ¿Por qué usar Mocks?
     * - Aislar el código bajo test (solo probamos el use case)
     * - No depender de infraestructura externa (BD, APIs)
     * - Tests más rápidos y confiables
     * - Podemos simular escenarios difíciles (errores, timeouts, etc.)
     */
    @Mock
    private FranchiseRepository franchiseRepository;

    /**
     * @InjectMocks
     * Crea una instancia REAL de CreateFranchiseUseCase
     * e INYECTA automáticamente los @Mock como dependencias.
     * 
     * Es equivalente a:
     * createFranchiseUseCase = new CreateFranchiseUseCase(franchiseRepository);
     * 
     * Pero de forma automática y más limpia.
     */
    @InjectMocks
    private CreateFranchiseUseCase createFranchiseUseCase;

    // ========================================================================
    // SETUP - Configuración antes de cada test
    // ========================================================================

    /**
     * @BeforeEach
     * Este método se ejecuta ANTES de cada método @Test
     * 
     * Útil para:
     * - Configurar estado inicial común
     * - Limpiar mocks
     * - Preparar datos de prueba
     * 
     * En este caso no lo necesitamos porque @ExtendWith(MockitoExtension.class)
     * ya inicializa los mocks automáticamente, pero lo dejamos como ejemplo.
     */
    @BeforeEach
    void setUp() {
        // Los mocks ya están inicializados por MockitoExtension
        // Aquí podrías agregar configuración adicional si fuera necesaria
    }

    // ========================================================================
    // TESTS - Casos de prueba
    // ========================================================================

    /**
     * ========================================================================
     * TEST 1: Caso de Éxito - Crear franquicia nueva
     * ========================================================================
     * 
     * ESCENARIO:
     * - Usuario proporciona un nombre válido "Starbucks"
     * - La franquicia NO existe en la base de datos
     * - El sistema debe crear y guardar la franquicia exitosamente
     * 
     * FLUJO:
     * 1. Use case valida el nombre (no vacío, trimmed)
     * 2. Use case consulta si existe (findByName)
     * 3. No existe, entonces crea una nueva franquicia
     * 4. Use case guarda la franquicia (save)
     * 5. Retorna la franquicia guardada
     */
    @Test
    @DisplayName("Debe crear una franquicia exitosamente cuando el nombre es válido y no existe")
    void shouldCreateFranchiseSuccessfully_WhenNameIsValidAndDoesNotExist() {
        
        // ====================================================================
        // ARRANGE (Preparar) - Configurar el escenario del test
        // ====================================================================
        
        // Datos de entrada
        String franchiseName = "Starbucks";
        
        // Franquicia esperada después de guardar (con ID asignado)
        Franchise expectedFranchise = Franchise.builder()
                .id(1L)
                .name(franchiseName)
                .build();

        // --------------------------------------------------------------------
        // CONFIGURAR COMPORTAMIENTO DE LOS MOCKS
        // --------------------------------------------------------------------
        
        /**
         * when(mock.metodo(argumentos)).thenReturn(valor)
         * 
         * "Cuando se llame a este método con estos argumentos,
         *  entonces retorna este valor"
         * 
         * IMPORTANTE en código reactivo:
         * - Siempre devolver Mono.empty(), Mono.just() o Flux
         * - NO devolver null (causaría NullPointerException)
         */
        
        // Mock 1: findByName retorna Mono.empty() (no existe)
        when(franchiseRepository.findByName(anyString()))
                .thenReturn(Mono.empty());
        
        /**
         * anyString(): Matcher de Mockito
         * - Acepta CUALQUIER String como argumento
         * - Útil cuando no nos importa el valor exacto
         * - Alternativas: eq("valor"), contains("texto"), etc.
         */

        // Mock 2: save retorna la franquicia con ID asignado
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(expectedFranchise));
        
        /**
         * any(Franchise.class): Matcher de Mockito
         * - Acepta CUALQUIER objeto de tipo Franchise
         * - Útil cuando el objeto se construye dentro del método
         */

        // ====================================================================
        // ACT (Actuar) - Ejecutar el código bajo test
        // ====================================================================
        
        Mono<Franchise> result = createFranchiseUseCase.execute(franchiseName);

        // ====================================================================
        // ASSERT (Verificar) - Comprobar que el resultado es el esperado
        // ====================================================================
        
        /**
         * StepVerifier - Herramienta de Reactor Test
         * 
         * ¿Por qué StepVerifier y no solo .block()?
         * - StepVerifier verifica el FLUJO COMPLETO de eventos reactivos
         * - Permite verificar errores, completitud, tiempo, etc.
         * - .block() solo obtiene el valor pero no verifica el comportamiento
         * 
         * Métodos de StepVerifier:
         * - expectNext(): Espera el siguiente valor emitido
         * - expectComplete(): Espera que el flujo se complete exitosamente
         * - expectError(): Espera que el flujo termine con error
         * - verifyComplete(): Ejecuta las verificaciones
         */
        
        StepVerifier.create(result)
                // Esperamos que emita la franquicia guardada
                .expectNext(expectedFranchise)
                // Esperamos que el flujo se complete sin errores
                .expectComplete()
                // Ejecuta las verificaciones
                .verify();

        // ====================================================================
        // VERIFY (Verificar interacciones) - Comprobar llamadas a mocks
        // ====================================================================
        
        /**
         * verify(mock).metodo(argumentos)
         * 
         * "Verifica que el mock fue llamado con estos argumentos"
         * 
         * ¿Por qué verificar interacciones?
         * - Asegura que el código ejecutó la lógica esperada
         * - Detecta si se olvidó llamar a un método importante
         * - Verifica el orden y cantidad de llamadas
         * 
         * Variantes:
         * - verify(mock): Verifica que se llamó 1 vez (default)
         * - verify(mock, times(2)): Verifica que se llamó 2 veces
         * - verify(mock, never()): Verifica que NUNCA se llamó
         * - verify(mock, atLeastOnce()): Al menos 1 vez
         */

        // Verificar que se consultó si la franquicia existe
        verify(franchiseRepository, times(1))
                .findByName(franchiseName);

        // Verificar que se guardó una franquicia
        verify(franchiseRepository, times(1))
                .save(any(Franchise.class));

        /**
         * Mejora alternativa - Verificar el contenido exacto del objeto guardado:
         * 
         * verify(franchiseRepository).save(argThat(franchise ->
         *     franchise.getName().equals("Starbucks")
         * ));
         * 
         * argThat(): Permite validar propiedades del argumento
         */

        // Verificar que NO se llamaron otros métodos no esperados
        verifyNoMoreInteractions(franchiseRepository);
        
        /**
         * verifyNoMoreInteractions(mock)
         * - Asegura que no hubo llamadas adicionales inesperadas
         * - Útil para detectar efectos secundarios no deseados
         * - OPCIONAL: Algunos lo consideran demasiado estricto
         */
    }

    // ========================================================================
    // PLANTILLA PARA OTROS TESTS (por implementar)
    // ========================================================================
    
    /**
     * OTROS CASOS DE PRUEBA RECOMENDADOS:
     * (Implementa estos siguiendo el patrón del test anterior)
     * 
     * 1. shouldThrowException_WhenNameIsEmpty()
     *    - Input: "" o "   "
     *    - Mock: No se llama al repositorio
     *    - Expected: BusinessException con FRANCHISE_NAME_EMPTY
     *    - Usar: .expectError(BusinessException.class)
     * 
     * 2. shouldThrowException_WhenNameIsNull()
     *    - Input: null
     *    - Expected: BusinessException con FRANCHISE_NAME_EMPTY
     * 
     * 3. shouldThrowException_WhenFranchiseAlreadyExists()
     *    - Input: "Starbucks"
     *    - Mock findByName: Retorna Mono.just(existing)
     *    - Mock save: NO se llama (usar verify(never()))
     *    - Expected: BusinessException con FRANCHISE_NAME_ALREADY_EXISTS
     * 
     * 4. shouldTrimNameBeforeSaving()
     *    - Input: "  Starbucks  "
     *    - Expected: Se guarda "Starbucks" (sin espacios)
     *    - Usar argThat() para verificar el nombre trimmed
     * 
     * PATRÓN GENERAL:
     * - Arrange: Preparar datos y configurar mocks
     * - Act: Ejecutar el método
     * - Assert: Verificar resultado con StepVerifier
     * - Verify: Verificar interacciones con verify()
     */

    // ========================================================================
    // NOTAS FINALES - Mejores prácticas
    // ========================================================================
    
    /**
     * MEJORES PRÁCTICAS DE TESTING:
     * 
     * 1. NAMING (Nomenclatura):
     *    - should[ExpectedBehavior]_When[Condition]
     *    - Describe QUÉ hace, no CÓMO lo hace
     * 
     * 2. INDEPENDENCIA:
     *    - Cada test debe ser independiente
     *    - No depender del orden de ejecución
     *    - No compartir estado entre tests
     * 
     * 3. ARRANGE-ACT-ASSERT:
     *    - Siempre seguir este patrón
     *    - Separa visualmente las secciones
     *    - Facilita lectura y mantenimiento
     * 
     * 4. UN CONCEPTO POR TEST:
     *    - Cada test verifica UNA cosa
     *    - Si falla, sabes exactamente qué se rompió
     * 
     * 5. MOCKS vs STUBS:
     *    - Mock: Verifica interacciones (con verify)
     *    - Stub: Solo retorna valores (sin verify)
     *    - En este ejemplo usamos ambos
     * 
     * 6. CÓDIGO REACTIVO:
     *    - Siempre usar StepVerifier
     *    - Nunca hacer .block() en producción
     *    - Verificar emisiones Y completitud
     * 
     * 7. COVERAGE (Cobertura):
     *    - Apunta a 80-90% de cobertura
     *    - Pero calidad > cantidad
     *    - Prueba casos felices Y casos de error
     */

    /**
     * COMANDOS ÚTILES:
     * 
     * // Ejecutar solo este test
     * ./gradlew :usecase:test --tests CreateFranchiseUseCaseTest
     * 
     * // Ejecutar este test específico
     * ./gradlew :usecase:test --tests "*shouldCreateFranchiseSuccessfully*"
     * 
     * // Ver reporte de cobertura
     * ./gradlew :usecase:test jacocoTestReport
     * open domain/usecase/build/reports/jacoco/test/html/index.html
     * 
     * // Modo watch (re-ejecuta en cambios)
     * ./gradlew :usecase:test --continuous
     */
}
