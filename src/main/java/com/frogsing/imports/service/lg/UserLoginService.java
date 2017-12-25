package com.frogsing.imports.service.lg;

import com.frogsing.dao.hy.MemberDao;
import com.frogsing.dao.hy.SinglepointloginDao;
import com.frogsing.dao.hy.UserDao;
import com.frogsing.heart.security.shiro.ILoginService;
import com.frogsing.heart.security.shiro.ILoginUser;
import com.frogsing.heart.security.shiro.ShiroUsernamePasswordToken;
import com.frogsing.heart.security.shiro.ShiroUtils;
import com.frogsing.heart.utils.*;
import com.frogsing.po.entity.hy.Member;
import com.frogsing.po.entity.hy.Singlepointlogin;
import com.frogsing.po.entity.hy.User;
import com.frogsing.po.utils.Consts.AuthenticateType;
import com.frogsing.po.utils.Consts.BoolType;
import com.frogsing.po.utils.Consts.MemberType;
import com.google.common.collect.Sets;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;

/**
 * 用户管理类.
 * 
 * @frogsing van
 */
// Spring Service Bean的标识.
@Component
@Transactional
public class UserLoginService implements ILoginService {

	private static Logger logger = LoggerFactory.getLogger(UserLoginService.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private SinglepointloginDao singlepointloginDao;
	@Autowired
	private MemberDao memberDao;

	/**
	 * 获取用户信息
	 */
	@Override
	public ILoginUser getIUser(ShiroUsernamePasswordToken token) {
		User user = userDao.findBySusernameOrSmobile(token.getUsername(), token.getUsername());

		if (user == null)
			return null;
		if (user.getBisdelete() == BoolType.YES.val())
			return null;
		Member member = memberDao.findOne(user.getSmemberid());

		if (member == null)
			return null;
		LoginUser u = new LoginUser(user.getId(), user.getSusername(), B.Y(user.getSname())?"":user.getSname(),
				token.prePassword(user.getSpassword()), member.getImembertype(), user.getBisadmin(),
				user.getDlastloginsuccessdate(), user.getSmemberid(),
				member.getScnname() == null ? "" : member.getScnname(), member.getSmemberno(), member.getIauthtype(),
				member.getBisseller(), B.Y(user.getSlikename())?"":user.getSlikename(), user.getSmobile(), member.getBisselfsaler());
		u.setImembersitetype(member.getIsite());
		return u;
	}

	/*
	 * 获取用户权限
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<String> getUserPurview(ILoginUser user) {

		LoginUser u = (LoginUser) user;

		Collection<String> db;
		Collection<String> rs = Sets.newHashSet();
		if (user.IsAdmin()) {
			if (u.getBissalesman() == BoolType.YES.val()) {
				db = userDao.getAllPurview(u.getMembertype(), u.getAuthtype(), 1, u.getMemberId());
			} else {
				db = userDao.getAllPurview(u.getMembertype(), u.getAuthtype(), 0, u.getMemberId());
			}
			Collection<String> special = userDao.getAllSpecialPurview(u.getMembertype(), u.getMemberId());
			if (special != null)
				db.addAll(special);
		} else {
			db = userDao.getAllPurview(user.getId());
		}
		for (String t : db) {
			String[] as = t.split(",");
			for (String b : as) {
				rs.add(b.trim());
			}
		}
		logger.info("所有权限:" + rs.toString());
		return rs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.frogsing.service.sys.ILoginService#DoLoginOk(java.lang.String)
	 */
	@Override
	public void DoLoginOk(ILoginUser user) {
		userDao.updateLoginTime(user.getId(), DateUtils.getCurrentDateTime());
	}

	@Override
	public Collection<String> getUserRole(ILoginUser user) {
		// LoginUser u=(LoginUser)user;
		HashSet<String> map = Sets.newHashSet("user");

		if (user instanceof LoginUser) {
			LoginUser u = (LoginUser) user;
			map.add(AuthenticateType.get(u.getAuthtype()).name());
			if (u.getMembertype() == MemberType.EXCHANGE.val())
				map.add("selfseller");
		}
		return map;
	}

	@Override
	public void doLoginIntegral(ILoginUser user) {

	}

	@Override
	public String getRedirectUrl(ServletRequest request, ServletResponse response, Subject subject) {

		return "index.shtml";
	}

	private String newLoginPoint(String susername) {
		String key = StringHelper.randomNum(128);
		Singlepointlogin t = new Singlepointlogin();
		t.setDaddtime(T.now());
		t.setDvalidtime(DateUtils.addSeconds(T.now(), 120));
		t.setSusername(susername);
		t.setSkey(key);
		singlepointloginDao.save(t);
		singlepointloginDao.deleteInvalid(T.now());
		return key;
	}

	@Override
	public String getSuccessUrl(ServletRequest request, ServletResponse response) {
		HttpServletRequest r=(HttpServletRequest)request;
		LoginUser u = ShiroUtils.getCurrentUser();
		L.info(u.getMemberName(), u.getName(), "登录成功！");
		if(r.getRequestURI().endsWith(".phtml")){
			return "center.phtml";
		}
		return "index.shtml";
	}

}
