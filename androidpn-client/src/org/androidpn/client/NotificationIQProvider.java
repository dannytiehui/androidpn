/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

/** 
 * This class parses incoming IQ packets to NotificationIQ objects.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class NotificationIQProvider implements IQProvider {

    public NotificationIQProvider() {
    }

    @Override
    public IQ parseIQ(XmlPullParser parser) throws Exception {

        NotificationIQ notification = new NotificationIQ();
        for (boolean done = false; !done;) {
            int eventType = parser.next();
            if (eventType == 2) {
                if ("id".equals(parser.getName())) {
                    notification.setId(parser.nextText());
                }
                if ("apiKey".equals(parser.getName())) {
                    notification.setApiKey(parser.nextText());
                }
                if ("title".equals(parser.getName())) {
                    notification.setTitle(parser.nextText());
                }
                if ("message".equals(parser.getName())) {
                    notification.setMessage(parser.nextText());
                }
                if ("uri".equals(parser.getName())) {
                    notification.setUri(parser.nextText());
                }
            } else if (eventType == 3
                    && "notification".equals(parser.getName())) {
                done = true;
            }
        }

        return notification;
    }

}
