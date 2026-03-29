package com.echocano.zestorder.product.infrastructure.adapter.input.rest;

import com.echocano.zestorder.product.infrastructure.adapter.input.rest.controllers.RestExceptionHandler;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductRequest;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import reactor.core.publisher.Mono;

@Tag(name = "Product Service", description = "Endpoints for handling product's operation.")
public interface ProductApi {

    @Operation(summary = "Save a product", description = "Store a product into the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201"
                    , description = "Product created successfully"
                    , content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400"
                    , description = "Validation Failed. | Product was deleted. | Product already exists. | Missing body."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "404"
                    , description = "Product not found."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "500"
                    , description = "Something wrong happened."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class)))
    })
    Mono<EntityModel<ProductResponse>> create(ProductRequest request);

    @Operation(summary = "Get a product", description = "Fetch a product from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "Successful operation"
                    , content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400"
                    , description = "Validation Failed. | Product was deleted. | Product already exists. | Missing body."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "404"
                    , description = "Product not found."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "500"
                    , description = "Something wrong happened."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class)))
    })
    Mono<EntityModel<ProductResponse>> getOne(
            @Parameter(description = "The unique ID of the product to retrieve", required = true, example = "60d5ecb8b392d40015f3a2a1")
            String id);

    @Operation(summary = "Update a product", description = "Update a product from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "Successful operation"
                    , content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400"
                    , description = "Validation Failed. | Product was deleted. | Product already exists. | Missing body."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "404"
                    , description = "Product not found."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "500"
                    , description = "Something wrong happened."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class)))
    })
    Mono<EntityModel<ProductResponse>> update(
            @Parameter(description = "The unique ID of the product to update", required = true, example = "60d5ecb8b392d40015f3a2a1")
            String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated product details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ProductRequest.class))
            )
            @Valid
            ProductRequest request);

    @Operation(summary = "Delete a product", description = "Delete a product from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "Successful operation"),
            @ApiResponse(responseCode = "400"
                    , description = "Validation Failed."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "404"
                    , description = "Product not found."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))),
            @ApiResponse(responseCode = "500"
                    , description = "Something wrong happened."
                    , content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class)))
    })
    Mono<Void> delete(
            @Parameter(description = "The unique ID of the product to delete", required = true, example = "60d5ecb8b392d40015f3a2a1")
            String id
    );

    @Operation(summary = "Get all products paged", description = "Fetch a paginated, filtered, and sorted list of products.")
    Mono<PagedModel<EntityModel<ProductResponse>>> getAllPaged(
            @Parameter(description = "Search term to filter products by name", example = "pizza") String search,
            @Parameter(description = "Page number (0-based index)", example = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") int size,
            @Parameter(description = "Field to sort by", example = "createdAt") String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "DESC") String direction);

    @Operation(summary = "Get all products by category paged", description = "Fetch a paginated, filtered, and sorted list of products by category.")
    Mono<PagedModel<EntityModel<ProductResponse>>> getAllByCategoryPaged(
            @Parameter(description = "Category to filter", example = "pizzas") String category,
            @Parameter(description = "Page number (0-based index)", example = "0") int page,
            @Parameter(description = "Number of records per page", example = "10") int size,
            @Parameter(description = "Field to sort by", example = "createdAt") String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "DESC") String direction);
}
