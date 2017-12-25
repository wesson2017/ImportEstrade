package com.frogsing.imports.service.hy;

import com.frogsing.dao.mb.CategorycontentDao;
import com.frogsing.dao.sp.CategoryDao;
import com.frogsing.heart.exception.E;
import com.frogsing.heart.jpa.BaseDao;
import com.frogsing.heart.jpa.BaseService;
import com.frogsing.heart.utils.B;
import com.frogsing.po.entity.mb.Categorycontent;
import com.frogsing.po.entity.sp.Category;
import com.frogsing.po.utils.Colums.mb_categorycontent;
import com.frogsing.po.utils.Consts.ContractType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
@Transactional
public class CategorycontentService extends BaseService<Categorycontent> {
	
	@Autowired
	private CategorycontentDao categorycontentDao;
	
	@Autowired
	private CategoryDao categoryDao;

	@Override
	protected BaseDao<Categorycontent, String> getBaseDao() {
		return categorycontentDao;
	}

	@Override
	protected void BaseSaveCheck(final Categorycontent obj) {
		if(B.Y(obj.getId())){//添加合同模板
			long i = this.categorycontentDao.count(new Specification<Categorycontent>() {
				public Predicate toPredicate(Root<Categorycontent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.and(cb.equal(root.get(mb_categorycontent.scategoryid), obj.getScategoryid()),
							cb.equal(root.get(mb_categorycontent.icontracttype), obj.getIcontracttype()));
				}
			});
			if(i == 0){
				Category cg=categoryDao.findOne(obj.getScategoryid());
				obj.setSystemcode(cg.getSsystemcode());
				obj.setScategory(cg.getSname());
				obj.setStitle(cg.getSname()+ ContractType.get(obj.getIcontracttype()).getLabel()+"模板");
			}else{
				//合同模板已经存在
				E.S("合同模板已经存在！");
			}
		}else{//编辑合同模板  合同模板ID不等于空
			long i = this.categorycontentDao.count(new Specification<Categorycontent>() {
				public Predicate toPredicate(Root<Categorycontent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.and(cb.equal(root.get(mb_categorycontent.scategoryid), obj.getScategoryid()),
							cb.equal(root.get(mb_categorycontent.icontracttype), obj.getIcontracttype()),
							cb.notEqual(root.get("id"),obj.getId()));
				}
			});
			if(i > 0){
				//合同模板已经存在
				E.S("合同模板已经存在！");
			}else{
				Category cg=categoryDao.findOne(obj.getScategoryid());
				obj.setSystemcode(cg.getSsystemcode());
				obj.setScategory(cg.getSname());
				obj.setStitle(cg.getSname()+ ContractType.get(obj.getIcontracttype()).getLabel()+"模板");
			}
		}
	}
	
	public List<Categorycontent> findByScategoryid(String scategoryid){
		return categorycontentDao.findByScategoryid(scategoryid);
	}
	public Categorycontent findByScategoryidAndIcontracttype(int icontracttype, String scategoryid){
		return categorycontentDao.findByScategoryidAndIcontracttype(scategoryid,icontracttype);
	}

	//根据品名ID查找合同模板
	public Categorycontent findByContentScategoryId(int contracttype, String scategoryid){
		Categorycontent obj=categorycontentDao.findByScategoryidAndIcontracttype(scategoryid,contracttype);
		if(obj == null){
			Category cg=categoryDao.findOne(scategoryid);
			if(B.Y(cg.getSparentid()))
				return null;
			else
				return this.findByContentScategoryId(contracttype,cg.getSparentid());
		}else
			return obj;
	}
	
}
