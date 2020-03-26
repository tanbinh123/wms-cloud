package com.zlsrj.wms.saas.service.impl;

import static java.util.stream.Collectors.toCollection;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zlsrj.wms.api.dto.TenantInfoAddParam;
import com.zlsrj.wms.api.dto.TenantInfoModuleInfoUpdateParam;
import com.zlsrj.wms.api.dto.TenantInfoRechargeParam;
import com.zlsrj.wms.api.dto.TenantInfoUpdateParam;
import com.zlsrj.wms.api.entity.ModuleMenu;
import com.zlsrj.wms.api.entity.TenantConsumptionBill;
import com.zlsrj.wms.api.entity.TenantInfo;
import com.zlsrj.wms.api.entity.TenantModule;
import com.zlsrj.wms.api.entity.TenantRoleMenu;
import com.zlsrj.wms.common.annotation.DictionaryDescription;
import com.zlsrj.wms.common.annotation.DictionaryOrder;
import com.zlsrj.wms.common.annotation.DictionaryText;
import com.zlsrj.wms.common.annotation.DictionaryValue;
import com.zlsrj.wms.common.util.TranslateUtil;
import com.zlsrj.wms.saas.mapper.ModuleMenuMapper;
import com.zlsrj.wms.saas.mapper.TenantConsumptionBillMapper;
import com.zlsrj.wms.saas.mapper.TenantInfoMapper;
import com.zlsrj.wms.saas.mapper.TenantModuleMapper;
import com.zlsrj.wms.saas.mapper.TenantRoleMenuMapper;
import com.zlsrj.wms.saas.mq.MqConfig;
import com.zlsrj.wms.saas.service.IIdService;
import com.zlsrj.wms.saas.service.ITenantInfoService;
import com.zlsrj.wms.saas.service.RedisService;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TenantInfoServiceImpl extends ServiceImpl<TenantInfoMapper, TenantInfo> implements ITenantInfoService {
	@Resource
	private IIdService idService;
	
	@Autowired
	private RedisService<String, String> redisService;

	@Autowired
	private DefaultMQProducer defaultMQProducer;
	
	@Autowired
	private MqConfig mqConfig;
	
	@Resource
	private TenantConsumptionBillMapper tenantConsumptionBillMapper;
	
	@Resource
	private TenantModuleMapper tenantModuleMapper;
	
	@Resource
	private ModuleMenuMapper moduleMenuMapper;
	
	@Resource
	private TenantRoleMenuMapper tenantRoleMenuMapper;

	@Override
	public boolean save(TenantInfo tenantInfo) {
		boolean success = false;
		
		success = super.save(tenantInfo);

		if (success) {
			try {
				// 顺序消息
				
				for(String tag:mqConfig.getInsertTags()) {
					String key = tenantInfo.getId();
					byte[] body = JSON.toJSONString(tenantInfo).getBytes(RemotingHelper.DEFAULT_CHARSET);
					Message message = new Message(mqConfig.getTopic(), tag, key, body);
					
					SendResult sendResult = defaultMQProducer.send(message, new MessageQueueSelector() {
			            @Override
			            public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
			            	//uuid生成的id，取第1个字符，转16进制字符串表示，然后转成10进制数字
			            	Integer id = Integer.valueOf(String.valueOf(((String)arg).toCharArray()[0]), 16);
			                //Integer id = (Integer) arg;
			                int index = id % mqs.size();
			                return mqs.get(index);
			            }
			            }, tenantInfo.getId());

			            log.info(String.format("%s%n", sendResult));
					
				}
				
				
//				Message message = new Message(paramConfigService.wmsSaasTopic, paramConfigService.tenantInfoTag,
//						JSON.toJSONString(tenantInfo).getBytes(RemotingHelper.DEFAULT_CHARSET));
//				// 同步消息
//				// SendResult sendResult = defaultMQProducer.send(message);
//				// log.info("sendResult={}", sendResult);
//				// 异步消息
//				defaultMQProducer.send(message, new SendCallback() {
//					@Override
//					public void onSuccess(SendResult sendResult) {
//						log.info(String.format(" OK %s %n", sendResult.getMsgId()));
//					}
//
//					@Override
//					public void onException(Throwable e) {
//						log.info(String.format(" Exception %s %n", e));
//						e.printStackTrace();
//					}
//				});
				
			} catch (Exception e) {
				log.info(JSON.toJSONString(tenantInfo));
				log.error("发送rocketmq消息出错", e);
			}
		}

		return success;
	}

	@Override
	public TenantInfo getDictionaryById(Serializable id) {
		try {
			String entityJSONString = redisService.getValue(id.toString());
			if (StringUtils.isNotBlank(entityJSONString)) {
				TenantInfo entity = JSONObject.parseObject(entityJSONString, TenantInfo.class);
				return entity;
			}
		} catch (Exception e) {
			log.error("redis error", e);
		}

		List<String> fieldList = Stream.of(TenantInfo.class.getDeclaredFields())
				/* 过滤静态属性 */
				.filter(field -> !Modifier.isStatic(field.getModifiers()))
				/* 过滤 transient关键字修饰的属性 */
				.filter(field -> !Modifier.isTransient(field.getModifiers()))
				.filter(field -> field.isAnnotationPresent(DictionaryValue.class)//
						|| field.isAnnotationPresent(DictionaryText.class)//
						|| field.isAnnotationPresent(DictionaryOrder.class)//
						|| field.isAnnotationPresent(DictionaryDescription.class)//
				).map(e -> e.getName()).collect(toCollection(LinkedList::new));

		QueryWrapper<TenantInfo> queryWrapper = new QueryWrapper<>();
		queryWrapper//
				.lambda()//
				.select(TenantInfo.class, //
						e -> fieldList.contains(e.getProperty()))//
				.eq(TenantInfo::getId, id);
		TenantInfo entity = this.getOne(queryWrapper);
		// TenantInfo entity = this.getById(id);

		redisService.setValue(entity.getId(), JSON.toJSONString(entity));

		return entity;
	}
	
	@Override
	public String save(TenantInfoAddParam tenantInfoAddParam) {
		String jsonString = JSON.toJSONString(tenantInfoAddParam);
		TenantInfo tenantInfo = JSON.parseObject(jsonString, TenantInfo.class);
		if (tenantInfo.getId() == null || tenantInfo.getId().trim().length() == 0) {
			tenantInfo.setId(idService.selectId());
		}
		// 账户余额
		if (tenantInfo.getTenantBalance() == null) {
			tenantInfo.setTenantBalance(BigDecimal.ZERO);
		}
		// 注册时间
		if (tenantInfo.getTenantRegisterTime() == null) {
			tenantInfo.setTenantRegisterTime(new DateTime());
		}

		this.save(tenantInfo);

		return tenantInfo.getId();
	}
	
	@Override
	public boolean updateById(TenantInfoUpdateParam tenantInfoUpdateParam) {
		TenantInfo tenantInfo = TranslateUtil.translate(tenantInfoUpdateParam, TenantInfo.class);
		return this.updateById(tenantInfo);
	}

	@Override
	public boolean updateById(TenantInfo entity) {
		boolean success = super.updateById(entity);
		if (success) {
			try {
				redisService.remove(entity.getId());
			} catch (Exception e) {
				log.error("redis error", e);
			}
		}

		return success;
	}

	@Override
	public boolean update(Wrapper<TenantInfo> updateWrapper) {
		boolean success = super.update(updateWrapper);
		if (success) {
			try {
				TenantInfo entity = updateWrapper.getEntity();
				redisService.remove(entity.getId());
			} catch (Exception e) {
				log.error("redis error", e);
			}
		}
		return success;
	}

	@Override
	public boolean removeById(Serializable id) {
		boolean success = super.removeById(id);
		if (success) {
			try {
				redisService.remove(id.toString());
			} catch (Exception e) {
				log.error("redis error", e);
			}
		}
		
		if (success) {
			try {
				// 顺序消息
				TenantInfo tenantInfo = TenantInfo.builder().id(id.toString()).build();
				
				for(String tag:mqConfig.getRemoveTags()) {
					String key = tenantInfo.getId();
					byte[] body = JSON.toJSONString(tenantInfo).getBytes(RemotingHelper.DEFAULT_CHARSET);
					Message message = new Message(mqConfig.getTopic(), tag, key, body);
					
					SendResult sendResult = defaultMQProducer.send(message, new MessageQueueSelector() {
			            @Override
			            public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
			            	//uuid生成的id，取第1个字符，转16进制字符串表示，然后转成10进制数字
			            	Integer id = Integer.valueOf(String.valueOf(((String)arg).toCharArray()[0]), 16);
			                //Integer id = (Integer) arg;
			                int index = id % mqs.size();
			                return mqs.get(index);
			            }
			            }, tenantInfo.getId());

			            log.info(String.format("%s%n", sendResult));
					
				}
				
			} catch (Exception e) {
				log.info(id.toString());
				log.error("发送rocketmq消息出错", e);
			}
		}
		
		return success;
	}
	
	@Override
	@Transactional
	public boolean recharge(TenantInfoRechargeParam tenantInfoRechargeParam) {
		boolean success = false;
		TenantInfo tenantInfo = this.getById(tenantInfoRechargeParam.getId());
		BigDecimal tenantBalanceBefore = tenantInfo.getTenantBalance();
		
		TenantConsumptionBill tenantConsumptionBill = TenantConsumptionBill.builder()//
				.id(idService.selectId())// 租户账单ID
				.tenantId(tenantInfoRechargeParam.getId())// 租户ID
				.consumptionBillType(1)// 账单类型（1：充值；2：消费）
				.consumptionBillTime(new Date())// 账单时间
				.consumptionBillName("账户充值")// 账单名称[账户充值/短信平台/...]
				.consumptionBillMoney(tenantInfoRechargeParam.getRechargeMoney())// 账单金额
				.tenantBalance(tenantBalanceBefore.add(tenantInfoRechargeParam.getRechargeMoney()))// 租户账户余额
				.consumptionBillRemark(null)// 备注
				.build();
				
		tenantConsumptionBillMapper.insert(tenantConsumptionBill);
		
		TenantInfo tenantInfoWhere = TenantInfo.builder()//
				.id(tenantInfoRechargeParam.getId())//
				.build();
		UpdateWrapper<TenantInfo> updateWrapperTenantInfo = new UpdateWrapper<TenantInfo>();
		updateWrapperTenantInfo.setEntity(tenantInfoWhere);
		updateWrapperTenantInfo.lambda()//
				.set(TenantInfo::getTenantBalance, tenantBalanceBefore.add(tenantInfoRechargeParam.getRechargeMoney()))//
		;
		
		success = this.update(updateWrapperTenantInfo);
		
		return success;
	}
	
	@Override
	@Transactional
	public boolean updateModule(TenantInfoModuleInfoUpdateParam tenantInfoModuleInfoUpdateParam) {
		boolean success = false;
		
		if(1 == tenantInfoModuleInfoUpdateParam.getModuleOnOff()) {
			//
			TenantModule tenantModule = TenantModule.builder()//
					.id(idService.selectId())// 租户模块ID
					.tenantId(tenantInfoModuleInfoUpdateParam.getTenantId())// 租户ID
					.moduleId(tenantInfoModuleInfoUpdateParam.getModuleId())// 模块ID
					.moduleEdition(tenantInfoModuleInfoUpdateParam.getModuleEdition())// 开通版本（1：基础版；2：高级版；3：旗舰版）
					.moduleOpenTime(new Date())// 开通时间
					.build();
					
			tenantModuleMapper.insert(tenantModule);
			success = true;
		} else if(0 == tenantInfoModuleInfoUpdateParam.getModuleOnOff()) {
			QueryWrapper<TenantModule> queryWrapperTenantModule = new QueryWrapper<TenantModule>();
			queryWrapperTenantModule.lambda()//
					.eq(TenantModule::getTenantId, tenantInfoModuleInfoUpdateParam.getTenantId())//
					.eq(TenantModule::getModuleId, tenantInfoModuleInfoUpdateParam.getModuleId())//
			;
			
			tenantModuleMapper.delete(queryWrapperTenantModule);
			
			QueryWrapper<ModuleMenu> queryWrapperModuleMenu = new QueryWrapper<ModuleMenu>();
			queryWrapperModuleMenu.lambda()//
					.select(ModuleMenu.class,i -> false)//
					.eq(ModuleMenu::getModuleId, tenantInfoModuleInfoUpdateParam.getModuleId())//
			;
			List<ModuleMenu> moduleMenuList = moduleMenuMapper.selectList(queryWrapperModuleMenu);
			List<String> menuIdList = moduleMenuList.stream().map(ModuleMenu::getId).collect(Collectors.toList());
			
			QueryWrapper<TenantRoleMenu> queryWrapperTenantRoleMenu = new QueryWrapper<TenantRoleMenu>();
			queryWrapperTenantRoleMenu.lambda()//
					.eq(TenantRoleMenu::getTenantId, tenantInfoModuleInfoUpdateParam.getTenantId())//
					.in(TenantRoleMenu::getMenuId, menuIdList)//
			;
			
			tenantRoleMenuMapper.delete(queryWrapperTenantRoleMenu);
			success = true;
		} else {
			log.error("删除出错，未知moduleOnOff={}",tenantInfoModuleInfoUpdateParam.getModuleOnOff());
		}
		
		
		return success;
	}
}
