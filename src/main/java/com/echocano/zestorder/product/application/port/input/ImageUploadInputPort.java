package com.echocano.zestorder.product.application.port.input;

import com.echocano.zestorder.product.domain.Image;
import com.echocano.zestorder.product.domain.UploadInstruction;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ImageUploadInputPort {

    Mono<List<Image>> executeBatch(List<UploadInstruction> requests);
}
