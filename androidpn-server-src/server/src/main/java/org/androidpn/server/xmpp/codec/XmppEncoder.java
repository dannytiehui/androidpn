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
package org.androidpn.server.xmpp.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/** 
 *  Encoder class that does nothing (to the already encoded data). 
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppEncoder implements ProtocolEncoder {

    // private final Log log = LogFactory.getLog(XmppEncoder.class);

    public void encode(IoSession session, Object message,
            ProtocolEncoderOutput out) throws Exception {
        // log.debug("encode()...");
    }

    public void dispose(IoSession session) throws Exception {
        // log.debug("dispose()...");
    }

}
