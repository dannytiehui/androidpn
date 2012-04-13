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
package org.androidpn.server.xmpp.router;

import org.androidpn.server.xmpp.PacketException;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.SessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

/** 
 * This class is to deliver the packets to the connected sessions. 
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class PacketDeliverer {

    private static final Log log = LogFactory.getLog(PacketDeliverer.class);

    /**
     * Delivers the packet to the packet recipient.  
     * 
     * @param packet the packet to deliver
     * @throws PacketException if the packet is null or the recipient was not found.
     */
    public static void deliver(Packet packet) throws PacketException {
        if (packet == null) {
            throw new PacketException("Packet was null");
        }

        try {
            JID recipient = packet.getTo();
            if (recipient != null) {
                ClientSession clientSession = SessionManager.getInstance()
                        .getSession(recipient);
                if (clientSession != null) {
                    clientSession.deliver(packet);
                }
            }
        } catch (Exception e) {
            log.error("Could not deliver packet: " + packet.toString(), e);
        }
    }
}
