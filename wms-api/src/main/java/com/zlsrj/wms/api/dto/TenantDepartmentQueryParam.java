package com.zlsrj.wms.api.dto;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "TenantDepartment查询参数", description = "租户部门")
public class TenantDepartmentQueryParam implements Serializable {

	private static final long serialVersionUID = 1611121823631115110L;

	@ApiModelProperty(value = "部门ID")
	@JSONField(name="id")
	private String id;

	@ApiModelProperty(value = "租户ID")
	@JSONField(name="tenant_id")
	private String tenantId;

	@ApiModelProperty(value = "部门名称")
	@JSONField(name="department_name")
	private String departmentName;

	@ApiModelProperty(value = "上级部门ID")
	@JSONField(name="department_parent_id")
	private String departmentParentId;

	@ApiModelProperty(value = "父级ID")
	@JSONField(name="parent_id")
	private String parentId;
	
}

