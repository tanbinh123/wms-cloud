package com.zlsrj.wms.employee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlsrj.wms.api.entity.TenantEmployee;
import com.zlsrj.wms.api.entity.TenantInfo;

public interface ITenantEmployeeService extends IService<TenantEmployee> {
	/**
	 * 根据新建租户信息创建默认用户类型
	 * @param tenantInfo
	 * @return
	 */
	boolean saveBatchByTenantInfo(TenantInfo tenantInfo);
}
