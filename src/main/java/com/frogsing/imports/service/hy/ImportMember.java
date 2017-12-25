package com.frogsing.imports.service.hy;/**
 * Created by wesson on 2017/10/11.
 */

import com.frogsing.dao.hy.MemberDao;
import com.frogsing.heart.jpa.BaseDao;
import com.frogsing.heart.jpa.BaseService;
import com.frogsing.po.entity.hy.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * Description:
 * <p>
 * Created by wesson on 2017/10/11.
 **/
@Component
@Transactional
public class ImportMember extends BaseService<Member> {

    @Autowired
    private MemberDao memberDao;

    @Override
    protected BaseDao<Member, String> getBaseDao() {
        return memberDao;
    }

    @Override
    protected void BaseSaveCheck(Member member) {

    }




}
