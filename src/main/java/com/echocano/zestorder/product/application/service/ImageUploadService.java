package com.echocano.zestorder.product.application.service;

import com.echocano.zestorder.product.application.port.input.ImageUploadInputPort;
import com.echocano.zestorder.product.application.port.output.ImageStorageOutputPort;
import com.echocano.zestorder.product.domain.Image;
import com.echocano.zestorder.product.domain.UploadInstruction;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.config.ImageStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class ImageUploadService implements ImageUploadInputPort {

    private final ImageStorageOutputPort storageOutputPort;
    private final ImageStorageProperties properties;

    @Override
    public Mono<List<Image>> executeBatch(List<UploadInstruction> instructions) {
        if (instructions.size() < properties.getMinCount() || instructions.size() > properties.getMaxCount()) {
            return Mono.error(new IllegalArgumentException("Invalid number of files."));
        }
        return Flux.fromIterable(instructions)
                .flatMap(this::processInstruction)
                .collectList();
    }

    private Mono<Image> processInstruction(UploadInstruction i) {
        if (!properties.getAllowedTypes().contains(i.contentType().toLowerCase())) {
            return Mono.error(new IllegalArgumentException("Unsupported type: " + i.contentType()));
        }
        if (i.sizeInBytes() > properties.getMaxSizeBytes()) {
            return Mono.error(new IllegalArgumentException("File too large: " + i.fileName()));
        }
        String uniqueName = UUID.randomUUID() + "-" + i.fileName();
        return Mono.fromCallable(() -> storageOutputPort.generateUploadUrl(uniqueName, i.contentType(), i.sizeInBytes()))
                .map(url -> new Image(uniqueName, url, i.contentType()));
    }
}
