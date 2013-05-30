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

package eu.vranckaert.worktime.utils.view.validation;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import eu.vranckaert.worktime.R;

/**
 * User: DIRK VRANCKAERT
 * Date: 30/05/13
 * Time: 8:56
 */
public class WorkTimeValidator extends Validator implements Validator.ValidationListener {
    Context context;
    private View validationErrorContainer;
    private TextView validationErrorTextView;

    public WorkTimeValidator(final Activity context) {
        super(context);
        this.context = context;
        this.validationErrorContainer = context.findViewById(R.id.validation_error_container);
        this.validationErrorTextView = (TextView) context.findViewById(R.id.validation_error_text_view);
        this.setValidationListener(this);
    }

    @Override
    public void preValidation() {
        validationErrorContainer.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess() {}

    @Override
    public void onFailure(View failedView, Rule<?> failedRule) {
        if (failedView instanceof TextView || failedView instanceof EditText) {
            validationErrorTextView.setText(R.string.validation_basic_big_message);
            if (failedView instanceof EditText) {
                final EditText failedViewEditText = (EditText) failedView;
                failedViewEditText.setError(failedRule.getFailureMessage());
                failedViewEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        failedViewEditText.setError(null);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        }

        validationErrorTextView.setText(context.getString(R.string.validation_basic_message) + ": " + failedRule.getFailureMessage());

        if (failedView != null) {
            failedView.requestFocus();
        }

        validationErrorContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onValidationCancelled() {

    }
}
