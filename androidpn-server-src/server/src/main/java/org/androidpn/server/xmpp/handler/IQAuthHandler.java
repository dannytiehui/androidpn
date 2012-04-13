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

import gnu.inet.encoding.Stringprep;
import gnu.inet.encoding.StringprepException;

import org.androidpn.server.xmpp.UnauthenticatedException;
import org.androidpn.server.xmpp.UnauthorizedException;
import org.androidpn.server.xmpp.auth.AuthManager;
import org.androidpn.server.xmpp.auth.AuthToken;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

/** 
 * This class is to handle the TYPE_IQ jabber:iq:auth protocol.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class IQAuthHandler extends IQHandler {

    private static final String NAMESPACE = "jabber:iq:auth";

    private Element probeResponse;

    /**
     * Constructor.
     */
    public IQAuthHandler() {
        probeResponse = DocumentHelper.createElement(QName.get("query",
                NAMESPACE));
        probeResponse.addElement("username");
        if (AuthManager.isPlainSupported()) {
            probeResponse.addElement("password");
        }
        if (AuthManager.isDigestSupported()) {
            probeResponse.addElement("digest");
        }
        probeResponse.addElement("resource");
    }

    /**
     * Handles the received IQ packet.
     * 
     * @param packet the packet
     * @return the response to send back
     * @throws UnauthorizedException if the user is not authorized
     */
    public IQ handleIQ(IQ packet) throws UnauthorizedException {
        IQ reply = null;

        ClientSession session = sessionManager.getSession(packet.getFrom());
        if (session == null) {
            log.error("Session not found for key " + packet.getFrom());
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            reply.setError(PacketError.Condition.internal_server_error);
            return reply;
        }

        try {
            Element iq = packet.getElement();
            Element query = iq.element("query");
            Element queryResponse = probeResponse.createCopy();

            if (IQ.Type.get == packet.getType()) { // get query
                String username = query.elementText("username");
                if (username != null) {
                    queryResponse.element("username").setText(username);
                }
                reply = IQ.createResultIQ(packet);
                reply.setChildElement(queryResponse);
                if (session.getStatus() != Session.STATUS_AUTHENTICATED) {
                    reply.setTo((JID) null);
                }
            } else { // set query
                String resource = query.elementText("resource");
                String username = query.elementText("username");
                String password = query.elementText("password");
                String digest = null;
                if (query.element("digest") != null) {
                    digest = query.elementText("digest").toLowerCase();
                }

                // Verify the resource
                if (resource != null) {
                    try {
                        resource = JID.resourceprep(resource);
                    } catch (StringprepException e) {
                        throw new UnauthorizedException("Invalid resource: "
                                + resource, e);
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Invalid resource (empty or null).");
                }

                // Verify the username
                if (username == null || username.trim().length() == 0) {
                    throw new UnauthorizedException(
                            "Invalid username (empty or null).");
                }
                try {
                    Stringprep.nodeprep(username);
                } catch (StringprepException e) {
                    throw new UnauthorizedException("Invalid username: "
                            + username, e);
                }
                username = username.toLowerCase();

                // Verify that username and password are correct
                AuthToken token = null;
                if (password != null && AuthManager.isPlainSupported()) {
                    token = AuthManager.authenticate(username, password);
                } else if (digest != null && AuthManager.isDigestSupported()) {
                    token = AuthManager.authenticate(username, session
                            .getStreamID().toString(), digest);
                }

                if (token == null) {
                    throw new UnauthenticatedException();
                }

                // Set the session authenticated successfully
                session.setAuthToken(token, resource);
                packet.setFrom(session.getAddress());
                reply = IQ.createResultIQ(packet);
            }
        } catch (Exception ex) {
            log.error(ex);
            reply = IQ.createResultIQ(packet);
            reply.setChildElement(packet.getChildElement().createCopy());
            if (ex instanceof IllegalArgumentException) {
                reply.setError(PacketError.Condition.not_acceptable);
            } else if (ex instanceof UnauthorizedException) {
                reply.setError(PacketError.Condition.not_authorized);
            } else if (ex instanceof UnauthenticatedException) {
                reply.setError(PacketError.Condition.not_authorized);
            } else {
                reply.setError(PacketError.Condition.internal_server_error);
            }
        }

        // Send the response directly to the session
        if (reply != null) {
            session.process(reply);
        }
        return null;
    }

    /**
     * Returns the namespace of the handler.
     * 
     * @return the namespace
     */
    public String getNamespace() {
        return NAMESPACE;
    }

}
