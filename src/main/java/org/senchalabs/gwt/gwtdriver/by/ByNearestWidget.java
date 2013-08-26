package org.senchalabs.gwt.gwtdriver.by;

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

import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.senchalabs.gwt.gwtdriver.invoke.ClientMethodsFactory;
import org.senchalabs.gwt.gwtdriver.invoke.ExportedMethods;

/**
 * GWT-specific {@code By} implementation that looks at the current search context and above for the
 * nearest containing Widget object, and returns the root element of that widget. Always returns
 * one item, even if multiple items are requested by {@link SearchContext#findElements}.
 * <p>
 * To check if the current element is a widget, use {@link ByWidget} instead. If searching for a
 * descendent element that is a widget, use another By to find those elements along with a 
 * {@code ByWidget} to confirm it is a widget.
 *
 */
public class ByNearestWidget extends By {
	private final WebDriver driver;
	private final Class<?> widget;

	/**
	 * Finds the nearest containing widget of any type - anything that extends Widget will be found.
	 *
	 * @param driver The driver to use to communicate with the browser.
	 */
	public ByNearestWidget(WebDriver driver) {
		this(driver, Widget.class);
	}

	/**
	 * Finds the nearest containing widget of the given type. This will find any subtype of that
	 * widget, allowing you to pass in {@link ValueBoxBase} and find any {@link TextBox}, 
	 * {@link TextArea}, {@link IntegerBox}, etc, as these are all subclasses of 
	 * {@code ValueBoxBase}. Note that interfaces cannot be used, only base classes, and those
	 * classes *must* extend Widget.
	 *
	 * @param driver the driver to use to communicate with the browser
	 * @param type the type of widget to find
	 */
	public ByNearestWidget(WebDriver driver, Class<? extends Widget> type) {
		this.widget = type;
		this.driver = driver;
	}

	@Override
	public List<WebElement> findElements(SearchContext context) {
		WebElement elt = tryFindElement(context);
		if (elt != null) {
			return Collections.singletonList(elt);
		}
		return Collections.emptyList();
	}

	@Override
	public WebElement findElement(SearchContext context) {
		WebElement potentialElement = tryFindElement(context);
		if (potentialElement == null) {
			throw new NoSuchElementException("Cannot find a " + widget.getName() + " in " + context);
		}
		return potentialElement;
	}

	/**
	 * Helper method to check the local context to find the nearest containing widget without
	 * throwing an exception.
	 *
	 * @param context the search context to examine
	 * @return the nearest parent element of the specified type, or null if no such element exists
	 */
	private WebElement tryFindElement(SearchContext context) {
		WebElement elt = context.findElement(By.xpath("."));
		ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
		WebElement potentialElement = m.getContainingWidgetEltOfType(elt, widget.getName());
		return potentialElement;
	}

	@Override
	public String toString() {
		return "ByNearestWidget" + (widget == Widget.class ? "" : " " + widget.getName());
	}

}
