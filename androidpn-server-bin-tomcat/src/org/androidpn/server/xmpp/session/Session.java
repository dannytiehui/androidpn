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
package org.androidpn.server.xmpp.session;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.androidpn.server.xmpp.net.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmpp.packet.JID;
import org.xmpp.packet.Packet;

/** 
 * This is an abstract class for a session between the server and a client.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public abstract class Session {

    public static final int MAJOR_VERSION = 1;

    public static final int MINOR_VERSION = 0;

    /** 
     * The session status when closed 
     */
    public static final int STATUS_CLOSED = 0;

    /**
     * The session status when connected
     */
    public static final int STATUS_CONNECTED = 1;

    /**
     * The session status when authenticated
     */
    public static final int STATUS_AUTHENTICATED = 2;

    private static final Log log = LogFactory.getLog(Session.class);

    protected Connection connection;

    protected SessionManager sessionManager;

    private String serverName;

    private JID address;

    private String streamID;

    private int status = STATUS_CONNECTED;

    private long startDate = System.currentTimeMillis();

    private long lastActiveDate;

    private long clientPacketCount = 0;

    private long serverPacketCount = 0;

    private final Map<String, Object> sessionData = new HashMap<String, Object>();

    /**
     * Constructor. Creates a new JID using server name and stream ID.
     * 
     * @param serverName the server name
     * @param conn the connection
     * @param streamID the stream ID
     */
    public Session(String serverName, Connection conn, String streamID) {
        this.connection = conn;
        this.sessionManager = SessionManager.getInstance();
        this.serverName = serverName;
        this.streamID = streamID;
        this.address = new JID(null, serverName, streamID, true);
    }

    /**
     * Returns the connection associated with this session.
     * 
     * @return the connection for this session
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Returns the server name.
     * 
     * @return the server name
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Returns the stream ID associated with this sesison.
     * 
     * @return the stream ID of this session
     */
    public String getStreamID() {
        return streamID;
    }

    /**
     * Returns the address of the user in JID.
     * 
     * @return the address of this session
     */
    public JID getAddress() {
        return address;
    }

    /**
     * Sets the new address of this session.
     * 
     * @param address the new address of this session
     */
    public void setAddress(JID address) {
        this.address = address;
    }

    /**
     * Returns the current status of this session.
     * 
     * @return the status of this session
     */
    public int getStatus() {
        return status;
    }

    /**
     *  Set the new status of this session.
     * 
     * @param status the new status of this session
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Returns the creation date of this session.
     * 
     * @return the creation date of this session
     */
    public Date getCreationDate() {
        return new Date(startDate);
    }

    /**
     * Returns the last activity time of this session.
     * 
     * @return the last time of this session
     */
    public Date getLastActiveDate() {
        return new Date(lastActiveDate);
    }

    /**
     * Increments the number of packets sent from the client to the server.
     */
    public void incrementClientPacketCount() {
        clientPacketCount++;
        lastActiveDate = System.currentTimeMillis();
    }

    /**
     * Increments the number of packets sent from the server to the client.
     */
    public void incrementServerPacketCount() {
        serverPacketCount++;
        lastActiveDate = System.currentTimeMillis();
    }

    /**
     * Returns the number of packets sent from the client to the server.
     * 
     * @return the number of packets
     */
    public long getNumClientPackets() {
        return clientPacketCount;
    }

    /**
     * Returns the number of packets sent from the server to the client.
     * 
     * @return the number of packets
     */
    public long getNumServerPackets() {
        return serverPacketCount;
    }

    public void setSessionData(String key, Object value) {
        synchronized (sessionData) {
            sessionData.put(key, value);
        }
    }

    public Object getSessionData(String key) {
        synchronized (sessionData) {
            return sessionData.get(key);
        }
    }

    public void removeSessionData(String key) {
        synchronized (sessionData) {
            sessionData.remove(key);
        }
    }

    /**
     * Process the packet.
     * 
     * @param packet the packet to process
     */
    public void process(Packet packet) {
        try {
            deliver(packet);
        } catch (Exception e) {
            log.error("Internal server error", e);
        }
    }

    /**
     * Delivers the packet to the associated connection.
     * 
     * @param packet the packet to deliver
     */
    public void deliver(Packet packet) {
        if (connection != null && !connection.isClosed()) {
            connection.deliver(packet);
        }
    }

    /**
     * Delivers raw text to the associated connection.
     * 
     * @param text the XML stanza string to deliver
     */
    public void deliverRawText(String text) {
        if (connection != null) {
            connection.deliverRawText(text);
        }
    }

    /**
     * Close the session including associated socket connection.
     */
    public void close() {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Returns true if the connection is closed.
     * 
     * @return true if the connection is closed, flase otherwise.
     */
    public boolean isClosed() {
        return connection.isClosed();
    }

    //    public boolean isSecure() {
    //        return connection.isSecure();
    //    }

    //    public boolean validate() {
    //        return connection.validate();
    //    }

    /**
     * Returns the IP address.
     * 
     * @return the IP address
     */
    public String getHostAddress() throws UnknownHostException {
        return connection.getHostAddress();
    }

    /**
     * Gets the host name for the IP address.
     * 
     * @return the host name for this IP address
     */
    public String getHostName() throws UnknownHostException {
        return connection.getHostName();
    }

    /**
     * Returns a text with the available stream features. 
     */
    public abstract String getAvailableStreamFeatures();

    @Override
    public String toString() {
        return super.toString() + " status: " + status + " address: " + address
                + " id: " + streamID;
    }

}
