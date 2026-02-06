package co.com.bancolombia.config;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.getmaxstockproductsbyfranchise.GetMaxStockProductsByFranchiseUseCase;
import co.com.bancolombia.usecase.removeproductfrombranch.RemoveProductFromBranchUseCase;
import co.com.bancolombia.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.updateproductname.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(FranchiseRepository franchiseRepository) {
        return new CreateFranchiseUseCase(franchiseRepository);
    }

    @Bean
    public AddBranchToFranchiseUseCase addBranchToFranchiseUseCase(
            BranchRepository branchRepository,
            FranchiseRepository franchiseRepository) {
        return new AddBranchToFranchiseUseCase(branchRepository, franchiseRepository);
    }

    @Bean
    public AddProductToBranchUseCase addProductToBranchUseCase(
            ProductRepository productRepository,
            BranchRepository branchRepository) {
        return new AddProductToBranchUseCase(productRepository, branchRepository);
    }

    @Bean
    public RemoveProductFromBranchUseCase removeProductFromBranchUseCase(ProductRepository productRepository, BranchRepository repository) {
        return new RemoveProductFromBranchUseCase(productRepository, repository);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepository productRepository) {
        return new UpdateProductStockUseCase(productRepository);
    }

    @Bean
    public GetMaxStockProductsByFranchiseUseCase getMaxStockProductsByFranchiseUseCase(
            FranchiseRepository franchiseRepository,
            BranchRepository branchRepository,
            ProductRepository productRepository) {
        return new GetMaxStockProductsByFranchiseUseCase(franchiseRepository, productRepository, branchRepository);
    }

    @Bean
    public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseRepository franchiseRepository) {
        return new UpdateFranchiseNameUseCase(franchiseRepository);
    }

    @Bean
    public UpdateBranchNameUseCase updateBranchNameUseCase(BranchRepository branchRepository) {
        return new UpdateBranchNameUseCase(branchRepository);
    }

    @Bean
    public UpdateProductNameUseCase updateProductNameUseCase(ProductRepository productRepository) {
        return new UpdateProductNameUseCase(productRepository);
    }
}
