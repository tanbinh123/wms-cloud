package ${domainName}.${projectNameApi}.dto;

import java.io.Serializable;
<#if table.includeBigDecimal>
import java.math.BigDecimal;
</#if>
<#if table.includeDate>
import java.util.Date;
</#if>

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "${table.entityName}查询参数", description = "${table.tableComment}")
public class ${table.entityName}QueryParam implements Serializable {

	private static final long serialVersionUID = ${serialVersionUIDQueryParam}L;

	<#list table.columnList as column>
	@ApiModelProperty(value = "${column.columnComment}")
	@JSONField(name="${column.columnName}")
	private ${column.propertyType} ${column.propertyName};

	<#if column.dataType=="date" || column.dataType=="datetime" || column.dataType=="timestamp" || column.dataType=="time">
	@ApiModelProperty(value = "${column.columnComment}开始")
	@JSONField(name="${column.columnName}_start")
	private ${column.propertyType} ${column.propertyName}Start;

	@ApiModelProperty(value = "${column.columnComment}结束")
	@JSONField(name="${column.columnName}_end")
	private ${column.propertyType} ${column.propertyName}End;

	</#if>
	<#if column.likeable>
	@ApiModelProperty(value = "${column.columnComment}")
	@JSONField(name="${column.columnName}_like")
	private ${column.propertyType} ${column.propertyName}Like;

	</#if>
	</#list>
	<#if table.includeParentId>
	@ApiModelProperty(value = "父级ID")
	@JSONField(name="parent_id")
	private String parentId;
	
	</#if>
}
