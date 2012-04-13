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
package org.androidpn.server.xmpp.net;

import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

import org.androidpn.server.util.Config;
import org.androidpn.server.xmpp.router.PacketRouter;
import org.androidpn.server.xmpp.session.ClientSession;
import org.androidpn.server.xmpp.session.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.io.XMPPPacketReader;
import org.jivesoftware.openfire.net.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.Presence;
import org.xmpp.packet.Roster;
import org.xmpp.packet.StreamError;

/** 
 * This class is to handle incoming XML stanzas.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class StanzaHandler {

    private static final Log log = LogFactory.getLog(StanzaHandler.class);

    protected Connection connection;

    protected Session session;

    protected String serverName;

    private boolean sessionCreated = false;

    private boolean startedTLS = false;

    private PacketRouter router;

    /**
     * Constructor.
     * 
     * @param serverName the server name
     * @param connection the connection
     */
    public StanzaHandler(String serverName, Connection connection) {
        this.serverName = serverName;
        this.connection = connection;
        this.router = new PacketRouter();
    }

    /**
     * Process the received stanza using the given XMPP packet reader.
     *  
     * @param stanza the received statza
     * @param reader the XMPP packet reader
     * @throws Exception if the XML stream is not valid.
     */
    public void process(String stanza, XMPPPacketReader reader)
            throws Exception {
        boolean initialStream = stanza.startsWith("<stream:stream");
        if (!sessionCreated || initialStream) {
            if (!initialStream) {
                return; // Ignore <?xml version="1.0"?>
            }
            if (!sessionCreated) {
                sessionCreated = true;
                MXParser parser = reader.getXPPParser();
                parser.setInput(new StringReader(stanza));
                createSession(parser);
            } else if (startedTLS) {
                startedTLS = false;
                tlsNegotiated();
            }
            return;
        }

        // If end of stream was requested
        if (stanza.equals("</stream:stream>")) {
            session.close();
            return;
        }
        // Ignore <?xml version="1.0"?>
        if (stanza.startsWith("<?xml")) {
            return;
        }
        // Create DOM object
        Element doc = reader.read(new StringReader(stanza)).getRootElement();
        if (doc == null) {
            return;
        }

        String tag = doc.getName();
        if ("starttls".equals(tag)) {
            if (negotiateTLS()) { // Negotiate TLS
                startedTLS = true;
            } else {
                connection.close();
                session = null;
            }
        } else if ("message".equals(tag)) {
            processMessage(doc);
        } else if ("presence".equals(tag)) {
            log.debug("presence...");
            processPresence(doc);
        } else if ("iq".equals(tag)) {
            log.debug("iq...");
            processIQ(doc);
        } else {
            log.warn("Unexpected packet tag (not message, iq, presence)"
                    + doc.asXML());
            session.close();
        }

    }

    private void processMessage(Element doc) {
        log.debug("processMessage()...");
        Message packet;
        try {
            packet = new Message(doc, false);
        } catch (IllegalArgumentException e) {
            log.debug("Rejecting packet. JID malformed", e);
            Message reply = new Message();
            reply.setID(doc.attributeValue("id"));
            reply.setTo(session.getAddress());
            reply.getElement().addAttribute("from", doc.attributeValue("to"));
            reply.setError(PacketError.Condition.jid_malformed);
            session.process(reply);
            return;
        }

        packet.setFrom(session.getAddress());
        router.route(packet);
        session.incrementClientPacketCount();
    }

    private void processPresence(Element doc) {
        log.debug("processPresence()...");
        Presence packet;
        try {
            packet = new Presence(doc, false);
        } catch (IllegalArgumentException e) {
            log.debug("Rejecting packet. JID malformed", e);
            Presence reply = new Presence();
            reply.setID(doc.attributeValue("id"));
            reply.setTo(session.getAddress());
            reply.getElement().addAttribute("from", doc.attributeValue("to"));
            reply.setError(PacketError.Condition.jid_malformed);
            session.process(reply);
            return;
        }
        if (session.getStatus() == Session.STATUS_CLOSED
                && packet.isAvailable()) {
            log.warn("Ignoring available presence packet of closed session: "
                    + packet);
            return;
        }

        packet.setFrom(session.getAddress());
        router.route(packet);
        session.incrementClientPacketCount();
    }

    private void processIQ(Element doc) {
        log.debug("processIQ()...");
        IQ packet;
        try {
            packet = getIQ(doc);
        } catch (IllegalArgumentException e) {
            log.debug("Rejecting packet. JID malformed", e);
            IQ reply = new IQ();
            if (!doc.elements().isEmpty()) {
                reply.setChildElement(((Element) doc.elements().get(0))
                        .createCopy());
            }
            reply.setID(doc.attributeValue("id"));
            reply.setTo(session.getAddress());
            String to = doc.attributeValue("to");
            if (to != null) {
                reply.getElement().addAttribute("from", to);
            }
            reply.setError(PacketError.Condition.jid_malformed);
            session.process(reply);
            return;
        }

        //        if (packet.getID() == null) {
        //            // IQ packets MUST have an 'id' attribute
        //            StreamError error = new StreamError(
        //                    StreamError.Condition.invalid_xml);
        //            session.deliverRawText(error.toXML());
        //            session.close();
        //            return;
        //        }

        packet.setFrom(session.getAddress());
        router.route(packet);
        session.incrementClientPacketCount();
    }

    private IQ getIQ(Element doc) {
        Element query = doc.element("query");
        if (query != null && "jabber:iq:roster".equals(query.getNamespaceURI())) {
            return new Roster(doc);
        } else {
            return new IQ(doc, false);
        }
    }

    private void createSession(XmlPullParser xpp)
            throws XmlPullParserException, IOException {
        for (int eventType = xpp.getEventType(); eventType != XmlPullParser.START_TAG;) {
            eventType = xpp.next();
        }
        // Create the correct session based on the sent namespace
        String namespace = xpp.getNamespace(null);
        if ("jabber:client".equals(namespace)) {
            session = ClientSession.createSession(serverName, connection, xpp);
            if (session == null) {
                StringBuilder sb = new StringBuilder(250);
                sb.append("<?xml version='1.0' encoding='UTF-8'?>");
                sb.append("<stream:stream from=\"").append(serverName);
                sb.append("\" id=\"").append(randomString(5));
                sb.append("\" xmlns=\"").append(xpp.getNamespace(null));
                sb.append("\" xmlns:stream=\"").append(
                        xpp.getNamespace("stream"));
                sb.append("\" version=\"1.0\">");

                // bad-namespace-prefix in the response
                StreamError error = new StreamError(
                        StreamError.Condition.bad_namespace_prefix);
                sb.append(error.toXML());
                connection.deliverRawText(sb.toString());
                connection.close();
                log
                        .warn("Closing session due to bad_namespace_prefix in stream header: "
                                + namespace);
            }
        }
    }

    private boolean negotiateTLS() {
        if (connection.getTlsPolicy() == Connection.TLSPolicy.disabled) {
            // Set the not_authorized error
            StreamError error = new StreamError(
                    StreamError.Condition.not_authorized);
            connection.deliverRawText(error.toXML());
            connection.close();
            log.warn("TLS requested by initiator when TLS was never offered"
                    + " by server. Closing connection : " + connection);
            return false;
        }
        // Client requested to secure the connection using TLS.
        try {
            startTLS();
        } catch (Exception e) {
            log.error("Error while negotiating TLS", e);
            connection
                    .deliverRawText("<failure xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\">");
            connection.close();
            return false;
        }
        return true;
    }

    private void startTLS() throws Exception {
        Connection.ClientAuth policy;
        try {
            policy = Connection.ClientAuth.valueOf(Config.getString(
                    "xmpp.client.cert.policy", "disabled"));
        } catch (IllegalArgumentException e) {
            policy = Connection.ClientAuth.disabled;
        }
        connection.startTLS(policy);
    }

    private void tlsNegotiated() {
        // Offer stream features including SASL Mechanisms
        StringBuilder sb = new StringBuilder(620);
        sb.append("<?xml version='1.0' encoding='UTF-8'?>");
        sb.append("<stream:stream ");
        sb.append("xmlns:stream=\"http://etherx.jabber.org/streams\" ");
        sb.append("xmlns=\"jabber:client\" from=\"");
        sb.append(serverName);
        sb.append("\" id=\"");
        sb.append(session.getStreamID());
        sb.append("\" xml:lang=\"");
        sb.append(connection.getLanguage());
        sb.append("\" version=\"");
        sb.append(Session.MAJOR_VERSION).append(".").append(
                Session.MINOR_VERSION);
        sb.append("\">");
        sb.append("<stream:features>");
        // Include specific features such as auth and register for client sessions
        String specificFeatures = session.getAvailableStreamFeatures();
        if (specificFeatures != null) {
            sb.append(specificFeatures);
        }
        sb.append("</stream:features>");
        connection.deliverRawText(sb.toString());
    }

    private String randomString(int length) {
        if (length < 1) {
            return null;
        }
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
                + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[new Random().nextInt(71)];
        }
        return new String(randBuffer);
    }

    //  public String getNamespace() {
    //  return "jabber:client";
    //}

}
