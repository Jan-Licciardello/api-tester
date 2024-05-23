package com.ntt.apitester.dto.of;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EnrichmentRequestBody {
    private String name;
    private String elementType;
    private Integer level;
}
