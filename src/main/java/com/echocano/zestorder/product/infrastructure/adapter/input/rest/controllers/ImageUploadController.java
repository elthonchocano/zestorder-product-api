package com.echocano.zestorder.product.infrastructure.adapter.input.rest.controllers;

import com.echocano.zestorder.product.application.port.input.ImageUploadInputPort;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ImageUploadRequest;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ImageUploadResponse;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.mapper.ImageRestMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageUploadController {

    private final ImageUploadInputPort imageUploadUseCase;
    private final ImageRestMapper mapper;

    @PostMapping("/upload-urls")
    public Mono<List<ImageUploadResponse>> getUrls(
            @Valid @RequestBody List<ImageUploadRequest> dtos) {
        return imageUploadUseCase.executeBatch(mapper.toDomains(dtos))
                .map(mapper::toDtos);
    }
}
