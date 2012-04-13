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

import org.androidpn.server.model.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class UserServiceTest extends
        AbstractTransactionalDataSourceSpringContextTests {

    private final Log log = LogFactory.getLog(getClass());

    protected String[] getConfigLocations() {
        setAutowireMode(AUTOWIRE_BY_NAME);
        return new String[] { "classpath:/spring-test.xml" };
    }

    private User user;

    private UserService service = null;

    public void setUserService(UserService userService) {
        this.service = userService;
    }

    public void testGetUser() throws Exception {
        user = service.getUserByUsername("admin");
        assertNotNull(user);
        log.debug(user);
        assertEquals("admin@domain.com", user.getEmail());
    }

    public void testSaveUser() throws Exception {
        user = service.getUserByUsername("admin");
        user.setName("Master");
        user = service.saveUser(user);
    }

    public void testAddAndRemoveUser() throws Exception {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("test1234");
        user.setName("Test User");
        user.setEmail("testuser@domain.com");
        user = service.saveUser(user);
        
        assertEquals("testuser@domain.com", user.getEmail());

        log.debug("removing user...");
        service.removeUser(user.getId());
    }
    
    public void testUserNotFoundException() throws Exception {
        try {
            user = service.getUserByUsername("userxx@domain.com");
            fail("Expected 'Exception' not thrown");
        } catch (UserNotFoundException e) {
            log.debug(e);
            assertNotNull(e);
        }
    }

}
