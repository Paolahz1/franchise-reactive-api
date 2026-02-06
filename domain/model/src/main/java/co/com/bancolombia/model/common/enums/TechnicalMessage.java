package co.com.bancolombia.model.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalMessage {

   // Franchise errors
    FRANCHISE_NOT_FOUND("404", "Franchise not found", ""),
    FRANCHISE_NAME_EMPTY("400", "Franchise name cannot be empty", ""),
    FRANCHISE_NAME_ALREADY_EXISTS("409", "A franchise with this name already exists", ""),
    FRANCHISE_NAME_DUPLICATE("409", "A franchise with this name already exists", ""),
    FRANCHISE_CREATION_ERROR("500", "Error creating franchise", ""),

    // Branch errors
    BRANCH_NOT_FOUND("404", "Branch not found", ""),
    BRANCH_NAME_EMPTY("400", "Branch name cannot be empty", ""),
    BRANCH_NAME_ALREADY_EXISTS("409", "A branch with this name already exists in the franchise", ""),
    BRANCH_NAME_DUPLICATE("409", "A branch with this name already exists in the franchise", ""),
    BRANCH_CREATION_ERROR("500", "Error adding branch to franchise", ""),

    // Product errors
    PRODUCT_NOT_FOUND("404", "Product not found", ""),
    PRODUCT_NAME_EMPTY("400", "Product name cannot be empty", ""),
    PRODUCT_NAME_DUPLICATE("409", "A product with this name already exists in the branch", ""),
    PRODUCT_STOCK_INVALID("400", "Product stock must be greater than or equal to zero", ""),
    PRODUCT_CREATION_ERROR("500", "Error adding product to branch", ""),
    PRODUCT_REMOVAL_ERROR("500", "Error removing product from branch", ""),

    // General validation errors
    INVALID_ID("400", "The provided identifier is invalid", ""),
    REQUIRED_FIELD_MISSING("400", "Required fields are missing in the request", ""),

    // Technical errors
    INTERNAL_ERROR("500", "An internal error occurred, please try again later", ""),
    DATABASE_ERROR("500", "Database connection error", "");

    private final String code;
    private final String message;
    private final String param;
}
