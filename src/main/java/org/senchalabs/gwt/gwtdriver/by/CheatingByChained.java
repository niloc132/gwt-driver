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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.selenesedriver.FindElements;
import org.openqa.selenium.support.pagefactory.ByChained;

/**
 * When looking for only one item, cheats by only looking for one item at each level. Useful only if
 * you want the first result of each {@link By} operation, otherwise use {@link FasterByChained} or
 * {@link ByChained}.
 * <p>
 * When running {@link FindElements} to search for multiple items, uses ByChained normal.
 *
 */
public class CheatingByChained extends By {
	private By[] bys;
	public CheatingByChained(By... bys) {
		this.bys = bys;
	}
	@Override
	public List<WebElement> findElements(SearchContext context) {
		return new ByChained(bys).findElements(context);
	}
	@Override
	public WebElement findElement(SearchContext context) {
		WebElement elt = null;
		for (By by : bys) {
			if (elt == null) {
				elt = by.findElement(context);
			} else {
				elt = by.findElement(elt);
			}
			if (elt == null) { 
				throw new NoSuchElementException("Cannot locate element using " + this);
			}
		}
		if (elt == null) { 
			throw new NoSuchElementException("Cannot locate element using " + this);
		}
		return elt;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("CheatingByChained(");
		stringBuilder.append("{");

		boolean first = true;
		for (By by : bys) {
			stringBuilder.append((first ? "" : ",")).append(by);
			first = false;
		}
		stringBuilder.append("})");
		return stringBuilder.toString();
	}
}
