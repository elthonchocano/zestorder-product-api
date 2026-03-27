package com.echocano.zestorder.product.infrastructure.adapter.input.rest;

import com.echocano.zestorder.product.infrastructure.adapter.input.rest.controllers.RestExceptionHandler;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ImageUploadRequest;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ImageUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Image Service", description = "Endpoints for handling image uploads and storage.")
public interface ImageApi {

    @Operation(
            summary = "Upload batch of images",
            description = "Processes a list of image metadata/files and returns the generated URLs."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully processed and uploaded images",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ImageUploadResponse.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request body or validation failed",
                    content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "External storage service error",
                    content = @Content(schema = @Schema(implementation = RestExceptionHandler.ErrorDetails.class))
            )
    })
    Mono<List<ImageUploadResponse>> getUrls(
            @RequestBody(description = "List of images to be processed")
            @Valid List<ImageUploadRequest> dtos);
}
