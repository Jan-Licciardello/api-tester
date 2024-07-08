package com.ntt.apitester.dto.of;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoCodeNetworkItemsRequestV2DTO extends BaseDTO {
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = -201173078421369913L;


	private Double zoom;


	private List<String> includeTypes;
	private String areeGrigie;
	private String cluster;
	@Nullable
	private Integer offset;
	@Nullable
	private Integer page;

	//New params for geoCode v2

	private Double minX;

	private Double minY;

	private Double maxX;

	private Double maxY;
	//


}
