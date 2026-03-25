package com.echocano.zestorder.product.infrastructure.adapter.input.rest.mapper;

import com.echocano.zestorder.product.domain.Image;
import com.echocano.zestorder.product.domain.UploadInstruction;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ImageUploadRequest;
import com.echocano.zestorder.product.infrastructure.adapter.input.rest.dto.ImageUploadResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageRestMapper {

    UploadInstruction toDomain(ImageUploadRequest dto);
    List<UploadInstruction> toDomains(List<ImageUploadRequest> dtos);

    @Mapping(source = "fileName", target = "finalFileName")
    @Mapping(source = "url", target = "uploadUrl")
    ImageUploadResponse toDto(Image image);

    List<ImageUploadResponse> toDtos(List<Image> images);
}
