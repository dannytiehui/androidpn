/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.console.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.androidpn.server.model.User;
import org.androidpn.server.service.ServiceLocator;
import org.androidpn.server.service.UserService;
import org.androidpn.server.xmpp.presence.PresenceManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/** 
 * A controller class to process the user related requests.  
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class UserController extends MultiActionController {

    private UserService userService;

    public UserController() {
        userService = ServiceLocator.getUserService();
    }

    public ModelAndView list(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PresenceManager presenceManager = new PresenceManager();
        List<User> userList = userService.getUsers();
        for (User user : userList) {
            if (presenceManager.isAvailable(user)) {
                // Presence presence = presenceManager.getPresence(user);
                user.setOnline(true);
            } else {
                user.setOnline(false);
            }
            // logger.debug("user.online=" + user.isOnline());
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("userList", userList);
        mav.setViewName("user/list");
        return mav;
    }

}
