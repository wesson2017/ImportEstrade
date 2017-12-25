-- delete ht_contract where scontractno like 'UPHT%'; 
select t.*,rowid from ht_contract t where t.scontractno like 'UPHT%'; 

--delete ht_contractdetail where scontractid in (select sguid from ht_contract where scontractno like 'UPHT%');
select t.*,rowid from ht_contractdetail t where t.scontractid in (select sguid from ht_contract where scontractno like 'UPHT%');

--delete ht_sendorder where sorderno like 'UPSO%' ;
select t.*,rowid from ht_sendorder t where t.sorderno like 'UPSO%' ;

--delete ht_sendorderdetail where scontractid in (select sguid from ht_contract where scontractno like 'UPHT%');
select t.*,rowid from ht_sendorderdetail t where t.scontractid in (select sguid from ht_contract where scontractno like 'UPHT%'); 

--delete ht_billfile where sbillid in (select sguid from ht_contract where scontractno like 'UPHT%'); 
select t.*,rowid from ht_billfile t where t.sbillid in (select sguid from ht_contract where scontractno like 'UPHT%'); 
