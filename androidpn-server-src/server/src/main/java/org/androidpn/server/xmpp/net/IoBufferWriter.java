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
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;

/** 
 * Wrapper on a MINA {@link IoBuffer} that extends the Writer class.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class IoBufferWriter extends Writer {

    private CharsetEncoder encoder;

    private IoBuffer ioBuffer;

    /**
     * Constructor.
     * 
     * @param ioBuffer the IoBuffer
     * @param encoder the charset encoder
     */
    public IoBufferWriter(IoBuffer ioBuffer, CharsetEncoder encoder) {
        this.encoder = encoder;
        this.ioBuffer = ioBuffer;
    }

    /**
     * Writes a portion of an array of characters.
     * 
     * @param cbuf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        ioBuffer.putString(new String(cbuf, off, len), encoder);
    }

    /**
     * Flushes the stream.
     */
    @Override
    public void flush() throws IOException {
        // Ignore
    }

    /**
     * Closes the stream, flushing it first.
     */
    @Override
    public void close() throws IOException {
        // Ignore
    }

}
