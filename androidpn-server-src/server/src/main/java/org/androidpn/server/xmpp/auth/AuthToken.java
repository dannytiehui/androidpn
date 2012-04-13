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
package org.androidpn.server.xmpp.auth;

import org.androidpn.server.util.Config;

/** 
 * This class represents a token that proves a user's authentication.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class AuthToken {

    private String username;

    private String domain;

    /**
     * Constucts a new AuthToken with the specified JID.
     * 
     * @param jid the username or bare JID
     */
    public AuthToken(String jid) {
        if (jid == null) {
            this.domain = Config.getString("xmpp.domain");
            return;
        }
        int index = jid.indexOf("@");
        if (index > -1) {
            this.username = jid.substring(0, index);
            this.domain = jid.substring(index + 1);
        } else {
            this.username = jid;
            this.domain = Config.getString("xmpp.domain");
        }
    }

    /**
     * Returns the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the domain.
     * 
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

}