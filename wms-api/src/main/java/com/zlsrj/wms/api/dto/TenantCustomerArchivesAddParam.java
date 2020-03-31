package com.zlsrj.wms.api.dto;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "TenantCustomerArchives新增参数", description = "用户档案")
public class TenantCustomerArchivesAddParam implements Serializable {

	private static final long serialVersionUID = 1014111453311156136L;

	@ApiModelProperty(value = "档案ID")
	private String id;

	@ApiModelProperty(value = "租户ID")
	private String tenantId;

	@ApiModelProperty(value = "用户ID")
	private String customerId;

	@ApiModelProperty(value = "用户号")
	private String customerCode;

	@ApiModelProperty(value = "档案名称")
	private String archivesName;

	@ApiModelProperty(value = "创建时间")
	private Date archivesCreateTime;

	@ApiModelProperty(value = "创建日期")
	private Date archivesCreateDate;

	@ApiModelProperty(value = "存储文件名称JSON串")
	private String archivesFilename;

	@ApiModelProperty(value = "证件信息JSON串，例如身份证号、地址等")
	private String archivesInformation;

	@ApiModelProperty(value = "证件号码，例如身份证号等")
	private String archivesCode;

	@ApiModelProperty(value = "数据新增时间")
	private Date addTime;

	@ApiModelProperty(value = "数据修改时间")
	private Date updateTime;

}

