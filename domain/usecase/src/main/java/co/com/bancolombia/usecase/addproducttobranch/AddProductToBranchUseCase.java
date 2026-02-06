package co.com.bancolombia.usecase.addproducttobranch;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.common.enums.TechnicalMessage;
import co.com.bancolombia.model.common.exceptions.BusinessException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddProductToBranchUseCase {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<Product> execute(Long branchId, String productName, Integer stock) {
        return Mono.justOrEmpty(productName)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_NAME_EMPTY))))
                .flatMap(trimmedName -> 
                    Mono.justOrEmpty(stock)
                        .filter(s -> s >= 0)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.PRODUCT_STOCK_INVALID))))
                        .flatMap(validStock -> 
                            branchRepository.findById(branchId)
                                .switchIfEmpty(Mono.defer(() -> Mono.error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND))))
                                .flatMap(branch -> 
                                    productRepository.findByNameAndBranchId(trimmedName, branchId)
                                        .flatMap(existing -> Mono.defer(() -> 
                                            Mono.<Product>error(new BusinessException(TechnicalMessage.PRODUCT_NAME_DUPLICATE))))
                                        .switchIfEmpty(Mono.defer(() -> {
                                            Product newProduct = Product.builder()
                                                    .name(trimmedName)
                                                    .stock(validStock)
                                                    .branchId(branchId)
                                                    .build();
                                            return productRepository.save(newProduct);
                                        }))
                                )
                        )
                );
    }
}
