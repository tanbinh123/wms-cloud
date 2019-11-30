package com.zlsrj.wms.module.mq.consumer;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.zlsrj.wms.api.entity.TenantInfo;
import com.zlsrj.wms.module.service.ITenantModuleAndMenuService;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TenantModuleMqCosume {
	
	@Autowired
	private ITenantModuleAndMenuService tenantModuleAndMenuService;
	
	@JmsListener(destination = "${mq.mbg.tenant.module}")
	public void receive(TextMessage textMessage) throws JMSException {
		if (textMessage != null) {
			String text = textMessage.getText();
			log.info(text);
			if (JSONUtil.isJson(text)) {
				TenantInfo tenantInfo = JSONUtil.toBean(text, TenantInfo.class);

				log.info("TenantModuleMqCosume.receive" + "\t" + tenantInfo.toString());
				tenantModuleAndMenuService.initByTenant(tenantInfo);
			}
		}

	}
}
