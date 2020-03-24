package com.zlsrj.wms.saas.strategy.tenant.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zlsrj.wms.api.entity.TenantInfo;
import com.zlsrj.wms.saas.service.ITenantEmployeeRoleService;
import com.zlsrj.wms.saas.strategy.tenant.TenantInsertStrategy;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TenantEmployeeRoleStrategy implements TenantInsertStrategy {
	@Autowired
	private ITenantEmployeeRoleService tenantEmployeeRoleService;
	
	@Override
	public boolean initData(TenantInfo tenantInfo) {
		boolean success = false;
		success = tenantEmployeeRoleService.saveBatchByTenantInfo(tenantInfo);
		return success;
	}

}