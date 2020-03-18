package com.zlsrj.wms.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "TenantPriceStep查询参数", description = "阶梯明细")
public class TenantPriceStepQueryParam implements Serializable {

	private static final long serialVersionUID = 2544711154145231311L;

	@ApiModelProperty(value = "阶梯明细ID")
	private String id;

	@ApiModelProperty(value = "租户ID")
	private String tenantId;

	@ApiModelProperty(value = "水价明细ID")
	private String priceDetailId;

	@ApiModelProperty(value = "阶梯级次")
	private Integer stepClass;

	@ApiModelProperty(value = "阶梯起始量")
	private BigDecimal startCode;

	@ApiModelProperty(value = "阶梯终止量")
	private BigDecimal endCode;

	@ApiModelProperty(value = "单价")
	private BigDecimal stepPrice;

	@ApiModelProperty(value = "标准用水人数")
	private Integer stepUsers;

	@ApiModelProperty(value = "超人数增补量")
	private BigDecimal stepUsersAdd;

	@ApiModelProperty(value = "数据新增时间")
	private Date addTime;

	@ApiModelProperty(value = "数据新增时间开始")
	private Date addTimeStart;

	@ApiModelProperty(value = "数据新增时间结束")
	private Date addTimeEnd;

	@ApiModelProperty(value = "数据修改时间")
	private Date updateTime;

	@ApiModelProperty(value = "数据修改时间开始")
	private Date updateTimeStart;

	@ApiModelProperty(value = "数据修改时间结束")
	private Date updateTimeEnd;

}

