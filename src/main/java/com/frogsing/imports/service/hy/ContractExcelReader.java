package com.frogsing.imports.service.hy;

import com.frogsing.dao.ht.ContractDao;
import com.frogsing.dao.ht.ContractDetailDao;
import com.frogsing.dao.ht.SendOrderDao;
import com.frogsing.dao.ht.SendOrderDetailDao;
import com.frogsing.dao.hy.MemberDao;
import com.frogsing.dao.hy.UserDao;
import com.frogsing.dao.hy.WarehouseDao;
import com.frogsing.dao.sp.CommodityDao;
import com.frogsing.dao.zj.BillPayDao;
import com.frogsing.dao.zj.MemberFundDao;
import com.frogsing.dao.zy.SaleResourceDao;
import com.frogsing.dao.zy.StockDao;
import com.frogsing.dao.zy.StockDetailDao;
import com.frogsing.heart.exception.E;
import com.frogsing.heart.exception.ServiceException;
import com.frogsing.heart.security.utils.MD5;
import com.frogsing.heart.utils.*;
import com.frogsing.imports.vo.UpdateVo;
import com.frogsing.po.entity.ht.Contract;
import com.frogsing.po.entity.ht.ContractDetail;
import com.frogsing.po.entity.ht.SendOrder;
import com.frogsing.po.entity.ht.SendOrderDetail;
import com.frogsing.po.entity.hy.Member;
import com.frogsing.po.entity.hy.User;
import com.frogsing.po.entity.hy.Warehouse;
import com.frogsing.po.entity.mb.Categorycontent;
import com.frogsing.po.entity.sp.Commodity;
import com.frogsing.po.entity.zj.BillPay;
import com.frogsing.po.entity.zj.MemberFund;
import com.frogsing.po.entity.zy.SaleResource;
import com.frogsing.po.entity.zy.Stock;
import com.frogsing.po.entity.zy.StockDetail;
import com.frogsing.po.utils.Colums;
import com.frogsing.po.utils.Consts;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.TemplateException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Transactional
public class ContractExcelReader {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private WarehouseDao warehouseDao;
	@Autowired
	private CommodityDao commodityDao;
	@Autowired
	private MemberFundDao memberFundDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ContractDao contractDao;
	@Autowired
	private CategorycontentService categorycontentService;
	@Autowired
	private ContractDetailDao contractDetailDao;
	@Autowired
	private SendOrderDao sendOrderDao;
	@Autowired
	private SendOrderDetailDao sendOrderDetailDao;
	@Autowired
	private BillFileService billFileService;
	@Autowired
	private StockDao stockDao;
	@Autowired
	private SaleResourceDao saleResourceDao;
	@Autowired
	private BillPayDao billPayDao;
	@Autowired
	private StockDetailDao stockDetailDao;

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
			if(B.Y(tCell) || "null".equalsIgnoreCase(tCell)) {
				continue;
			}
			if ("序号".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.scontractno,i);
				continue;
			}
			if ("挂牌日期".contains(tCell)) {
				putColName(colmap,Colums.zy_saleresource.dbegindate,i);
				continue;
			}
			if ("合同日期".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.dcontractdate,i);
				continue;
			}
			if ("支付日期".contains(tCell)) {
				putColName(colmap,Colums.zj_billpay.dpaytime,i);
				continue;
			}
			if ("交货日期".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.ddeliverydate,i);
				continue;
			}
			if ("采购方,采购方全称,采购企业全称,买方,买方会员".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.sbuyermembername,i);
				continue;
			}
			if ("销售方,销售方全称,销售方企业全称,卖方,卖方会员".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.ssellermembername,i);
				continue;
			}
			if ("仓库,存货仓库,仓库名称,仓库全称".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.swarehouse,i);
				continue;
			}
			if ("品名,品名全称".contains(tCell)) {
				putColName(colmap,Colums.ht_contractdetail.scommodityname,i);
				continue;
			}
			if ("规格".contains(tCell)) {
				putColName(colmap,Colums.ht_contractdetail.sspec,i);
				continue;
			}
			if ("材质".contains(tCell)) {
				putColName(colmap,Colums.ht_contractdetail.smaterial,i);
				continue;
			}
			if ("单价,价格".contains(tCell)) {
				putColName(colmap,Colums.ht_contractdetail.fprice,i);
				continue;
			}
			if ("数量,重量".contains(tCell)) {
				putColName(colmap,Colums.ht_contractdetail.fweight,i);
				continue;
			}
			if ("发货日期".contains(tCell)) {
				putColName(colmap,Colums.ht_sendorder.dorderdate,i);
				continue;
			}
			if ("收货日期".contains(tCell)) {
				putColName(colmap,Colums.ht_sendorder.dacceptdate,i);
				continue;
			}
			if ("开票日期".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.dmakeinvoicedate,i);
				continue;
			}
			if ("收票日期".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.drsvinvoicedate,i);
				continue;
			}
			if ("发票附件英文名称".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.sinvalidatereason,i);
				continue;
			}
			if ("发票附件名称".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.sinvalidatereason+"CN",i);
				continue;
			}
			if ("品牌".contains(tCell)) {
				putColName(colmap,Colums.ht_contractdetail.sbrand,i);
				continue;
			}
			if ("生产厂家".contains(tCell)) {
				putColName(colmap,Colums.ht_contractdetail.sproducer,i);
				continue;
			}
			if ("合同附件英文名称".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.sterms,i);
				continue;
			}
			if ("合同附件名称".contains(tCell)) {
				putColName(colmap,Colums.ht_contract.sterms+"CN",i);
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
	private void readExcelContent(Sheet sheet,Map<String,Integer> colmap,String excelfilename) throws IOException, TemplateException {
		//List<InputOrderVo> zylist = Lists.newArrayList();
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		//System.out.println("-------"+sheet.getLastRowNum()+"======"+sheet.getPhysicalNumberOfRows());
		Row row ;
		//int colNum = row.getPhysicalNumberOfCells();
		// 正文内容应该从第二行开始,第一行为表头的标题
		Map<String,Map<String,Object>> map = new HashMap<String, Map<String, Object>>();
		//Map<String,Map<String,Object>> dtl = new HashMap<String, Map<String, Object>>();
		for (int i = 2; i <=rowNum; i++) {
			row = sheet.getRow(i);
			if (row == null || row.getFirstCellNum() == -1) {
				continue;
				//E.S("第" + (i + 1) + "行数据格式不正确");
			}
			if("END".equalsIgnoreCase(getCellFormatValue(row.getCell(0)))){
				break;
			}
			String code = getCellValue(colmap, row, Colums.ht_contract.scontractno);
			if (B.Y(code)) {
				E.S("第" + (i + 1) + "行合同序号不能为空");
			}

			if (B.Y(getCellValue(colmap, row, Colums.ht_contractdetail.scommodityname))) {
				E.S("第" + (i + 1) + "行品名不能为空");
			}
			if (B.Y(getCellValue(colmap, row, Colums.ht_contractdetail.fprice))) {
				E.S("第" + (i + 1) + "行单价不能为空");
			}
			if (B.Y(getCellValue(colmap, row, Colums.ht_contractdetail.fweight))) {
				E.S("第" + (i + 1) + "行数量不能为空");
			}

			List<Commodity> commoditys = commodityDao.findBySname(getCellValue(colmap, row, Colums.ht_contractdetail.scommodityname));
			if (B.Y(commoditys)) {
				E.S("第" + (i + 1) + "行品名【" + getCellValue(colmap, row, Colums.ht_contractdetail.scommodityname) + "】不存在");
			}

			double fprice = 0D;
			try {
				fprice = Double.parseDouble(getCellValue(colmap, row, Colums.ht_contractdetail.fprice));
			} catch (Exception e) {
				e.printStackTrace();
				E.S("第" + (i + 1) + "行单价不合法");
			}
			double fweight = 0D;
			try {
				fweight = Double.parseDouble(getCellValue(colmap, row, Colums.ht_contractdetail.fweight));
			} catch (Exception e) {
				e.printStackTrace();
				E.S("第" + (i + 1) + "行数量不合法");
			}

			if (code.indexOf("~") == -1) {
				map.put(code, new HashMap<String, Object>());
				if (B.Y(getCellValue(colmap, row, Colums.ht_contract.dcontractdate))) {
					E.S("第" + (i + 1) + "行合同日期不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.zy_saleresource.dbegindate))) {
					E.S("第" + (i + 1) + "行挂牌日期不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.zj_billpay.dpaytime))) {
					E.S("第" + (i + 1) + "行支付日期不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.ht_contract.sbuyermembername))) {
					E.S("第" + (i + 1) + "行采购方全称不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.ht_contract.ssellermembername))) {
					E.S("第" + (i + 1) + "行销售方全称不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.ht_contract.swarehouse))) {
					E.S("第" + (i + 1) + "行仓库不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.ht_sendorder.dorderdate))) {
					E.S("第" + (i + 1) + "行发货日期不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.ht_sendorder.dacceptdate))) {
					E.S("第" + (i + 1) + "行收货日期不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.ht_contract.dmakeinvoicedate))) {
					E.S("第" + (i + 1) + "行开票日期不能为空");
				}
				if (B.Y(getCellValue(colmap, row, Colums.ht_contract.drsvinvoicedate))) {
					E.S("第" + (i + 1) + "行收票日期不能为空");
				}
				Date dcontractdate = null;
				try {
					dcontractdate = DateUtils.parseDateByFormat(getCellValue(colmap, row, Colums.ht_contract.dcontractdate), "yyyy-MM-dd");
					map.get(code).put(Colums.ht_contract.dcontractdate, dcontractdate);
				} catch (Exception e) {
					e.printStackTrace();
					E.S("第" + (i + 1) + "行合同日期不合法");
				}
				Date dorderdate = null;
				try {
					dorderdate = DateUtils.parseDateByFormat(getCellValue(colmap, row, Colums.ht_sendorder.dorderdate), "yyyy-MM-dd");
					map.get(code).put(Colums.ht_sendorder.dorderdate, dorderdate);
				} catch (Exception e) {
					e.printStackTrace();
					E.S("第" + (i + 1) + "行发货日期不合法");
				}
				Date dacceptdate = null;
				try {
					dacceptdate = DateUtils.parseDateByFormat(getCellValue(colmap, row, Colums.ht_sendorder.dacceptdate), "yyyy-MM-dd");
					map.get(code).put(Colums.ht_sendorder.dacceptdate, dacceptdate);
				} catch (Exception e) {
					e.printStackTrace();
					E.S("第" + (i + 1) + "行收货日期不合法");
				}
				Date dmakeinvoicedate = null;
				try {
					dmakeinvoicedate = DateUtils.parseDateByFormat(getCellValue(colmap, row, Colums.ht_contract.dmakeinvoicedate), "yyyy-MM-dd");
					map.get(code).put(Colums.ht_contract.dmakeinvoicedate, dmakeinvoicedate);
				} catch (Exception e) {
					e.printStackTrace();
					E.S("第" + (i + 1) + "行开票日期不合法");
				}
				Date drsvinvoicedate = null;
				try {
					drsvinvoicedate = DateUtils.parseDateByFormat(getCellValue(colmap, row, Colums.ht_contract.drsvinvoicedate), "yyyy-MM-dd");
					map.get(code).put(Colums.ht_contract.drsvinvoicedate, drsvinvoicedate);
				} catch (Exception e) {
					e.printStackTrace();
					E.S("第" + (i + 1) + "行收票日期不合法");
				}

				Date dgpdate = null;
				try {
					dgpdate = DateUtils.parseDateByFormat(getCellValue(colmap, row, Colums.zy_saleresource.dbegindate), "yyyy-MM-dd");
					map.get(code).put(Colums.zy_saleresource.dbegindate, dgpdate);
				} catch (Exception e) {
					e.printStackTrace();
					E.S("第" + (i + 1) + "行挂牌日期不合法");
				}

				Date dpaydate = null;
				try {
					dpaydate = DateUtils.parseDateByFormat(getCellValue(colmap, row, Colums.zj_billpay.dpaytime), "yyyy-MM-dd");
					map.get(code).put(Colums.zj_billpay.dpaytime, dpaydate);
				} catch (Exception e) {
					e.printStackTrace();
					E.S("第" + (i + 1) + "行支付日期不合法");
				}

				Date ddeliverydate = null;
				try {
					ddeliverydate = DateUtils.parseDateByFormat(getCellValue(colmap, row, Colums.ht_contract.ddeliverydate), "yyyy-MM-dd");
					map.get(code).put(Colums.ht_contract.ddeliverydate, ddeliverydate);
				} catch (Exception e) {
				}
				Member buyer = memberDao.findByScnname(getCellValue(colmap, row, Colums.ht_contract.sbuyermembername));
				if (buyer == null) {
					E.S("第" + (i + 1) + "行采购方【" + getCellValue(colmap, row, Colums.ht_contract.sbuyermembername) + "】会员不存在");
				}
				map.get(code).put("buyer", buyer);
				Member seller = memberDao.findByScnname(getCellValue(colmap, row, Colums.ht_contract.ssellermembername));
				if (seller == null) {
					E.S("第" + (i + 1) + "行销售方【" + getCellValue(colmap, row, Colums.ht_contract.ssellermembername) + "】会员不存在");
				}
				map.get(code).put("seller", seller);
				User buyu = userDao.findBySmemberidAndBisadmin(buyer.getId(), 1);
				User sellu = userDao.findBySmemberidAndBisadmin(seller.getId(), 1);

				map.get(code).put("buyu", buyu);
				map.get(code).put("sellu", sellu);
				Warehouse warehouse = warehouseDao.findBySwarehousename(getCellValue(colmap, row, Colums.ht_contract.swarehouse));
				if (warehouse == null) {
					E.S("第" + (i + 1) + "行仓库【" + getCellValue(colmap, row, Colums.ht_contract.swarehouse) + "】不存在");
				}
				map.get(code).put("warehouse", warehouse);

				map.get(code).put("commodity", commoditys.get(0));
				map.get(code).put(Colums.ht_contractdetail.fprice, fprice);
				map.get(code).put(Colums.ht_contractdetail.fweight, fweight);
				map.get(code).put(Colums.ht_contractdetail.sspec, getCellValue(colmap,row, Colums.ht_contractdetail.sspec));
				map.get(code).put(Colums.ht_contractdetail.smaterial, getCellValue(colmap,row, Colums.ht_contractdetail.smaterial));
				map.get(code).put(Colums.ht_contractdetail.sbrand, getCellValue(colmap,row, Colums.ht_contractdetail.sbrand));
				map.get(code).put(Colums.ht_contractdetail.sproducer, getCellValue(colmap,row, Colums.ht_contractdetail.sproducer));

				map.get(code).put(Colums.ht_contract.sinvalidatereason, getCellValue(colmap,row, Colums.ht_contract.sinvalidatereason));
				map.get(code).put(Colums.ht_contract.sinvalidatereason+"CN", getCellValue(colmap,row, Colums.ht_contract.sinvalidatereason+"CN"));
				map.get(code).put(Colums.ht_contract.sterms, getCellValue(colmap,row, Colums.ht_contract.sterms));
				map.get(code).put(Colums.ht_contract.sterms+"CN", getCellValue(colmap,row, Colums.ht_contract.sterms+"CN"));
				map.get(code).put("row",i);
			} else {
				code = code.substring(0, code.indexOf("~"));
				List<Map<String, Object>> dtllist = Lists.newArrayList();
				if (map.get(code).containsKey("dtllist"))
					dtllist = (List<Map<String, Object>>) map.get(code).get("dtllist");
				Map<String, Object> dtlmap = Maps.newHashMap();
				dtlmap.put("commodity", commoditys.get(0));
				dtlmap.put(Colums.ht_contractdetail.fprice, fprice);
				dtlmap.put(Colums.ht_contractdetail.fweight, fweight);
				dtlmap.put(Colums.ht_contractdetail.sspec, getCellValue(colmap,row, Colums.ht_contractdetail.sspec));
				dtlmap.put(Colums.ht_contractdetail.smaterial, getCellValue(colmap,row, Colums.ht_contractdetail.smaterial));
				dtlmap.put(Colums.ht_contractdetail.sbrand, getCellValue(colmap,row, Colums.ht_contractdetail.sbrand));
				dtlmap.put(Colums.ht_contractdetail.sproducer, getCellValue(colmap,row, Colums.ht_contractdetail.sproducer));
				dtllist.add(dtlmap);
				map.get(code).put("dtllist", dtllist);
			}
			/**------------**/
		}

		if(map.isEmpty())
			return;
		for(Map.Entry<String,Map<String,Object>> entry:map.entrySet()){
			Map<String,Object> curmap=entry.getValue();
			Member seller =(Member) curmap.get("seller");
			Member buyer =(Member) curmap.get("buyer");
			User sellu =(User) curmap.get("sellu");
			User buyu =(User) curmap.get("buyu");
			Commodity commoditys =(Commodity) curmap.get("commodity");
			Warehouse warehouse =(Warehouse) curmap.get("warehouse");
			Date dcontractdate = (Date)curmap.get(Colums.ht_contract.dcontractdate);
			Date dorderdate = (Date)curmap.get(Colums.ht_sendorder.dorderdate);
			Date dacceptdate = (Date)curmap.get(Colums.ht_sendorder.dacceptdate);

			Date dmakeinvoicedate = (Date)curmap.get(Colums.ht_contract.dmakeinvoicedate);
			Date drsvinvoicedate = (Date)curmap.get(Colums.ht_contract.drsvinvoicedate);
			Date dgpdate = (Date)curmap.get(Colums.zy_saleresource.dbegindate);
			Date dpaydate = (Date)curmap.get(Colums.zj_billpay.dpaytime);

			Date ddeliverydate = (Date)curmap.get(Colums.ht_contract.ddeliverydate);
			Double fweight=(Double) curmap.get(Colums.ht_contractdetail.fweight);
			Double fprice=(Double) curmap.get(Colums.ht_contractdetail.fprice);


			Stock stock=new Stock();
			stock.setId(null);
			stock.setIresourcetype(Consts.SaleResourceType.General.val());  //资源类型 int
			stock.setSbillid("");  //单据id String
			String stockno = "UPSK"+DateUtils.dateToString((Date)curmap.get("dcontractdate"),"yyMMdd")+StringHelper.randomNum(10);
			stock.setSstockno(stockno);  //库存编号 String
			stock.setIsitetype(Consts.SiteType.Host.val());  //分站 int
			stock.setIlight(0);  //验证类型 int
			stock.setSbindno("");  //捆包号 String
			stock.setSfriendno("");  //仓库资源code String
			stock.setSmemberid(seller.getId());
			stock.setSuserid(sellu.getId());
			stock.setSusername(sellu.getSusername());
			stock.setSmembername(seller.getScnname());
			stock.setSmemberno(seller.getSmemberno());
			stock.setSbigcategoryid(commoditys.getCategory().getSparentid());  //品种大类ID String
			stock.setSbigcategorysystemcode("");  //品种大类编码 String
			stock.setSbigcategoryname("");  //品种大类名称 String
			stock.setScategorysystemcode(commoditys.getCategory().getSsystemcode());  //品种编码 String
			stock.setScategoryid(commoditys.getCategory().getId());  //品种ID String
			stock.setScategory(commoditys.getCategory().getSname());  //品种名称 String
			stock.setScommodityid(commoditys.getId());  //品名ID String
			stock.setScommoditysystemcode(commoditys.getScommoditysystemcode());  //品名编码 String
			stock.setScommodityname(commoditys.getSname());  //品名 String
			stock.setSlocalname("");  //俗名 String
			stock.setSenname("");  //英文名 String
			stock.setSsubstitute("");  //替代品 String
			stock.setSclassification("");  //所属分类 String
			stock.setSbigpic("");  //大图片 String
			stock.setSwarrantyurl("");  //质保书地址 String
			stock.setStinypic("");  //小图片 String
			stock.setIlcfs(0);  //量尺方式 int
			stock.setSwoodlevel("");  //等级 String


			stock.setSspec((String) curmap.get(Colums.ht_contractdetail.sspec));  //规格 String
			stock.setFspeca(0D);  //规格参数1 double
			stock.setFspecb(0D);  //规格参数2 double
			stock.setFspecc(0D);  //规格参数3 double
			stock.setSmaterial((String) curmap.get(Colums.ht_contractdetail.smaterial));  //材质 String
			stock.setSorigincountryid("");  //产地国家id String
			stock.setSoriginareaid("");  //产地地区id String
			stock.setSorigincountry("");  //产地国家 String
			stock.setSoriginarea("");  //产地地区 String
			stock.setSbrand((String) curmap.get(Colums.ht_contractdetail.sbrand));  //品牌 String
			stock.setSproducer((String) curmap.get(Colums.ht_contractdetail.sproducer));  //生产厂家 String
			stock.setSpacking("");  //包装 String
			stock.setSweightunit(commoditys.getSweightunit());  //数量单位 String
			stock.setSstandard("");  //技术标准 String
			stock.setSmeasurement("");  //计量方式 String
			stock.setDproductiondate(null);  //生产日期 Date
			stock.setIwarranty(0);  //质保书 int
			stock.setSdeliverytype("");  //交货方式 String
			stock.setSprovince(warehouse.getSprovince());  //交货省份 String
			stock.setScity(warehouse.getScity());  //交货城市 String
			stock.setSaddress(warehouse.getSaddress());  //交货地址 String
			stock.setSwarehouse(warehouse.getSwarehousename());  //交货仓库 String
			stock.setSwarehousebindcode(warehouse.getSbindcode());  //联盟仓库代码 String
			stock.setSwarehouseid(warehouse.getId());  //仓库ID String
			stock.setFweight(fweight);  //重量 double
			stock.setIlocknumber(0L);  //锁定件数 long
			stock.setFlockweight(0D);  //锁定重量 double
			stock.setInumber(1L);  //件数 long
			stock.setFnumberweight(fweight);  //单件重量 double
			stock.setFmatchweight(fweight);  //成交量 double
			stock.setImatchnumber(1L);  //成交件数 long
			stock.setDarrivedate(null);  //到货日期 Date
			stock.setBisonsale(Consts.BoolType.NO.val());  //是否挂牌 int
			stock.setBisdelete(Consts.BoolType.NO.val());  //是否删除 int
			stock.setIversion(0L);  //版本号 long
			stock.setSserviceman(sellu.getSname());  //联系人 String
			stock.setSservicetel(sellu.getSmobile());  //联系电话 String
			stock.setSserviceqq(sellu.getSqq());  //联系QQ String
			stock.setSaddoperator(sellu.getSname());  //添加人 String
			stock.setDadddate(dgpdate);  //添加日期 Date
			stock.setDmodifydate(dgpdate);  //修改日期 Date
			stock.setSmodifyoperator("");  //修改人 String
			stock.setSdescription("");  //详情描述 String
			stock.setSbindremark("");  //验货备注 String
			stock.setSremark("");  //备注 String
			stock.setSinputsrc(1);  //录入方式 int
			stock.setShashcode(hashStock(stock));  //哈希值 String


			stockDao.save(stock);
			List<Map<String,Object>> list=Lists.newArrayList();
			if(curmap.containsKey("dtllist")){
				list=(List<Map<String,Object>>)curmap.get("dtllist");
			}

			if(B.N(list)){
				for(Map<String,Object> obj:list){

					StockDetail sd=new StockDetail();
					sd.setId(null);
					sd.setStockid(stock.getId());
					sd.setScommodityid(((Commodity)obj.get("commodity")).getId());
					sd.setScommodityname(((Commodity)obj.get("commodity")).getSname());
					sd.setSbrand((String) obj.get(Colums.ht_contractdetail.sbrand));
					sd.setSspec((String) obj.get(Colums.ht_contractdetail.sspec));
					sd.setSmaterial((String) obj.get(Colums.ht_contractdetail.smaterial));
					sd.setSweightunit(((Commodity)obj.get("commodity")).getSweightunit());
					sd.setFweight((Double) obj.get(Colums.ht_contractdetail.fweight));
					sd.setFprice((Double) obj.get(Colums.ht_contractdetail.fprice));
					stockDetailDao.save(sd);
				}
			}

			SaleResource res=new SaleResource();
			res.setId(null);
			//添加挂牌资源
			String resno="UPZY"+DateUtils.dateToString(dgpdate,"yyMMdd")+StringHelper.randomNum(10);
			res.setSresourceno(resno);
			res.setIcredit(0l);
			res.setFprice(fprice);
			res.setSstockid(stock.getId());
			res.setSmemberid(seller.getId());
			res.setSmemberno(seller.getSmemberno());
			res.setSuserid(sellu.getId());
			res.setSusername(sellu.getSusername());
			res.setIupdown(0.0);
			res.setDbegindate(dgpdate);
			res.setIhitcount(Long.valueOf(StringHelper.randomNum(2)));
			res.setSaddoperator(sellu.getSusername());
			res.setDadddate(dgpdate);
			res.setBisspread(0);
			res.setBisindex(0);
			res.setBisinimage(0);
			res.setFminbuyamount(fweight);
			res.setIstatus(Consts.GPStatus.Over.val());
			res.setIdiscounttype(Consts.DiscountType.NO.val());
			res.setBisprotocal(Consts.BoolType.NO.val());
			this.saleResourceDao.save(res);


			Contract c=new Contract();
			c.setId(null);

			double famount=F.roundMoney(F.multiply(F.roundMoney(fprice),F.roundWeight(fweight)));

			MemberFund buyfund = null;
			MemberFund salefund = null;

			try {
				buyfund = memberFundDao.findFund(buyer.getId(),String.valueOf(Consts.BankType.CCB.val()));
			} catch (ServiceException ex) {
				//E.S("采购方-" + BankType.get(Integer.valueOf(Props.get("bank.default",String.valueOf(BankType.CCB.val())))).label() + ":"+ ex.getMessage());
			}
			try {
				salefund = memberFundDao.findFund(buyer.getId(),String.valueOf(Consts.BankType.CCB.val()));
			} catch (ServiceException ex) {
				//E.S("供应商-" + BankType.get(Integer.valueOf(Props.get("bank.default",String.valueOf(BankType.CCB.val())))).label() + ":" + ex.getMessage());
			}

			c.setBismakeinvoice(Consts.BoolType.YES.val());
			c.setDmakeinvoicedate(dmakeinvoicedate);
			c.setBisrsvinvoice(Consts.BoolType.YES.val());
			c.setDrsvinvoicedate(drsvinvoicedate);

			c.setIbanktype(3);
			c.setIsite(0);
			c.setBisordered(Consts.BoolType.YES.val());// 透明字段,用于是否一口价的判断
			// 需要根据分站查询，后需完善
			c.setDdeliverydate(ddeliverydate == null?DateUtils.addDays(dcontractdate, 7):ddeliverydate);

			c.setImodeltype(Consts.ModelType.Free.val());
			c.setIcontracttype(Consts.ContractType.COMMON.val());
			c.setDadddate(dcontractdate);
			c.setDcontractdate(dcontractdate);
			c.setDmodifydate(T.now());

			c.setSaddoperator("importer");
			c.setSbuyermemberid(buyer.getId());
			c.setSbuyermembername(buyer.getScnname());
			c.setSbuyermemberno(buyer.getSmemberno());
			c.setSbuyeraccountid(buyfund == null? "" : buyfund.getId());
			c.setSbuyermemberphone(buyer.getSphone());
			c.setSbuyersname(buyu.getSname());
			c.setSbuyeruserid(buyu.getId());
			c.setSbuyerusername(buyu.getSusername());


			String sno = "UPHT"+DateUtils.dateToString(dcontractdate,"yyMMdd")+StringHelper.randomNum(10);
			c.setSsyscontractno(sno);
			c.setScontractno(sno);
			c.setSsellermemberid(seller.getId());
			c.setSsellermembername(seller.getScnname());
			c.setSsellermemberno(seller.getSmemberno());
			c.setSselleraccountid(salefund == null? "" : salefund.getId());
			c.setSsellermemberphone(seller.getSphone());
			c.setSwarehouse(warehouse.getSwarehousename());
			c.setSwarehouseid(warehouse.getId());

			c.setIpaytype(Consts.AcctcPayType.OFFLINE.val());
			c.setIpricetype(Consts.PriceType.One.val());
			c.setIresourcetype(Consts.SaleResourceType.General.val());
			c.setBisadminadd(Consts.BoolType.YES.val());

			c.setFaddamount(0D);
			c.setFbondamount(0D);
			c.setFbuyerlockable(famount);
			c.setFbuyerlocked(famount);
			c.setFbuyerpaid(famount);
			c.setFdeliveryfund(famount);
			c.setFbuyerpayable(famount);
			c.setFrefund(0D);
			c.setFsellerreceivable(famount);
			c.setFsellerreceived(famount);
			c.setFtotalamount(famount);
			c.setFtotalweight(fweight);
			c.setFdeliverygoodsfund(famount);
			c.setFdeliveryweight(fweight);

			c.setBisbuyerjudge(Consts.BoolType.NO.val());
			c.setBissellerjudge(Consts.BoolType.NO.val());
			c.setIprogress(Consts.ContractProgress.gFinish.val());
			c.setIcontractstatus(Consts.ContractStatus.OK.val());

			c.setIdeliverystatus(Consts.DeliveryStatus.gFINISH.val());
			c.setIprogress(Consts.ContractProgress.gFinish.val());
			c.setIpaymentstatus(Consts.PaymentStatus.B_BaidBuy.val());
			//c.setSremark(T.now()+"="+excelfilename+"="+i);
			this.contractDao.save(c);


			Categorycontent ccs=categorycontentService.findByContentScategoryId(c.getIcontracttype(),commoditys.getScategoryid());
			if(ccs != null){
				Map<String, Object> _maparr = new HashMap<String, Object>();
				_maparr.put("contract", c);
				String content = FreemarkUtils.render(ccs.getBcontent(), _maparr);
				c.setSterms(content);
			}
			this.contractDao.save(c);

			if(F.compareMoney(fprice,0)<=0)
				E.S("第" + ((Integer)curmap.get("row") + 1) + "行价格必须大于0");

			if(F.compareWeight(fweight,0)<=0)
				E.S("第" + ((Integer)curmap.get("row") + 1) + "行重量必须大于0");


			//保存发货单信息
			SendOrder so=new SendOrder();
			so.setId(null);
			so.setSbuyeroperatorid(c.getSbuyeruserid());
			so.setSbuyermemberid(c.getSbuyermemberid());
			so.setSselleroperatorid(c.getSselleruserid());
			so.setSsellermemberid(c.getSsellermemberid());
			so.setScontractid(c.getId());
			so.setSorderno("UPSO"+DateUtils.dateToString(dorderdate,"yyMMdd")+StringHelper.randomNum(10));
			so.setSwarehouseid(c.getSwarehouseid());
			so.setDadddate(dorderdate);
			so.setDorderdate(dorderdate);
			so.setIstatus(Consts.SendOrderStatus.Accepted.val());
			so.setIprintcount(0l);
			so.setBisfinish(Consts.BoolType.NO.val());
			so.setBisdelete(Consts.BoolType.NO.val());
			so.setSaddoperator("importer");
			so.setSmodifyoperator("importer");
			so.setDmodifydate(dacceptdate);
			so.setDacceptdate(dacceptdate);
			so.setBisfinish(Consts.BoolType.YES.val());
			sendOrderDao.save(so);

			if(B.Y(list)){
				list.add(curmap);
			}
			c.setFtotalweight(0D);
			c.setFdeliveryweight(0D);
			c.setFopenbillweight(0D);
			for(Map<String,Object> obj:list){
				fprice=(Double) obj.get(Colums.ht_contractdetail.fprice);
				fweight=(Double) obj.get(Colums.ht_contractdetail.fweight);
				commoditys = ((Commodity)obj.get("commodity"));

				ContractDetail d = new ContractDetail();
				d.setId(null);

				d.setBisprotocal(Consts.BoolType.NO.val());
				d.setDadddate(T.now());
				d.setDproductiondate(T.now());
				d.setFaddprice(0D);
				d.setFnumberweight(fweight);
				d.setFprice(fprice);
				d.setFsaleprice(fprice);
				d.setFweight(fweight);
				d.setFoutprice(fprice);
				d.setFoutamount(F.multiply(fprice,fweight));
				d.setFoutweight(fweight);
				d.setFacceptweight(fweight);
				d.setForderweight(fweight);
				d.setInumber(1l);
				d.setIordernumber(1L);
				d.setIoutnumber(1L);
				d.setSweightunit(commoditys.getSweightunit());
				d.setSbalecode("");
				d.setSbigcategoryid(commoditys.getCategory().getSparentid());
				d.setSbigcategoryname("");
				d.setSbigcategorysystemcode("");
				d.setSbigpic("");
				d.setSbrand((String) obj.get(Colums.ht_contractdetail.sbrand));
				d.setScategory(commoditys.getCategory().getSname());
				d.setScategoryid(commoditys.getScategoryid());
				d.setScategorysystemcode(commoditys.getCategory().getSsystemcode());
				d.setScommodityid(commoditys.getId());
				d.setScommodityname(commoditys.getSname());
				d.setScommoditysystemcode(commoditys.getScommoditysystemcode());
				d.setScontractid(c.getId());
				d.setSfriendno("");
				d.setSmaterial((String) obj.get(Colums.ht_contractdetail.smaterial));
				d.setSmeasurement("");
				d.setSspec((String) obj.get(Colums.ht_contractdetail.sspec));
				d.setSproducer((String) obj.get(Colums.ht_contractdetail.sproducer));
				d.setSresourceid(res.getId());
				d.setSresourceno(res.getSresourceno());
				d.setStinypic("");
				d.setSwarehouse(warehouse.getSwarehousename());
				d.setSwarehouseid(warehouse.getId());
				d.setSwoodlevel("");
				d.setSstockid(stock.getId());
				this.contractDetailDao.save(d);

				SendOrderDetail sod=new SendOrderDetail();
				sod.setScontractid(c.getId());
				sod.setSorderid(so.getId());
				sod.setScontractdetailid(d.getId());
				sod.setIorderpackage(1l);
				sod.setIoutpackage(1l);
				sod.setBisfinish(Consts.BoolType.YES.val());
				sod.setFoderweight(fweight);
				sod.setFoutweight(fweight);

				sendOrderDetailDao.save(sod);

				c.setFdeliveryweight(F.add(c.getFdeliveryweight(),fweight));
				c.setFtotalweight(F.add(c.getFtotalweight(),fweight));
				c.setFopenbillweight(F.add(c.getFopenbillweight(),fweight));

			}

			this.contractDao.save(c);


			if(B.N((String) curmap.get(Colums.ht_contract.sinvalidatereason))){
				String filename=(String) curmap.get(Colums.ht_contract.sinvalidatereason+"CN");
				billFileService.addFile("/ExcelImport/invoice/"+(String) curmap.get(Colums.ht_contract.sinvalidatereason),
						B.Y(filename)?(String) curmap.get(Colums.ht_contract.sinvalidatereason):filename,
						Consts.BillFileType.OpenInvoice.val(), c.getId(), "",
						c.getSbuyermemberid(), "发票","",Lists.newArrayList(c.getId()),c.getDcontractdate());
			}
			if(B.N((String) curmap.get(Colums.ht_contract.sterms))){
				String filename=(String) curmap.get(Colums.ht_contract.sterms+"CN");
				billFileService.addFile("/ExcelImport/contract/"+(String) curmap.get(Colums.ht_contract.sterms),
						B.Y(filename)?(String) curmap.get(Colums.ht_contract.sterms):filename,
						Consts.BillFileType.Contract.val(), c.getId(), "",
						c.getSsellermemberid(), "合同","",Lists.newArrayList(c.getId()),c.getDcontractdate());
			}

			//生成支付单
			BillPay obj=new BillPay();
			obj.setId(null);
			obj.setSpayno("UPBP"+DateUtils.dateToString(dpaydate,"yyMMdd")+StringHelper.randomNum(10));
			obj.setIbanktype(c.getIbanktype());
			obj.setIacctpaytype(Consts.AcctcPayType.OFFLINE.val());
			obj.setSbillid(c.getId());
			obj.setSbillno(c.getScontractno());
			obj.setIpaytype(Consts.BillPaySrcType.Contract.val());
			obj.setIpaystatus(Consts.BillPayStatus.Over.val());
			obj.setItradestatus(Consts.PayState.PAID.val());
			obj.setSmarketserailno("");
			obj.setSbankserialno("");
			obj.setDdate(dpaydate);
			obj.setDpaytime(dpaydate);
			obj.setDpayovertime(dpaydate);
			obj.setFamount(c.getFtotalamount());
			obj.setSpayfundid("");
			obj.setSpaymemberid(c.getSbuyermemberid());
			obj.setSreceivefundid("");
			obj.setSreceivememberid(c.getSsellermemberid());
			obj.setIsendpoint(Consts.PayStartPoint.Pay.val());
			obj.setIcanreject(Consts.BoolType.YES.val());
			obj.setSremark("支付合同款");
			obj.setIcanceltype(Consts.BillPayCancelType.No.val());
			obj.setSpaybiztype(AsyncPayFundType.LongContractAccept.val());
			obj.setIversion(0L);

			this.billPayDao.save(obj);
		}
	}




	private String getCellValue(Map<String,Integer> colmap,Row crow,String celName){
		if(!colmap.containsKey(celName))
			return "";
		if(null == crow.getCell(colmap.get(celName)))
			return "";
		return getCellFormatValue(crow.getCell(colmap.get(celName)));
	}

	/**
	 * 根据HSSFCell类型设置数据
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellFormatValue(Cell cell) {
		String cellvalue;
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			// 如果当前Cell的Type为NUMERIC
			case HSSFCell.CELL_TYPE_NUMERIC:
			case HSSFCell.CELL_TYPE_FORMULA:
				// 判断当前的cell是否为Date
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// 如果是Date类型则，转化为Data格式

					// 方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();

					// 方法2：这样子的data格式是不带带时分秒的：2011-10-12
					Date date = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					cellvalue = sdf.format(date);

				} else {// 如果是纯数字
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

	public static String hashStock(Stock stock) {
		return MD5.encode(stock.getSmemberid() + stock.getSproducer() + stock.getScommodityname() + stock.getSmaterial()
				+ stock.getSspec() + stock.getSwarehouse());
	}


	public void updateContract(UpdateVo vo){
		Contract contract=this.contractDao.findByScontractno(vo.getScontractno());
		if (contract == null)
			E.S("编号为【"+vo.getScontractno()+"】的合同不存在");
		if(vo.getDcontractdate() != null){
			contract.setDcontractdate(vo.getDcontractdate());
			contract.setDdeliverydate(DateUtils.addDays(vo.getDcontractdate(),7));
		}

		if(vo.getDinvoicedate() != null){
			contract.setDmakeinvoicedate(vo.getDinvoicedate());
		}
		if(vo.getDacceptinvoicedate() != null){
			contract.setDrsvinvoicedate(vo.getDacceptinvoicedate());
		}

		if(vo.getDgpdate() != null){
			List<ContractDetail> details=this.contractDetailDao.findByScontractid(contract.getId());
			List<String> resids=Lists.newArrayList();
			List<String> stockids=Lists.newArrayList();
			for(ContractDetail del:details){
				resids.add(del.getSresourceid());
				stockids.add(del.getSstockid());
			}
			List<SaleResource> resources = this.saleResourceDao.findByIdIn(resids.toArray(new String[]{}));
			for(SaleResource resource:resources){
				resource.setDbegindate(vo.getDgpdate());
				resource.setDadddate(vo.getDgpdate());
				this.saleResourceDao.save(resource);

				Stock stock=this.stockDao.findBySresourceid(resource.getId());
				if(stock != null){
					stock.setDadddate(vo.getDgpdate());
					stock.setDmodifydate(vo.getDgpdate());
					this.stockDao.save(stock);
				}
			}

		}

		SendOrder order = this.sendOrderDao.findByContractId(contract.getId());
		if(vo.getDsenddate() != null){
			order.setDadddate(vo.getDsenddate());
			order.setDorderdate(vo.getDsenddate());
		}
		if(vo.getDacceptdate() != null){
			order.setDacceptdate(vo.getDacceptdate());
		}

		if(vo.getDpaydate() != null){
			final String htid=contract.getId();
			List<BillPay> billPays=this.billPayDao.findAll(new Specification<BillPay>() {
				public Predicate toPredicate(Root<BillPay> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					return cb.equal(Colums.zj_billpay.sbillid(root),htid);
				}
			});
			if(B.N(billPays)){
				for(BillPay pay:billPays){
					pay.setDpaytime(vo.getDpaydate());
					pay.setDpayovertime(vo.getDpaydate());
					pay.setDdate(vo.getDpaydate());
					this.billPayDao.save(pay);
				}
			}
		}

		String contractfile="";
		for(CommonsMultipartFile file:vo.getScontractfile()){
			if(file == null || file.getSize() <= 0)
				continue;
			contractfile = FtpUtils.uploadToFtpServer(file,"/ExcelImport/contract/");
			billFileService.addFile(contractfile, file.getOriginalFilename(),
					Consts.BillFileType.Contract.val(), contract.getId(), "",
					contract.getSbuyermemberid(), "合同","",Lists.newArrayList(contract.getId()),
					contract.getDcontractdate());
		}

		String sendgoodsfile="";
		for(CommonsMultipartFile file:vo.getSendgoodfile()){
			if(file == null || file.getSize() <= 0)
				continue;
			sendgoodsfile = FtpUtils.uploadToFtpServer(file,"/ExcelImport/sendgoods/");
			billFileService.addFile(sendgoodsfile, file.getOriginalFilename(),
					Consts.BillFileType.Send.val(), order.getId(), "",
					order.getSbuyermemberid(), "合同发货","",Lists.newArrayList(contract.getId(),order.getId()),
					order.getDorderdate());
		}

		String acceptgoodsfile="";
		for(CommonsMultipartFile file:vo.getAcceptgoodfile()){
			if(file == null || file.getSize() <= 0)
				continue;
			acceptgoodsfile = FtpUtils.uploadToFtpServer(file,"/ExcelImport/acceptgoods/");
			billFileService.addFile(acceptgoodsfile, file.getOriginalFilename(),
					Consts.BillFileType.Receive.val(), order.getId(), "",
					order.getSbuyermemberid(), "合同收货","",Lists.newArrayList(contract.getId(),order.getId()),
					order.getDacceptdate());
		}

		String invoicefile="";
		for(CommonsMultipartFile file:vo.getInvoicefile()){
			if(file == null || file.getSize() <= 0)
				continue;
			invoicefile = FtpUtils.uploadToFtpServer(file,"/ExcelImport/invoice/");
			billFileService.addFile(invoicefile, file.getOriginalFilename(),
					Consts.BillFileType.OpenInvoice.val(), contract.getId(), "",
					contract.getSsellermemberid(), "合同开票","",Lists.newArrayList(contract.getId()),
					contract.getDmakeinvoicedate());
		}

		final String htid=contract.getId();
		List<BillPay> billPays=this.billPayDao.findAll(new Specification<BillPay>() {
			public Predicate toPredicate(Root<BillPay> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(Colums.zj_billpay.sbillid(root),htid);
			}
		});
		if(B.N(billPays)){
			String payfile="";
			for(CommonsMultipartFile file:vo.getInvoicefile()){
				if(file == null || file.getSize() <= 0)
					continue;
				payfile = FtpUtils.uploadToFtpServer(file,"/ExcelImport/billpay/");
				billFileService.addFile(payfile, file.getOriginalFilename(),
						Consts.BillFileType.BillPay.val(), billPays.get(0).getId(), "",
						billPays.get(0).getSpaymemberid(), "合同支付","",Lists.newArrayList(contract.getId(),billPays.get(0).getId()),
						billPays.get(0).getDpaytime());
			}
		}


	}

	public void deleteContract(UpdateVo vo){

		Contract contract=this.contractDao.findByScontractno(vo.getScontractno());
		if (contract == null)
			E.S("编号为【"+vo.getScontractno()+"】的合同不存在");
		if(!contract.getScontractno().startsWith("UP"))
			E.S("此合同不是导入的合同，不能删除数据");
		this.billFileService.deleteByBillId(contract.getId());


		//删除发货信息
		//删除发货明细
		SendOrder order = this.sendOrderDao.findByContractId(contract.getId());
		if(order != null){
			this.billFileService.deleteByBillId(order.getId());
			List<SendOrderDetail> sendOrderDetails=this.sendOrderDetailDao.findBySorderid(order.getId());
			if(B.N(sendOrderDetails)){
				this.sendOrderDetailDao.delete(sendOrderDetails);
			}
		}

		//删除合同明细
		List<ContractDetail> details=this.contractDetailDao.findByScontractid(contract.getId());
		if(B.N(details)){
			for(ContractDetail detail:details){
				//删除挂牌资源
				this.stockDao.delete(detail.getSstockid());
				this.saleResourceDao.delete(detail.getSresourceid());
			}
			this.contractDetailDao.delete(details);
		}

		//删除支付信息
		final String htid=contract.getId();
		List<BillPay> billPays=this.billPayDao.findAll(new Specification<BillPay>() {
			public Predicate toPredicate(Root<BillPay> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(Colums.zj_billpay.sbillid(root),htid);
			}
		});
		if(B.N(billPays)){
			this.billPayDao.delete(billPays);
		}

		this.contractDao.delete(contract);
	}



	public static void main(String[] args) {
		//System.out.println("UPHT"+DateUtils.dateToString("","yyMMdd")+StringHelper.randomNum(10););
	}

}
