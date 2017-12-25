/** 
 * Filename SysParamService.java
 * Create on 2013-11-29
 * Copyright 2011 Frogsing All Rights Reserved.
 */
package com.frogsing.imports.service.hy;

import com.frogsing.dao.sys.BusinessParameterDao;
import com.frogsing.dao.sys.ParameterDao;
import com.frogsing.heart.service.IParameterService;
import com.frogsing.heart.web.vo.BusinessParameterVo;
import com.frogsing.po.entity.sys.BusinessParameter;
import com.frogsing.po.utils.Consts.BizParaType;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Description: 
 * 
 * @author <a href="mailto:service@frogsing.com">Sandy</a>
 * @since version1.0
 */
@Component
@Transactional(readOnly=true)
public class TagParamService implements IParameterService {

	@Autowired
	private BusinessParameterDao businessParameterDao;
	@Autowired
	private ParameterDao parameterDao;
	


//	public String getValue(String key,int type){
//		if(0 == type){
//			return parameterDao.getParaValue(Integer.valueOf(key));
//		}else if(1 == type){
//			BusinessParameter parameter=this.businessParameterDao.findBySparametername(key);
//			if(parameter == null)
//				return "";
//			return parameter.getSparametervalue();
//		}else
//			return "";
//	}





	public List<BusinessParameterVo> getAllParameters(int type) {
		
		List<BusinessParameter> list=businessParameterDao.findByIparatypeOrderByIsortAsc(type);
		List<BusinessParameterVo> rs=Lists.newArrayListWithCapacity(list.size());

		for(BusinessParameter b:list){
			BusinessParameterVo vo=new BusinessParameterVo();
			vo.setIparatype(b.getIparatype());
			vo.setIsort(b.getIsort());
			vo.setSparametername(b.getSparametername());
			vo.setSparametervalue(b.getSparametervalue());
			rs.add(vo);
		}
		return  rs;
	
	}


	



	public List<BusinessParameterVo> getFtpParameters() {
		return this.getAllParameters(BizParaType.FTPInfo.val());
	}






	public String GetParaValueByParaType(String type) {
	
		return parameterDao.getParaValue(Integer.parseInt(type));

		
	}





	public String GetParaValueByParaType(int type) {
		
		return parameterDao.getParaValue(type);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public String genarateSeqNo(String name) {
		return parameterDao.getSeqNo(name);
	}






	public Date getServerTime() {
		return (Date)parameterDao.getFieldValue("select sysdate from dual");
	}
	
}
