package com.colinalworth.gwtdriver.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver.Timeouts;

/**
 * Represents a GWT Widget class, allowing some 
 * @author colin
 *
 */
public class GwtWidget {
	private final WebDriver driver;
	private final WebElement element;

	public GwtWidget(WebDriver driver, WebElement element) {
		this.driver = driver;
		this.element = element;
	}

	public WebElement getElement() {
		return element;
	}
	public WebDriver getDriver() {
		return driver;
	}



	public <W extends GwtWidget> W as(Class<W> clazz) {
		try {
			W instance;
			instance = clazz.getConstructor(WebDriver.class, WebElement.class).newInstance(getDriver(), getElement());
			return instance;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static class ByWidget extends By {
		private final WebDriver driver;
		public ByWidget(WebDriver driver) {
			this.driver = driver;
		}
		@Override
		public List<WebElement> findElements(SearchContext context) {
			//			return ((FindsByXPath) context).findElementsByXPath(".//*[@__listener]");

			driver.manage().timeouts().setScriptTimeout(1000, TimeUnit.SECONDS);

			List<WebElement> elts = context.findElements(By.tagName("*"));

			System.out.println();
			System.out.println("Searching in " + context);
			List<WebElement> ret = new ArrayList<WebElement>();
			for (WebElement elt : elts) {
				String matches = (String) ((JavascriptExecutor)driver).executeAsyncScript("_simplewidgets_se.apply(this, arguments)", "isWidget", elt);

				System.out.println("ByWidget  " + matches + "  " + elt);
				if ("true".equals(matches)) {
					ret.add(elt);
				}
			}
			System.out.println("Done, found " + ret.size());

			return ret;
		}
		@Override
		public String toString() {
			return "isWidget";
		}
	}
}
