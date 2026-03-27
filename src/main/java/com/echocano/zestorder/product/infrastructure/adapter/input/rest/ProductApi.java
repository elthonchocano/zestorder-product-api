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
import org.springframework.hateoas.EntityModel;
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
            @Parameter(description = "Product ID") String id);

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
    Mono<EntityModel<ProductResponse>> update(String id, ProductRequest request);

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
    Mono<Void> delete(String id);
}
