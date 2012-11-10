package com.colinalworth.gwtdriver.by;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

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
		StringBuilder stringBuilder = new StringBuilder("By.chained(");
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
