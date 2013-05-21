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

package eu.vranckaert.worktime.utils.view.showcase;

import android.app.Activity;
import com.github.espiandev.showcaseview.ShowcaseView;

import java.util.List;

/**
 * Date: 18/03/13
 * Time: 8:34
 *
 * @author Dirk Vranckaert
 */
public class ShowcaseViewUtility implements ShowcaseView.OnShowcaseEventListener {
    private List<ShowcaseViewElement> showcaseViewElements;
    private Activity activity;
    private ShowcaseView showcaseView;
    private int currentShowcaseViewIndex = -1;

    private OnShowcaseEndedListener onShowcaseEndedListener;

    private ShowcaseViewUtility(List<ShowcaseViewElement> showcaseViewElements, Activity activity) {
        this.showcaseViewElements = showcaseViewElements;
        this.activity = activity;

        showNextShowcaseElement();
    }

    public static ShowcaseViewUtility start(List<ShowcaseViewElement> showcaseViewElements, Activity activity) {
        return new ShowcaseViewUtility(showcaseViewElements, activity);
    }

    private void showNextShowcaseElement() {
        if (showcaseViewElements != null && !showcaseViewElements.isEmpty() && (currentShowcaseViewIndex < showcaseViewElements.size()-1) ) {
            currentShowcaseViewIndex++;
            ShowcaseViewElement showcaseViewElement = showcaseViewElements.get(currentShowcaseViewIndex);
            showcaseView = showcaseViewElement.getShowcaseView(activity);
            showcaseView.setOnShowcaseEventListener(this);
        } else if (onShowcaseEndedListener != null) {
            onShowcaseEndedListener.onShowcaseEndedListener();
        }
    }

    @Override
    public void onShowcaseViewHide(ShowcaseView showcaseView) {
        showNextShowcaseElement();
    }

    @Override
    public void onShowcaseViewShow(ShowcaseView showcaseView) {}

    public interface OnShowcaseEndedListener {
        public void onShowcaseEndedListener();
    }

    public void setOnShowcaseEndedListener(OnShowcaseEndedListener onShowcaseEndedListener) {
        this.onShowcaseEndedListener = onShowcaseEndedListener;
    }
}
