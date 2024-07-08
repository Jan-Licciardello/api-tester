package com.ntt.apitester.dto.of;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filter extends BaseDTO {

	private Double lat;
	private Double lng;
	private Double radius;
}
