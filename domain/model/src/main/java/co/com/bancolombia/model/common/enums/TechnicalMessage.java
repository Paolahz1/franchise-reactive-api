package co.com.bancolombia.model.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalMessage {

    // Franchise errors
    FRANCHISE_NOT_FOUND("FRA001", "La franquicia no fue encontrada"),
    FRANCHISE_NAME_EMPTY("FRA002", "El nombre de la franquicia no puede estar vacío"),
    FRANCHISE_NAME_DUPLICATE("FRA003", "Ya existe una franquicia con ese nombre"),
    FRANCHISE_CREATION_ERROR("FRA004", "Error al crear la franquicia"),

    // Branch errors
    BRANCH_NOT_FOUND("BRA001", "La sucursal no fue encontrada"),
    BRANCH_NAME_EMPTY("BRA002", "El nombre de la sucursal no puede estar vacío"),
    BRANCH_NAME_DUPLICATE("BRA003", "Ya existe una sucursal con ese nombre en la franquicia"),
    BRANCH_CREATION_ERROR("BRA004", "Error al agregar la sucursal a la franquicia"),

    // Product errors
    PRODUCT_NOT_FOUND("PRO001", "El producto no fue encontrado"),
    PRODUCT_NAME_EMPTY("PRO002", "El nombre del producto no puede estar vacío"),
    PRODUCT_NAME_DUPLICATE("PRO003", "Ya existe un producto con ese nombre en la sucursal"),
    PRODUCT_STOCK_INVALID("PRO004", "El stock del producto debe ser mayor o igual a cero"),
    PRODUCT_CREATION_ERROR("PRO005", "Error al agregar el producto a la sucursal"),
    PRODUCT_REMOVAL_ERROR("PRO006", "Error al eliminar el producto de la sucursal"),

    // General validation errors
    INVALID_ID("VAL001", "El identificador proporcionado no es válido"),
    REQUIRED_FIELD_MISSING("VAL002", "Faltan campos requeridos en la solicitud"),

    // Technical errors (mensajes genéricos para el cliente)
    INTERNAL_ERROR("TEC001", "Ha ocurrido un error interno, por favor intente más tarde"),
    DATABASE_ERROR("TEC002", "Error de conexión con la base de datos");

    private final String code;
    private final String message;
}
