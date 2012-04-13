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
package org.androidpn.server.dao;

import org.androidpn.server.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class UserDaoTest extends
        AbstractTransactionalDataSourceSpringContextTests {

    private UserDao dao = null;

    public void setUserDao(UserDao dao) {
        this.dao = dao;
    }

    protected String[] getConfigLocations() {
        setAutowireMode(AUTOWIRE_BY_NAME);
        return new String[] { "classpath:/spring-test.xml" };
    }

    public void testDummy() {
        assertTrue(1 == 1);
    }

    public void testGetUser() throws Exception {
        User user = dao.getUser(1L);
        assertNotNull(user);
        assertEquals("admin@domain.com", user.getEmail());
    }

    public void testAddAndRemoveUser() throws Exception {
        User user = new User();
        user.setEmail("testuser@domain.com");
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword("test1234");
        user = dao.saveUser(user);

        assertNotNull(user.getId());
        user = dao.getUser(user.getId());
        assertEquals("test1234", user.getPassword());

        dao.removeUser(user.getId());
        user = dao.getUser(user.getId());
        assertNull(user);
    }

    public void testUpdateUser() throws Exception {
        User user = dao.getUser(1L);
        dao.saveUser(user);
        user = dao.getUser(1L);
        user.setId(null);
        endTransaction();
        try {
            dao.saveUser(user);
            fail("saveUser didn't throw DataIntegrityViolationException");
        } catch (DataIntegrityViolationException e) {
            assertNotNull(e);
        }
    }

    public void testUserExists() throws Exception {
        assertTrue(dao.exists(1L));
    }

    public void testUserNotExists() throws Exception {
        assertFalse(dao.exists(-1L));
    }

}
