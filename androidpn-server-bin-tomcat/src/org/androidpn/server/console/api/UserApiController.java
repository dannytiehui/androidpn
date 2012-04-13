//
//  UserApiController.java
//  FeOA
//
//  Created by LuTH on 2012-3-31.
//  Copyright 2012 flyrise. All rights reserved.
//

package org.androidpn.server.console.api;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.presence.PresenceManager;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class UserApiController extends MultiActionController {

	private UserService userService;

	public UserApiController() {
		userService = ServiceLocator.getUserService();
	}

	public void list(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		PresenceManager presenceManager = new PresenceManager();
		List<User> userList;
		Date createDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			String from = request.getParameter("from").toString();
			if (!"".equals(from)) {
				createDate = sdf.parse(from);
				userList = userService.getUsersFromCreatedDate(createDate);
			} else {
				userList = userService.getUsers();
			}
		} catch (NullPointerException e) {
			userList = userService.getUsers();
		}

		String online = ServletRequestUtils.getStringParameter(request,
				"online", null);

		ArrayList<String> out = new ArrayList<String>();
		for (User user : userList) {

			if ("1".equals(online) && presenceManager.isAvailable(user)) {
				// Presence presence = presenceManager.getPresence(user);
				user.setOnline(true);
				out.add("\"" + user.getUsername() + "\"");
			} else if ("0".equals(online) && !presenceManager.isAvailable(user)) {
				user.setOnline(false);
				out.add("\"" + user.getUsername() + "\"");
			} else if (online == null) {
				if (presenceManager.isAvailable(user)) {
					// Presence presence = presenceManager.getPresence(user);
					user.setOnline(true);
				} else {
					user.setOnline(false);
				}
				out.add("\"" + user.getUsername() + "\"");
			}
			// logger.debug("user.online=" + user.isOnline());
		}
		response.getWriter().print("{\"data\":" + out.toString() + "}");
	}

}
