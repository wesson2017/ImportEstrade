package com.frogsing.imports.service.hy;


import com.frogsing.dao.ht.BillFileDao;
import com.frogsing.dao.ht.ContractDao;
import com.frogsing.heart.exception.E;
import com.frogsing.heart.jpa.BaseDao;
import com.frogsing.heart.jpa.BaseService;
import com.frogsing.heart.spring.Props;
import com.frogsing.heart.utils.B;
import com.frogsing.heart.utils.FtpUtils;
import com.frogsing.heart.utils.T;
import com.frogsing.po.entity.ht.BillFile;
import com.frogsing.po.entity.ht.Contract;
import com.frogsing.po.entity.ky.LongContract;
import com.frogsing.po.entity.ky.SingleBuy;
import com.frogsing.po.entity.zj.BillPay;
import com.frogsing.po.utils.Colums.ht_billfile;
import com.frogsing.po.utils.Consts;
import com.frogsing.po.utils.Consts.BillFileType;
import com.frogsing.po.utils.Consts.ContractType;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;
import java.util.List;

@Component
@Transactional
public class BillFileService extends BaseService<BillFile> {

	@Autowired
	private BillFileDao billFileDao;
	@Autowired
	private ContractDao contractDao;

	@Override
	protected BaseDao<BillFile, String> getBaseDao(){
		return billFileDao;
	}
	@Override
	protected void BaseSaveCheck(BillFile obj){


	}


	private BillFile buildEntity(String billid, String billno, String uploadid, String sfile, String sremark){
		BillFile file=new BillFile();
		file.setId(null);
		file.setSbillid(billid);
		file.setSuploadid(uploadid);
		file.setDaddtime(T.now());
		file.setSremark(sremark);
		file.setSfiletype(sfile);

		return file;
	}

	/**
	 * 通用添加文件
	 * @param file
	 * @param billType
	 * @param billid
	 * @param billno
	 * @param uploadid
	 * @param remark
	 * @return
	 */
	public BillFile addFile(CommonsMultipartFile file, int billType, String billid, String billno, String uploadid, String sfile, String remark, List<String> releaseId){
		String dir="BillFile/"+billno+"/"+billType+"/";
		String  filename=file.getOriginalFilename();
		if ((filename!=null)&&(filename.length()>0))
        {
                //查找字符‘.‘出现的最后一个索引位置
                int x = filename.lastIndexOf('.');
                //判断此索引是否存在，并且不是最后一个字符
                if ((x>-1)&&(x<filename.length()-1))
                {
                        String suffix=filename.substring(x+1);
                        if(Props.get("file.suffix","").indexOf(suffix) != -1){

                        	E.S("不支持此文件上传");
                        }
                }
        }
		String filename2= FtpUtils.uploadToFtpServer(file,dir);
    	if(B.Y(filename2))
			return null;
		BillFile obj=this.buildEntity(billid,billno, uploadid, sfile,remark);
		obj.setSname(file.getOriginalFilename());
		obj.setSurl(filename2);
		obj.setIbilltype(billType);
		if(releaseId == null)
			releaseId=Lists.newArrayList();
		if(!releaseId.contains(billType+"-"+billid))
			releaseId.add(billType+"-"+billid);
		obj.setSreleaseid(releaseId.toString());

		this.billFileDao.save(obj);

		return obj;

	}

	public BillFile addFile(String url, String filename, int billType, String billid, String billno, String uploadid, String sfile, String remark, List<String> releaseId, Date date){
		BillFile obj=this.buildEntity(billid,billno, uploadid, sfile,remark);
		obj.setDaddtime(date);
		obj.setSname(filename);
		obj.setSurl(url);
		obj.setIbilltype(billType);
		if(releaseId == null)
			releaseId=Lists.newArrayList();
		if(!releaseId.contains(billType+"-"+billid))
			releaseId.add(billType+"-"+billid);
		obj.setSreleaseid(releaseId.toString());

		this.billFileDao.save(obj);
		return obj;
	}

	public  List<String> buildReleaseId(Object c){
		List<String> l=Lists.newArrayList();
		if(c == null)
			return l;
		if(c instanceof LongContract){
			l.add(BillFileType.Long.val()+"-"+((LongContract) c).getId());
		}else if(c instanceof SingleBuy){
			SingleBuy obj=(SingleBuy)c;
			l.add(BillFileType.Single.val()+"-"+obj.getId());
			if(B.N(obj.getSlongid()))
				l.add(0, BillFileType.Long.val()+"-"+obj.getSlongid());
		}else if(c instanceof Contract){
			Contract obj=(Contract)c;
			l.add(BillFileType.Contract.val()+"-"+obj.getId());
			if(ContractType.COMMON.isNot(obj.getIcontracttype()) && B.N(obj.getSorderid())){
				l.add(0, BillFileType.Single.val()+"-"+obj.getSorderid());
				if(B.N(obj.getSinglebuy().getSlongid()))
					l.add(0, BillFileType.Long.val()+"-"+obj.getSinglebuy().getSlongid());
			}
		}else if(c instanceof BillPay){
			BillPay obj=(BillPay)c;
			Consts.BillPaySrcType type= Consts.BillPaySrcType.get(obj.getIpaytype());
            switch (type) {
                case Contract:
                    l.add(BillFileType.Contract.val()+"-"+obj.getSbillid());
                    break;
                case Other:
                    break;
                case TimeSettle:
                    break;
                case LongContract:
                    l.add(BillFileType.Long.val()+"-"+obj.getSbillid());
                    break;
                default:
                    break;

            }
			l.add(BillFileType.BillPay.val()+"-"+obj.getId());

		}

		return l;
	}


	public List<BillFile> findByBillId(String id, Sort s){
		return this.billFileDao.findBySbillid(id,s);
	}
	public List<BillFile> findByBillId(String... id){
		if(id == null || id.length <= 0)
			return Lists.newArrayList();
		return this.billFileDao.findBySbillidIn(Lists.newArrayList(id),new Sort(Direction.DESC, ht_billfile.ibilltype, ht_billfile.daddtime));
	}

	public List<BillFile> findByBillIdAndFiletype(String id, int billType){
		return this.billFileDao.findBySbillidAndIbilltype(id, billType);
	}
	public List<BillFile> findBySreleaseId(String sreleaseid){
		return this.billFileDao.findBySreleaseidLikeOrderByDaddtimeAsc("%"+sreleaseid+"%");
	}

	public void deleteByBillId(String id){
		List<BillFile> files=this.findByBillId(id);
		if(B.N(files))
			this.billFileDao.delete(files);
	}

	public void deleteone(String id){
		if(B.Y(id))
			E.S("文件不存在");
		BillFile bf=billFileDao.findOne(id);
		if(bf==null)
			E.S("文件不存在");
		billFileDao.delete(bf);
	}
}
