package com.frogsing.imports.service.lg;

import com.frogsing.heart.security.shiro.ILoginService;
import com.frogsing.heart.security.shiro.ILoginUser;
import com.frogsing.heart.security.shiro.ShiroUsernamePasswordToken;
import com.frogsing.heart.security.shiro.ShiroUsernamePasswordToken.UserType;
import com.frogsing.heart.security.shiro.ShiroUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Collection;

/**
 * 用户管理类.
 * 
 * @frogsing van
 */
// Spring Service Bean的标识.
@Component
@Transactional
public class LoginProxy implements ILoginService {

	@Autowired
	private UserLoginService userLoginService;

	/**
	 * 获取用户信息
	 */
	public ILoginUser getIUser(ShiroUsernamePasswordToken token) {
		UserType type = token.getUsertype();
		switch (type) {
		case user:
			return userLoginService.getIUser(token);
		case open:
			return userLoginService.getIUser(token);
		default:
			break;
		}

		return null;
	}

	/*
	 * 获取用户权限
	 */
	@Transactional(readOnly = true)
	public Collection<String> getUserPurview(ILoginUser user) {
		UserType type = user.getUsertype();
		switch (type) {
		case user:
			return userLoginService.getUserPurview(user);
		case open:
			return userLoginService.getUserPurview(user);
		default:
			break;
		}
		return null;

	}

	public void DoLoginOk(ILoginUser user) {
		UserType type = user.getUsertype();
		switch (type) {
		case user:
			userLoginService.DoLoginOk(user);
		default:
			break;
		}
	}

	public Collection<String> getUserRole(ILoginUser user) {
		UserType type = user.getUsertype();
		switch (type) {
		case user:
			return userLoginService.getUserRole(user);
		case open:
			return userLoginService.getUserRole(user);
		default:
			break;
		}
		return null;

	}

	public void doLoginIntegral(ILoginUser user) {

	}

	public String getRedirectUrl(ServletRequest request,
			ServletResponse response, Subject subject) {
		UserType type = ShiroUtils.getUserType(request);
		if (type == null) {
			ILoginUser user = (ILoginUser) subject.getPrincipal();
			if (user != null)
				type = user.getUsertype();
		}
		if (type == null)
			return "";
		switch (type) {
		case user:
			return userLoginService.getRedirectUrl(request, response, subject);
		default:
			break;
		}
		return null;
	}

	public String getSuccessUrl(ServletRequest request,
			ServletResponse response) {
		
		ILoginUser user = ShiroUtils.getCurrentUser();
		UserType type = user.getUsertype();
		switch (type) {
		case user:
			return userLoginService.getSuccessUrl( request,
					 response);
		default:
			break;
		}
		return null;
	}

}
