package com.zlsrj.wms.saas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlsrj.wms.api.dto.TenantCustomerTypeAddParam;
import com.zlsrj.wms.api.dto.TenantCustomerTypeUpdateParam;
import com.zlsrj.wms.api.entity.TenantCustomerType;

public interface ITenantCustomerTypeService extends IService<TenantCustomerType> {
	String save(TenantCustomerTypeAddParam tenantCustomerTypeAddParam);

	boolean updateById(TenantCustomerTypeUpdateParam tenantCustomerTypeUpdateParam);

}