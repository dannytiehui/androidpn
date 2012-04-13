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
package org.androidpn.server.xmpp.handler;

import org.androidpn.server.xmpp.router.PacketDeliverer;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;

/** 
 * This class is to handle the presence protocol.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class PresenceUpdateHandler {

    protected final Log log = LogFactory.getLog(getClass());

    protected SessionManager sessionManager;

    /**
     * Constructor.
     */
    public PresenceUpdateHandler() {
        sessionManager = SessionManager.getInstance();
    }

    /**
     * Processes the presence packet.
     * 
     * @param packet the packet
     */
    public void process(Packet packet) {
        ClientSession session = sessionManager.getSession(packet.getFrom());

        try {
            Presence presence = (Presence) packet;
            Presence.Type type = presence.getType();

            if (type == null) { // null == available
                if (session != null
                        && session.getStatus() == Session.STATUS_CLOSED) {
                    log.warn("Rejected available presence: " + presence + " - "
                            + session);
                    return;
                }

                if (session != null) {
                    session.setPresence(presence);
                    if (!session.isInitialized()) {
                        // initSession(session);
                        session.setInitialized(true);
                    }
                }

            } else if (Presence.Type.unavailable == type) {

                if (session != null) {
                    session.setPresence(presence);
                }

            } else {
                presence = presence.createCopy();
                if (session != null) {
                    presence.setFrom(new JID(null, session.getServerName(),
                            null, true));
                    presence.setTo(session.getAddress());
                } else {
                    JID sender = presence.getFrom();
                    presence.setFrom(presence.getTo());
                    presence.setTo(sender);
                }
                presence.setError(PacketError.Condition.bad_request);
                PacketDeliverer.deliver(presence);
            }

        } catch (Exception e) {
            log.error("Internal server error. Triggered by packet: " + packet,
                    e);
        }
    }

}
