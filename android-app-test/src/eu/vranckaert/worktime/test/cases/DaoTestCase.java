/*
 * Copyright 2012 Dirk Vranckaert
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

package eu.vranckaert.worktime.test.cases;

import android.content.Context;
import android.test.AndroidTestCase;
import eu.vranckaert.worktime.dao.generic.GenericDao;
import eu.vranckaert.worktime.dao.generic.GenericDaoImpl;
import eu.vranckaert.worktime.test.utils.TestUtil;

import java.lang.reflect.Constructor;

/**
 * User: DIRK VRANCKAERT
 * Date: 22/03/12
 * Time: 14:58
 */
public class DaoTestCase<I extends GenericDao, T extends GenericDaoImpl> extends AndroidTestCase {
    /**
     * The context that is used to execute the test.
     */
    public Context ctx;
    
    private I dao;
    private Class daoClass;
    
    public DaoTestCase(Class<T> daoImplClass) {
        this.daoClass = daoImplClass;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ctx = getContext();

        Constructor<T> constructor = daoClass.getConstructor(Context.class);
        dao = (I) constructor.newInstance(ctx);
        
        TestUtil.cleanUpDatabase(getContext());
    }
    
    public I getDao() {
        return dao;
    }
    
    public <F extends GenericDao, D extends GenericDaoImpl> F getDaoForClass(Class<F> daoInterface, Class<D> daoClass) {
        return TestUtil.getDaoForClass(ctx, daoInterface, daoClass);
    }
}
