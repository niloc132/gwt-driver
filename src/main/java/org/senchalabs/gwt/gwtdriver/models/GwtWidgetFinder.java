package org.senchalabs.gwt.gwtdriver.models;

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

import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.ui.FluentWait;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

public class GwtWidgetFinder<W extends GwtWidget<?>> {
	protected WebDriver driver;
	protected WebElement elt;
	public GwtWidgetFinder<W> withDriver(WebDriver driver) {
		this.driver = driver;
		if (elt == null) {
			elt = driver.findElement(By.tagName("body"));
		}
		return this;
	}
	public GwtWidgetFinder<W> withElement(WebElement element) {
		if (driver == null && elt instanceof WrapsDriver) {
			driver = ((WrapsDriver) elt).getWrappedDriver();
		}
		this.elt = element;
		return this;
	}
	@SuppressWarnings("unchecked")
	public W done() {
		assert getClass() == GwtWidgetFinder.class : "GwtWidgetFinder.done() must be overridden in all subclasses";
		return (W) new GwtWidget<GwtWidgetFinder<?>>(driver, elt);
	}

	public W waitFor() {
		return waitFor(10, TimeUnit.SECONDS);
	}

	public W waitFor(long duration, TimeUnit unit) {
		return new FluentWait<WebDriver>(driver)
				.withTimeout(duration, unit)
				.ignoring(NotFoundException.class)
				.until(new Function<WebDriver, W>() {
			@Nullable
			@Override
			public W apply(@Nullable WebDriver webDriver) {
				return done();
			}
		});
	}
}
