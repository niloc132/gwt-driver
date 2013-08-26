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
import org.openqa.selenium.support.pagefactory.ByChained;

/**
 * When looking for only one item, speeds up the last By in the chain by only running it until it
 * finds something.
 * <p>
 * When using {@link SearchContext#findElements}, uses ByChained to find all possible elements as normal.
 * <p>
 * Using this class should be functionally eqivelent to using ByChained, except faster in some
 * cases.
 *
 */
public class FasterByChained extends By {
	private By[] bys;
	public FasterByChained(By... bys) {
		this.bys = bys;
	}
	@Override
	public List<WebElement> findElements(SearchContext context) {
		return new ByChained(bys).findElements(context);
	}
	@Override
	public WebElement findElement(SearchContext context) {
		By[] firstBys = new By[bys.length - 1];
		System.arraycopy(bys, 0, firstBys, 0, firstBys.length);
		List<WebElement> elts = new ByChained(firstBys).findElements(context);
		if (elts == null) { 
			throw new NoSuchElementException("Cannot locate element using " + this);
		}
		for (WebElement elt : elts) {
			try {
				return elt.findElement(bys[bys.length - 1]);
			} catch (NoSuchElementException ex) {
				continue;
			}
		}
		throw new NoSuchElementException("Cannot locate element using " + this);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("FasterByChained(");
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
