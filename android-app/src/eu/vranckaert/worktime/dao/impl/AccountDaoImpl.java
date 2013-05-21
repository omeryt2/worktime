/*
 * Copyright 2013 Dirk Vranckaert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.vranckaert.worktime.dao.impl;

import android.content.Context;
import com.google.inject.Inject;
import eu.vranckaert.worktime.dao.AccountDao;
import eu.vranckaert.worktime.dao.generic.GenericDaoImpl;
import eu.vranckaert.worktime.model.User;

import java.util.List;

/**
 * User: Dirk Vranckaert
 * Date: 13/12/12
 * Time: 11:36
 */
public class AccountDaoImpl extends GenericDaoImpl<User, String> implements AccountDao {
    private static final String LOG_TAG = AccountDaoImpl.class.getSimpleName();

    @Inject
    public AccountDaoImpl(final Context context) {
        super(User.class, context);
    }

    @Override
    public void storeLoggedInUser(User user) {
        super.deleteAll();
        super.save(user);
    }

    @Override
    public User getLoggedInUser() {
        List<User> users = super.findAll();
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }
}
