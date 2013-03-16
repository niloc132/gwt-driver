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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import com.colinalworth.gwtdriver.by.ByNearestWidget;
import com.colinalworth.gwtdriver.by.ByWidget;
import com.colinalworth.gwtdriver.by.FasterByChained;
import com.colinalworth.gwtdriver.models.Dialog.DialogFinder;
import com.colinalworth.gwtdriver.models.GwtWidget.ForWidget;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * 
 *
 */
@ForWidget(DialogBox.class)
public class Dialog extends GwtWidget<DialogFinder> {


	public Dialog(WebDriver driver, WebElement element) {
		super(driver, element);
	}

	public String getHeadingText() {
		WebElement headerWidget = getHeaderElement();
		return headerWidget.getText();
	}

	/**
	 * @return finds the header element. May need to be overridden if a custom caption type is used
	 */
	public WebElement getHeaderElement() {
		//very unlikely to have more than one caption, use the first we hit
		WebElement headerWidget = getElement().findElement(new FasterByChained(
				By.xpath(".//*"),
				new ByWidget(getDriver(), DialogBox.CaptionImpl.class)));
		return headerWidget;
	}

	public static class DialogFinder extends GwtWidgetFinder<Dialog> {
		private boolean top = false;
		private String heading;
		public DialogFinder withHeading(String heading) {
			this.heading = heading;
			return this;
		}
		public DialogFinder atTop() {
			top = true;
			return this;
		}
		@Override
		public Dialog done() {
			if (heading != null) {
				String escaped = escapeToString(heading);
				elt = elt.findElement(new FasterByChained(By.xpath("//body/*"),
						new ByWidget(driver, DialogBox.class),
						By.xpath(".//*[contains(text(), "+escaped+")]"),
						new ByNearestWidget(driver, DialogBox.class)));
			} else if (top) {
				List<WebElement> allWindows = driver.findElements(new ByChained(
						By.xpath("//body/*"),
						new ByWidget(driver, DialogBox.class)
						));
				Collections.sort(allWindows, new Comparator<WebElement>() {
					public int compare(WebElement o1, WebElement o2) {
						return Integer.parseInt(o1.getCssValue("z-index")) - Integer.parseInt(o2.getCssValue("z-index"));
					}
				});
				elt = allWindows.get(0);
			}
			
			return new Dialog(driver, elt);
		}
	}

}
