package com.ntt.apitester.dto.of;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatedNetworkRequestBody extends BaseDTO {


	private String name;
	private String elementType;
	private Double zoom;

	private List<String> includeTypes;
	private Filter filter;
	private String areeGrigie;
}
