package com.frogsing.imports.vo;/**
 * Created by wesson on 2017/10/27.
 */

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.Date;

/**
 * Description:
 * <p>
 * Created by wesson on 2017/10/27.
 **/
public class UpdateVo {

    private String scontractno;
    private Date dcontractdate;
    private Date dsenddate;
    private Date dacceptdate;
    private Date dinvoicedate;
    private Date dacceptinvoicedate;
    private Date dgpdate;
    private Date dpaydate;

    private CommonsMultipartFile[] scontractfile;
    private CommonsMultipartFile[] sendgoodfile;
    private CommonsMultipartFile[] acceptgoodfile;
    private CommonsMultipartFile[] invoicefile;
    private CommonsMultipartFile[] payfile;


    public String getScontractno() {
        return scontractno;
    }

    public void setScontractno(String scontractno) {
        this.scontractno = scontractno;
    }

    public Date getDcontractdate() {
        return dcontractdate;
    }

    public void setDcontractdate(Date dcontractdate) {
        this.dcontractdate = dcontractdate;
    }

    public Date getDsenddate() {
        return dsenddate;
    }

    public void setDsenddate(Date dsenddate) {
        this.dsenddate = dsenddate;
    }

    public Date getDacceptdate() {
        return dacceptdate;
    }

    public void setDacceptdate(Date dacceptdate) {
        this.dacceptdate = dacceptdate;
    }

    public Date getDinvoicedate() {
        return dinvoicedate;
    }

    public void setDinvoicedate(Date dinvoicedate) {
        this.dinvoicedate = dinvoicedate;
    }

    public Date getDacceptinvoicedate() {
        return dacceptinvoicedate;
    }

    public void setDacceptinvoicedate(Date dacceptinvoicedate) {
        this.dacceptinvoicedate = dacceptinvoicedate;
    }

    public Date getDgpdate() {
        return dgpdate;
    }

    public void setDgpdate(Date dgpdate) {
        this.dgpdate = dgpdate;
    }

    public CommonsMultipartFile[] getScontractfile() {
        return scontractfile;
    }

    public void setScontractfile(CommonsMultipartFile[] scontractfile) {
        this.scontractfile = scontractfile;
    }

    public CommonsMultipartFile[] getSendgoodfile() {
        return sendgoodfile;
    }

    public void setSendgoodfile(CommonsMultipartFile[] sendgoodfile) {
        this.sendgoodfile = sendgoodfile;
    }

    public CommonsMultipartFile[] getAcceptgoodfile() {
        return acceptgoodfile;
    }

    public void setAcceptgoodfile(CommonsMultipartFile[] acceptgoodfile) {
        this.acceptgoodfile = acceptgoodfile;
    }

    public CommonsMultipartFile[] getInvoicefile() {
        return invoicefile;
    }

    public void setInvoicefile(CommonsMultipartFile[] invoicefile) {
        this.invoicefile = invoicefile;
    }

    public Date getDpaydate() {
        return dpaydate;
    }

    public void setDpaydate(Date dpaydate) {
        this.dpaydate = dpaydate;
    }

    public CommonsMultipartFile[] getPayfile() {
        return payfile;
    }

    public void setPayfile(CommonsMultipartFile[] payfile) {
        this.payfile = payfile;
    }
}
