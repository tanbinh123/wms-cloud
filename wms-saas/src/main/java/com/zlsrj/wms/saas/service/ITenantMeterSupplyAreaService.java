package com.zlsrj.wms.saas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlsrj.wms.api.dto.TenantMeterSupplyAreaAddParam;
import com.zlsrj.wms.api.dto.TenantMeterSupplyAreaUpdateParam;
import com.zlsrj.wms.api.entity.TenantMeterSupplyArea;

public interface ITenantMeterSupplyAreaService extends IService<TenantMeterSupplyArea> {
	String save(TenantMeterSupplyAreaAddParam tenantCustomerTypeAddParam);

	boolean updateById(TenantMeterSupplyAreaUpdateParam tenantCustomerTypeUpdateParam);

}