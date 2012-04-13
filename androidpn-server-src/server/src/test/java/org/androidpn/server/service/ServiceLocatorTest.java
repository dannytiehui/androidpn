/*
 * Copyright 2010 the original author or authors.
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
package org.androidpn.server.service;

import java.io.File;

import junit.framework.TestCase;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class ServiceLocatorTest extends TestCase {

    private File homeDir;

    protected void setUp() throws Exception {
        // File sysTempDir = new File(System.getProperty("java.io.tmpdir"));
        // homeDir = File.createTempFile("apn", ".tmp", sysTempDir);
        homeDir = new File("/temp/androidpn" + System.currentTimeMillis());
        File confDir = new File(homeDir, "conf");
        confDir.mkdirs();
        System.out.println("tempConfDir=" + confDir);
    }

    public void testGetService() throws Exception {
        System.setProperty("base.dir", homeDir.getAbsolutePath());
        
        UserService service = ServiceLocator.getUserService();
        assertNotNull(service);
        assertTrue(service instanceof UserService);
    }

    protected void tearDown() {
        if (homeDir.exists()) {
            homeDir.delete();
        }
    }

}
