package com.backend.Gdg.global.converter;
import com.backend.Gdg.global.domain.entity.Paragraph;
import com.backend.Gdg.global.web.dto.Paragraph.ParagraphResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParagraphConverter {
    public ParagraphResponseDTO.ParagraphDetail toDetail(Paragraph p) {
        return ParagraphResponseDTO.ParagraphDetail.builder()
                .paragraph_id(p.getParagraphId())
                .content(p.getContent())
                .imageUrl(p.getImageUrl())
                .color(p.getUserColor())
                .build();
    }

    public List<ParagraphResponseDTO.ParagraphDetail> toDetailList(List<Paragraph> paragraphs) {
        return paragraphs.stream()
                .map(this::toDetail)
                .collect(Collectors.toList());
    }
}
