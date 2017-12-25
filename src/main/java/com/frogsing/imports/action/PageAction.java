package com.frogsing.imports.action;/**
 * Created by wesson on 2017/10/10.
 */

import com.frogsing.heart.exception.ServiceException;
import com.frogsing.heart.utils.B;
import com.frogsing.heart.web.Msg;
import com.frogsing.imports.service.hy.ContractExcelReader;
import com.frogsing.imports.service.hy.MemberExcelReader;
import com.frogsing.imports.vo.UpdateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description:
 * <p>
 * Created by wesson on 2017/10/10.
 **/
@Controller
@RequestMapping(value = "")
public class PageAction {
    @Autowired
    private MemberExcelReader memberExcelReader;
    @Autowired
    private ContractExcelReader contractExcelReader;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                dateFormat, true));
    }

    @RequestMapping(value = "index.html", method = RequestMethod.GET)
    public String index(Model model, HttpServletRequest request) {
        return "index";
    }

    @RequestMapping(value = "update.html", method = RequestMethod.GET)
    public String toupdate(Model model,HttpServletRequest request){
        return "update";
    }

    @RequestMapping(value = "delete.html", method = RequestMethod.GET)
    public String todelete(Model model,HttpServletRequest request){
        return "delete";
    }

    @RequestMapping(value = "doupdate.html")
    public String doupdate(
            UpdateVo updateVo,
            Model model,HttpServletRequest request){
        try {
            this.contractExcelReader.updateContract(updateVo);
            Msg.success(model,"修改成功");
        } catch (ServiceException e) {
            Msg.error(model,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Msg.error(model,"修改失败");
        }
        return "result";
    }

    @RequestMapping(value = "dodelete.html")
    public String dodelete(
            UpdateVo updateVo,
            Model model,HttpServletRequest request){
        try {
            this.contractExcelReader.deleteContract(updateVo);
            Msg.success(model,"删除成功");
        } catch (ServiceException e) {
            Msg.error(model,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Msg.error(model,"删除失败");
        }
        return "result";
    }

    @RequestMapping(value = "upload.html")
    public String uploadImage(
            @RequestParam(value="file",required = false) CommonsMultipartFile file,
            Model model, HttpServletResponse response) {
        try {
            InputStream excelStream = file.getInputStream();
            memberExcelReader.excelReader(excelStream,file.getOriginalFilename());
            Msg.success(model,"导入成功");
        } catch (ServiceException e) {
            Msg.error(model,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Msg.error(model,"导入失败");
        }
        return "result";
    }

    @RequestMapping(value = "contract.html")
    public String importcontract(
            @RequestParam(value="file",required = false) CommonsMultipartFile file,
            Model model, HttpServletResponse response) {
        try {
            InputStream excelStream = file.getInputStream();
            contractExcelReader.excelReader(excelStream,file.getOriginalFilename());
            Msg.success(model,"导入成功");
        } catch (ServiceException e) {
            Msg.error(model,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Msg.error(model,"导入失败");
        }
        return "result";
    }

}
