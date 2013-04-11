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

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.senchalabs.gwt.gwtdriver.invoke.ClientMethodsFactory;
import org.senchalabs.gwt.gwtdriver.invoke.ExportedMethods;

import com.google.gwt.user.client.ui.Widget;

/**
 * GWT-specific {@code By} implementation that looks for widgets that in the current search context.
 * Use in conjunction with other {@code By} statements to look for widgets that match a certain
 * criteria.
 *
 */
public class ByWidget extends By {
	private final WebDriver driver;
	private final String type;

	public ByWidget(WebDriver driver) {
		this(driver, Widget.class);
	}

	public ByWidget(WebDriver driver, Class<? extends Widget> widgetType) {
		this.driver = driver;
		this.type = widgetType.getName();
	}

	@Override
	public List<WebElement> findElements(SearchContext context) {
		List<WebElement> elts = context.findElements(By.xpath("."));

//		System.out.println("Searching in " + context + " for all " + type);
		List<WebElement> ret = new ArrayList<WebElement>();
		ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
		for (WebElement elt : elts) {
			String matches = m.instanceofwidget(elt, type);

//			System.out.println("ByWidget\t" + matches + "\t" + elt.getTagName() + ": " + elt.getText());
			if ("true".equals(matches)) {
				ret.add(elt);
			}
		}
//		System.out.println("Done, found " + ret.size());

		return ret;
	}
	@Override
	public WebElement findElement(SearchContext context) {
		List<WebElement> elts = context.findElements(By.xpath("."));

//		System.out.println("Searching in " + context + " for any " + type);
		ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
		for (WebElement elt : elts) {
			String matches = m.instanceofwidget(elt, type);

//			System.out.println("ByWidget\t" + matches + "\t" + elt.getTagName() + ": " + elt.getText());
			if ("true".equals(matches)) {
				return elt;
			}
		}
		throw new NoSuchElementException("Can't find widget of type " + type);
	}
	@Override
	public String toString() {
		return "isWidget(" + type + ")";
	}
}