package co.com.bancolombia.usecase.createfranchise;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;    
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;    
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension; // Herramienta para verificar flujos reactivos

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@ExtendWith(MockitoExtension.class)
@DisplayName("CreateFranchiseUseCase - Unit Tests")
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private CreateFranchiseUseCase createFranchiseUseCase;

    @Test
    @DisplayName("Should create a franchise successfully when the name is valid and does not exist")
    void shouldCreateFranchiseSuccessfully_WhenNameIsValidAndDoesNotExist() {
       
        
        Franchise franchise = Franchise.builder().name("Starbucks").build();
        
        Franchise expectedFranchise = Franchise.builder()
                .id(1L)
                .name("Starbucks")
                .build();


        when(franchiseRepository.findByName(anyString())).thenReturn(Mono.empty());
        
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(expectedFranchise));

        Mono<Franchise> result = createFranchiseUseCase.execute(franchise);

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


        // Verificar que se consultó si la franquicia existe
        verify(franchiseRepository, times(1))
                .findByName(franchise.getName());

        // Verificar que se guardó una franquicia
        verify(franchiseRepository, times(1))
                .save(any(Franchise.class));


        // Verificar que NO se llamaron otros métodos no esperados
        verifyNoMoreInteractions(franchiseRepository);


    }

    // ========================================================================
    // PLANTILLA PARA OTROS TESTS (por implementar)
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
