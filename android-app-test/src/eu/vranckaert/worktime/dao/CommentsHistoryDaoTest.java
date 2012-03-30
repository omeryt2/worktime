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

package eu.vranckaert.worktime.dao;

import eu.vranckaert.worktime.dao.impl.CommentHistoryDaoImpl;
import eu.vranckaert.worktime.model.CommentHistory;
import eu.vranckaert.worktime.test.cases.DaoTestCase;

import java.util.List;

/**
 * User: DIRK VRANCKAERT
 * Date: 22/03/12
 * Time: 15:23
 */
public class CommentsHistoryDaoTest extends DaoTestCase<CommentHistoryDao, CommentHistoryDaoImpl> {
    public CommentsHistoryDaoTest() {
        super(CommentHistoryDaoImpl.class);
    }
    
    public void testSave() {
        String testComment = "This is just custom test-comment";
        
        List<CommentHistory> initialHistories = getDao().findAll();
        assertNotNull("The initial history list should not be null", initialHistories);
        assertEquals("No items should be in the list", 0, initialHistories.size());
        
        getDao().save(testComment);

        List<CommentHistory> histories = getDao().findAll();
        assertNotNull("The initial history list should not be null", histories);
        assertEquals("One item should be in the list", 1, histories.size());
        CommentHistory history = histories.get(0);
        assertNotNull("The id should not be null", history.getId());
        assertEquals(testComment, history.getComment());
    }

    public void testDeleteAll() {
        getDao().save("Comment 1");
        getDao().save("Comment 2");
        getDao().save("Comment 3");

        List<CommentHistory> histories = getDao().findAll();
        assertNotNull("The history list should not be null", histories);
        assertEquals("Tree items should be in the list", 3, histories.size());

        getDao().deleteAll();

        histories = getDao().findAll();
        assertNotNull("The history list should not be null", histories);
        assertEquals("No items should be in the list", 0, histories.size());
    }
}
