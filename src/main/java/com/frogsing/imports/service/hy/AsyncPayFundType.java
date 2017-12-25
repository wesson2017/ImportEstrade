package com.frogsing.imports.service.hy;/**
 * Created by wesson on 2017/10/27.
 */

/**
 * Description:
 * <p>
 * Created by wesson on 2017/10/27.
 **/
public enum AsyncPayFundType {
    Contract_Pay_Margin("Contract_Pay_Margin","合同支付预付金"),
    Contract_Pay_Amount("Contract_Pay_Amount","合同支付货款"),
    Contract_Pay_ServiceFee("Contract_Pay_ServiceFee","合同支付服务费"),
    Contract_Pay_ReturnFund("Contract_Pay_ReturnFund","合同退款"),
    Contract_Pay_AddingPay("Contract_Pay_AddingPay","合同补款"),
    Contract_Pay_LadingBack("Contract_Pay_LadingBack","合同还款提货"),
    Tender_Investor("Tender_Investor","投资付款"),
    Tender_Borrower_ServiceFee("Tender_Borrower_ServiceFee","支付借款服务费"),
    Borrower_ReturnInvestor("Borrower_ReturnInvestor","还款付息"),
    Member_FeePay("Member_FeePay","会员费用支付"),
    BillPay("BillPay","支付单支付"),
    TimeSettle("TimeSettle","结算单支付"),
    LongContractAccept("LongContractAccept","长约收款"),
    LongContractPay("LongContractPay","长约付款"),
    Tender_Borrower_VouchFee("Tender_Borrower_VouchFee","支付借款担保费");

    private String code;
    private String label;
    private AsyncPayFundType(String code,String label){
        this.code=code;
        this.label=label;
    }
    public String val(){
        return code;
    }
    public String label(){
        return label;
    }
}
