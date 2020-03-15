package com.zlsrj.wms.api.vo;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "TenantDepartment对象", description = "租户部门")
public class TenantDepartmentVo implements Serializable {

	private static final long serialVersionUID = 1151213112571588529L;

	@ApiModelProperty(value = "部门ID")
	@JSONField(name="id")
	private String id;

	@ApiModelProperty(value = "租户ID")
	@JSONField(name="tenant_id")
	private String tenantId;

	@ApiModelProperty(value = "租户ID")
	@JSONField(name="tenant_name")
	private String tenantName;

	@ApiModelProperty(value = "部门名称")
	@JSONField(name="department_name")
	private String departmentName;

	@ApiModelProperty(value = "上级部门ID")
	@JSONField(name="department_parent_id")
	private String departmentParentId;

	@ApiModelProperty(value = "子级租户部门列表")
	@JSONField(name="children")
	private List<TenantDepartmentVo> children;
	
	@ApiModelProperty(value = "是否包含子级租户部门")
	@JSONField(name="has_children")
	private boolean hasChildren;
	
}
