package com.frogsing.imports.service.hy;

import com.frogsing.dao.hy.MemberDao;
import com.frogsing.dao.hy.MemberInvoiceDao;
import com.frogsing.dao.hy.UserDao;
import com.frogsing.dao.tz.MemberaccountDao;
import com.frogsing.heart.exception.E;
import com.frogsing.heart.security.shiro.ShiroUtils;
import com.frogsing.heart.security.utils.MD5;
import com.frogsing.heart.utils.B;
import com.frogsing.heart.utils.DateUtils;
import com.frogsing.heart.utils.StringHelper;
import com.frogsing.heart.utils.T;
import com.frogsing.heart.web.utils.ParaUtils;
import com.frogsing.po.entity.hy.Member;
import com.frogsing.po.entity.hy.MemberInvoice;
import com.frogsing.po.entity.hy.User;
import com.frogsing.po.entity.tz.Memberaccount;
import com.frogsing.po.utils.Colums;
import com.frogsing.po.utils.Consts;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Transactional
public class MemberExcelReader {


	@Autowired
	private MemberDao memberDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MemberaccountDao memberaccountDao;
	@Autowired
	private MemberInvoiceDao memberInvoiceDao;



	public void excelReader(InputStream is,String filename) throws Exception{
		Sheet sheet;
		Workbook book = null;
		try {
			book = new XSSFWorkbook(is);
		} catch (Exception ex) {
			book = new HSSFWorkbook(is);
		}
		if(book == null)
			E.S("导入的Excel文件不合法");
		sheet = book.getSheetAt(0);
		Map<String,Integer> colmap = readGpExcelTitle(sheet.getRow(1));

		readExcelContent(sheet,colmap,filename);
	}

	/**
	 * 读取GPExcel表格表头的内容
	 * 
	 * @return String 表头内容的数组
	 */
	private Map<String,Integer> readGpExcelTitle(Row row) {
		//row = sheet.getRow(0);
		// 标题总列数
		Map<String,Integer> colmap=new HashMap<String,Integer>();
		int colNum = row.getLastCellNum();
		String tCell="";
		for (int i = 0; i < colNum; i++) {
			if (null == row.getCell(i)) {
				continue;
			}
			tCell=getCellFormatValue(row.getCell(i));
			if(B.Y(tCell) || "null".equalsIgnoreCase(tCell))
				continue;
			if ("企业名称".contains(tCell)) {
				putColName(colmap,Colums.hy_member.sshortname,i);
				continue;
			}
			if ("用户名".contains(tCell)) {
				putColName(colmap,Colums.hy_user.susername,i);
				continue;
			}
			if ("密码".contains(tCell)) {
				putColName(colmap,Colums.hy_user.spassword,i);
				continue;
			}
			if ("手机号".contains(tCell)) {
				putColName(colmap,Colums.hy_user.smobile,i);
				continue;
			}
			if ("企业全称".contains(tCell)) {
				putColName(colmap,Colums.hy_member.scnname,i);
				continue;
			}
			if ("法人".contains(tCell)) {
				putColName(colmap,Colums.hy_member.slegalperson,i);
				continue;
			}
			if ("法人身份证号".contains(tCell)) {
				putColName(colmap,Colums.hy_member.slegalpersoncode,i);
				continue;
			}
			if ("联系人".contains(tCell)) {
				putColName(colmap,Colums.hy_member.slinkman,i);
				continue;
			}
			if ("联系电话".contains(tCell)) {
				putColName(colmap,Colums.hy_member.sphone,i);
				continue;
			}
			/*if ("第二联系人".contains(tCell)) {
				putColName(colmap,Colums.hy_member.slinkmantwo,i);
				continue;
			}
			if ("第二联系电话".contains(tCell)) {
				putColName(colmap,Colums.hy_member.sphonetwo,i);
				continue;
			}
			if ("邮箱".contains(tCell)) {
				putColName(colmap,Colums.hy_member.semail,i);
				continue;
			}
			if ("传真".contains(tCell)) {
				putColName(colmap,Colums.hy_member.sfax,i);
				continue;
			}*/
			if ("开户银行".contains(tCell)) {
				putColName(colmap,Colums.hy_member.sopenbank,i);
				continue;
			}
			if ("开户账号".contains(tCell)) {
				putColName(colmap,Colums.hy_member.sopenaccount,i);
				continue;
			}
			if ("统一代码".contains(tCell)) {
				putColName(colmap,Colums.hy_member.ssocialcreditno,i);
				continue;
			}
			if ("办公地址".contains(tCell)) {
				putColName(colmap,Colums.hy_member.sbusaddress,i);
				continue;
			}
			if ("申请日期".contains(tCell)) {
				putColName(colmap,Colums.hy_member.dadddate,i);
				continue;
			}

		}
		return colmap;
	}

	private void putColName(Map<String,Integer> colmap,String name,int i){
		if (colmap.containsKey(name)) {
			E.S("Excel格式不正确：表头列名重复");
		}
		colmap.put(name,i);
	}

	/**
	 * 读取GPExcel数据内容
	 * 
	 * @return Map 包含单元格数据内容的Map对象
	 */
	private void readExcelContent(Sheet sheet,Map<String,Integer> colmap,String excelfilename) {
		//List<InputOrderVo> zylist = Lists.newArrayList();
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		//System.out.println("-------"+sheet.getLastRowNum()+"======"+sheet.getPhysicalNumberOfRows());
		Row row = sheet.getRow(0);
		//int colNum = row.getPhysicalNumberOfCells();
		// 正文内容应该从第二行开始,第一行为表头的标题
		String DFSmeasurement="";
		String scode="";
		for (int i = 2; i <=rowNum; i++) {
			row = sheet.getRow(i);
			if (row == null || row.getFirstCellNum() == -1) {
				continue;
				//E.S("第" + (i + 1) + "行数据格式不正确");
			}
			if("END".equalsIgnoreCase(getCellFormatValue(row.getCell(0)))){
				break;
			}

			if (B.Y(getCellValue(colmap,row, Colums.hy_member.sshortname))) {
				E.S("第" + (i + 1) + "行企业简称不能为空");
			}
			if (B.Y(getCellValue(colmap,row, Colums.hy_member.scnname))) {
				E.S("第" + (i + 1) + "行企业名称不能为空");
			}
			if (B.Y(getCellValue(colmap,row, Colums.hy_user.susername))) {
				E.S("第" + (i + 1) + "行用户名不能为空");
			}
			if (getCellValue(colmap,row, Colums.hy_user.susername).length() < 6) {
				E.S("第" + (i + 1) + "行用户名长度不能小于6");
			}
			if (B.Y(getCellValue(colmap,row, Colums.hy_user.spassword))) {
				E.S("第" + (i + 1) + "行密码不能为空");
			}
			if (getCellValue(colmap,row, Colums.hy_user.spassword).length() < 6) {
				E.S("第" + (i + 1) + "行密码长度不能小于6");
			}
			/*if (B.Y(getCellValue(colmap,row, Colums.hy_user.smobile))) {
				E.S("第" + (i + 1) + "行手机号不能为空");
			}*/
			if(memberDao.countCName(getCellValue(colmap,row,Colums.hy_member.scnname),1)>0){
				E.S("企业名称【"+getCellValue(colmap,row, Colums.hy_member.scnname)+"】已经存在");
			}

			if(userDao.findBySusername(getCellValue(colmap,row, Colums.hy_user.susername)) != null) {
				E.S("用户名【" + getCellValue(colmap,row, Colums.hy_member.scnname) + "】已经存在");
			}


			if (userDao.count(Colums.hy_user.susername, getCellValue(colmap,row, Colums.hy_user.susername)) > 0)
				E.S("用户名【" + getCellValue(colmap,row, Colums.hy_member.scnname) + "】已经存在");

			Member member = new Member();

			member.setId(null);

			member.setSgpmemberid("");  //挂牌交易会员ID String
			member.setBismembercert(Consts.BoolType.YES.val());  //是否通过企业认证 int
			member.setSmemberno("UP"+ParaUtils.seqno(Colums.hy_member.tablename));  //企业编号 String
			member.setScnname(getCellValue(colmap,row,Colums.hy_member.scnname));  //企业名称 String
			member.setSenname("");  //英文名称 String
			member.setSshortname(getCellValue(colmap,row,Colums.hy_member.sshortname));  //中文简称 String
			member.setIregsource(Consts.IRegSource.OTHER.val());  //客户来源类型 int
			member.setSagentid("");  //客户来源编码 String
			member.setFbondamount(0D);  //保证金额 double
			member.setBisbond(Consts.BoolType.NO.val());  //是否已缴保证金 int
			member.setIcredit(0L);  //信用值 long
			member.setImembertype(Consts.MemberType.MEMBER.val());  //会员类型 int
			member.setSbusinessmode("");  //经营模式 String
			member.setSmemberkind("");  //企业性质 String
			member.setSbusinessno(getCellValue(colmap,row,Colums.hy_member.ssocialcreditno));  //工商执照号 String
			member.setSorgcode(getCellValue(colmap,row,Colums.hy_member.ssocialcreditno));  //组织机构代码 String
			member.setSlegalpersoncode(getCellValue(colmap,row,Colums.hy_member.slegalpersoncode));  //法定代表人身份证 String
			member.setStaxno(getCellValue(colmap,row,Colums.hy_member.ssocialcreditno));  //税务登记号 String
			member.setSsocialcreditno(getCellValue(colmap,row,Colums.hy_member.ssocialcreditno));  //统一社会信用代码 String
			member.setBiscardmerged(Consts.BoolType.YES.val());  //是否三码和一 int
			member.setBisone(Consts.BoolType.YES.val());  //是否三证合一 int
			member.setSlegalperson(getCellValue(colmap,row,Colums.hy_member.slegalperson));  //法人 String
			member.setIemployeeamount(0L);  //员工人数 long
			member.setScountry("中国");  //国家 String
			member.setSareaid("");  //地区区号 String
			member.setSprovince("");  //省份 String
			member.setScity("");  //城市 String
			member.setSregion("");  //区县 String
			member.setSregmoneytype("");  //注册资金货币种类 String
			member.setIregmoney(0L);  //注册资金 long
			member.setSopenbank(getCellValue(colmap,row, Colums.hy_member.sopenbank));  //开户银行 String
			member.setSopenname(getCellValue(colmap,row, Colums.hy_member.scnname));  //开户名称 String
			member.setSopenaccount(getCellValue(colmap,row,Colums.hy_member.sopenaccount));  //开户账号 String
			member.setSbalancebank("");  //结算银行 String
			member.setSbalancename("");  //结算户名 String
			member.setSfax(getCellValue(colmap,row,Colums.hy_member.sfax));  //传真 String
			member.setSbalanceaccount("");  //结算行帐号 String
			member.setBcaflag(Consts.BoolType.NO.val());  //是否使用CA int
			member.setSbusbigtype("");  //主营行业大类 String
			member.setSbussmalltype("");  //主营行业小类 String
			member.setSbussmallname("");  //主营小类名称 String
			member.setSregaddress("");  //注册地址 String
			member.setSbusaddress(getCellValue(colmap,row,Colums.hy_member.sbusaddress));  //经营地址 String
			member.setSlinkman(getCellValue(colmap,row,Colums.hy_member.slinkman));  //联系人 String
			member.setSzipcode("");  //邮编 String
			member.setSphone(getCellValue(colmap,row,Colums.hy_member.sphone));  //电话 String
			member.setSmobile(getCellValue(colmap,row,Colums.hy_user.smobile));  //手机 String
			member.setSemail(getCellValue(colmap,row,Colums.hy_member.semail));  //Email String
			member.setSlinkmantwo(getCellValue(colmap,row,Colums.hy_member.slinkmantwo));  //联系人二 String
			member.setSphonetwo(getCellValue(colmap,row,Colums.hy_member.sphonetwo));  //联系电话二 String
			member.setScompanydesc("");  //公司简介 String
			member.setDvaliddate(DateUtils.addYears(T.Date(),100));  //有效期 Date
			member.setImemberstatus(Consts.MemberStatus.NORMAL.val());  //状态 int
			member.setIscope(1);  //服务范围 int
			member.setScheckuserid("");  //审核人 String
			Date adddate=T.now();
			try {
				System.out.println(colmap.get(Colums.hy_member.dadddate)+"======="+getCellValue(colmap,row,Colums.hy_member.dadddate));
				String d=getCellValue(colmap,row,Colums.hy_member.dadddate);
				if(B.N(d)){
					if(d.indexOf("/") != -1){
						adddate = DateUtils.parseDateByFormat(getCellValue(colmap,row,Colums.hy_member.dadddate),"yyyy/MM/dd");
					}else{
						adddate = DateUtils.parseDateByFormat(getCellValue(colmap,row,Colums.hy_member.dadddate),"yyyy-MM-dd");
					}
				}


			} catch (Exception e) {
				e.printStackTrace();
				E.S("第" + (i + 1) + "行申请日期不合法");
			}
			member.setDapplydate(adddate);  //申请日期 Date
			member.setDcheckdate(adddate);  //审核日期 Date
			member.setDadddate(adddate);  //添加日期 Date
			member.setDmodifydate(adddate);  //修改日期 Date
			member.setScheckinfo("");  //审核意见 String
			//member.setSremark("");  //备注 String
			member.setBdelete(Consts.BoolType.NO.val());  //是否删除 int
			member.setSinvoicephone( getCellValue(colmap,row,Colums.hy_member.sphone));  //开票电话 String
			member.setSinvoiceaddress(getCellValue(colmap,row,Colums.hy_member.sbusaddress));  //开票地址 String
			member.setSbanner("");  //店铺banner String
			member.setSlogo("");  //会员LOGO String
			member.setSweburl("");  //网站 String
			member.setSadduser("");  //添加人 String
			member.setSmodifyoperator("");  //修改人 String
			member.setSshortnamejpname(StringHelper.getFirstSpell(getCellValue(colmap,row,Colums.hy_member.sshortname)));  //简称简拼名 String
			member.setSshortnamepyname(StringHelper.getFullSpell(getCellValue(colmap,row,Colums.hy_member.sshortname)));  //简称全拼名 String
			member.setSjpname(StringHelper.getFirstSpell(getCellValue(colmap,row,Colums.hy_member.scnname)));  //简拼名 String
			member.setImaxoperator(5L);  //最大交易员数 long
			member.setImembervisibility(1);  //会员可见范围 int
			member.setSpyname(StringHelper.getFullSpell(getCellValue(colmap,row,Colums.hy_member.scnname)));  //全拼名 String
			member.setIauthtype(Consts.AuthenticateType.Company.val());  //认证类型 int
			member.setImaxtopresource(1000L);  //最大推荐资源数 long
			member.setBisseller(0);  //是否分销商 int
			member.setBisspread(0);  //是否推广协议 int
			member.setSgpurl("");  //挂牌excel路径 String
			member.setSgqurl("");  //资源单excel路径 String
			member.setIintegralavailable(0L);  //当前可用积分 long
			member.setIintegralamount(0L);  //总积分 long
			member.setIintegralused(0L);  //已兑换积分 long
			member.setSgpfilename("");  //挂牌excel源文件名 String
			member.setSgqfilename("");  //供应excel源文件名 String
			member.setScontractterms("");  //合同默认条款 String
			member.setBisgangchang(0);  //是否钢厂 int
			member.setBisbaojia(0);  //是否报价 int
			member.setIsort(0L);  //排序号 long
			member.setSlocation("");  //地理位置 String
			member.setIsite(0);  //默认分站 int
			member.setBisstop(1);  //是否营业 int
			member.setBisselfsaler(0);  //是否自营 int
			member.setBisbind(0);  //是否关联 int
			member.setSbindno("");  //关联编号 String
			member.setBissupply(1);  //是否供应商 int
			member.setBisbuyer(1);  //是否采购商 int
			member.setSremark(T.now()+"="+excelfilename+"="+i);

			memberDao.save(member);// 保存会员信息

			User operatorBean = new User();
			operatorBean.setSusername(getCellValue(colmap,row,Colums.hy_user.susername));// 交易员登录名
			operatorBean.setSname(getCellValue(colmap,row,Colums.hy_member.slegalperson));// 交易员名称
			operatorBean.setSmobile(getCellValue(colmap,row,Colums.hy_user.smobile));// 联系手机
			operatorBean.setSemail(getCellValue(colmap,row,Colums.hy_member.semail));// 联系邮箱
			// operatorBean.setSfax(vo.getSfax());// 传真
			operatorBean.setSphone(getCellValue(colmap,row,Colums.hy_member.sphone));// 联系电话

			operatorBean.setSmemberid(member.getId());// 设置会员ID
			operatorBean.setSoperatorno("UP"+userDao.getSeqNo(Colums.hy_user.tablename));// 设置交易员编号
			operatorBean.setSpassword(MD5.encode(getCellValue(colmap,row,Colums.hy_user.spassword)));// 加密密码
			System.out.println("======"+getCellValue(colmap,row,Colums.hy_user.spassword)+"======");
			operatorBean.setDadddate(T.Date());// 设置交易员添加日期
			operatorBean.setDmodifydate(T.Date());// 设置最后修改日期
			operatorBean.setBisadmin(Consts.BoolType.YES.val());// 设置为默认管理员
			operatorBean.setBisvalid(Consts.BoolType.YES.val());// 设置交易员是否有效
			operatorBean.setBisdelete(Consts.BoolType.NO.val());// 设置交易员是否删除
			operatorBean.setBisymobile(1);
			operatorBean.setBisyemail(0);
			userDao.saveAndFlush(operatorBean);// 保存交易员信息


			Memberaccount memberaccount=new Memberaccount();
			memberaccount.setSmemberid(member.getId());
			memberaccount.setFtotalroan(0.0);
			memberaccount.setFnowroan( 0.0);
			memberaccount.setFpaidinterestotal(0.0);
			memberaccount.setFpayableinterest(0.0);
			memberaccount.setFbidtotal( 0.0);
			memberaccount.setFnowbid(0.0);
			memberaccount.setFreceivedinteresttotal( 0.0);
			memberaccount.setFreceivableinterest(0.0);
			memberaccount.setIborrowtime(0l); //借款次数 long
			memberaccount.setIbidtime(0l); //投资次数 long
			memberaccount.setFfreeout(0.0);
			memberaccount.setFfreeouted(0.0);
			memberaccountDao.save(memberaccount);

			MemberInvoice mi = new MemberInvoice();
			mi.setId(null);
			mi.setSmembercnname(member.getScnname());
			mi.setSmemberid(member.getId());
			mi.setDadddate(T.now());
			mi.setIsite(Consts.SiteType.Host.val());
			mi.setSaddrandtel(member.getSinvoicephone());
			mi.setSbankname(member.getSopenbank());
			mi.setSbankankaccount(member.getSopenaccount());
			mi.setSpostaddress(member.getSinvoiceaddress());
			mi.setStaxcode("");

			this.memberInvoiceDao.save(mi);

		}
		//return zylist;
	}

	private String getCellValue(Map<String,Integer> colmap,Row crow,String celName){
		if(!colmap.containsKey(celName))
			return "";
		if(null == crow.getCell(colmap.get(celName)))
			return "";
		return getCellFormatValue(crow.getCell(colmap.get(celName)));
	}

	// 构造参数
	/*private  void paramListToMap() {
		if(map.isEmpty()){
			List<BusinessParameterVo> params = ParaUtils.biz(BizParaType.OrderExcel.val());
			for (BusinessParameterVo param : params) {
				String[] temp = param.getSparametervalue().split("~");
				if (temp.length < 1) {
					E.S("系统配置错误: " + param.getSparametername() + ",请联系客服人员");
				}
				List<String> valuelist = new ArrayList<String>();
				for (int i = 0; i < temp.length; i++) {
					valuelist.add(temp[i]);
				}
				map.put(param.getSparametername(), valuelist);
			}
		}
	}*/



	

	/**
	 * 根据HSSFCell类型设置数据
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellFormatValue(Cell cell) {
		String cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			// 如果当前Cell的Type为NUMERIC
			case HSSFCell.CELL_TYPE_NUMERIC:
			case HSSFCell.CELL_TYPE_FORMULA: {
				// 判断当前的cell是否为Date
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式

					// 方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();

					// 方法2：这样子的data格式是不带带时分秒的：2011-10-12
					Date date = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					cellvalue = sdf.format(date);

				}
				// 如果是纯数字
				else {
					// 取得当前Cell的数值
					double d = cell.getNumericCellValue();
					if (d - (int) d < Double.MIN_VALUE) {
						// 是否为int型
						cellvalue = Integer.toString((int) d);
					} else {
						// 是否为double型
						cellvalue = Double.toString(cell.getNumericCellValue());
					}
				}
				break;
			}
			// 如果当前Cell的Type为STRIN
			case HSSFCell.CELL_TYPE_STRING:
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			// 默认的Cell值
			default:
				cellvalue = " ";
			}
		} else {
			cellvalue = "";
		}
		return B.Y(cellvalue)?cellvalue:cellvalue.trim();

	}

	public static void main(String[] args) {
		 try {
			 String a="12";
			 System.out.println(Integer.getInteger(a));
			 System.out.println(Long.parseLong(a));
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		
	}
}
