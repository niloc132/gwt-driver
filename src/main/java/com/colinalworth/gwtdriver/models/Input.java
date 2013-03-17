package com.colinalworth.gwtdriver.models;

/*
 * #%L
 * gwt-driver
 * %%
 * Copyright (C) 2012 - 2013 Sencha Labs
 * %%
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
 * #L%
 */

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.colinalworth.gwtdriver.models.GwtWidget.ForWidget;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;

/**
 * Simple abstraction to refer to many input widgets in GWT such as {@link TextBox}, 
 * {@link TextArea}, {@link PasswordTextBox}, {@link IntegerBox}, etc. 
 * <p>
 * Has no specialized finder type, as GWT doesn't have anything that clearly and uniformly marks
 * fields as being labeled, or empty text to indicate that nothing has been entered. Users of this
 * library can define their own GwtWidget type with a corresponding finder to rectify this if they
 * have a consistent way that these widgets can be found.
 *
 */
@ForWidget(ValueBoxBase.class)
public class Input extends GwtWidget<GwtWidgetFinder<Input>>{
	public Input(WebDriver driver, WebElement element) {
		super(driver, element);
	}

	public void sendKeys(String str) {
		getElement().sendKeys(str);
	}

	public String getValue() {
		return getElement().getAttribute("value");
	}
}
