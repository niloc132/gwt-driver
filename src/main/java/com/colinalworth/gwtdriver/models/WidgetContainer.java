package com.colinalworth.gwtdriver.models;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import com.colinalworth.gwtdriver.by.ByWidget;
import com.google.gwt.user.client.ui.HasWidgets;

/**
 * A Widget that can contain more widgets. A GWT widget type need not implement or extend anything
 * to be mapped to this, but likely implements {@link HasWidgets}.
 * @author colin
 *
 */
public class WidgetContainer extends GwtWidget<GwtWidgetFinder<WidgetContainer>> {

	public WidgetContainer(WebDriver driver, WebElement element) {
		super(driver, element);
	}

	/**
	 * Convenience method to find child/descendant widgets and wrap them. Same semantics as 
	 * {@link WebElement#findElements(By)} - can be used to find if nothing matches this By.
	 * 
	 * @param by
	 * @return
	 */
	public List<GwtWidget<?>> findWidgets(By by) {
		List<WebElement> elts = getElement().findElements(new ByChained(by, new ByWidget(getDriver())));
		System.out.println("elts found " + elts.size());
		List<GwtWidget<?>> children = new ArrayList<GwtWidget<?>>();
		for (WebElement elt : elts) {
			children.add(new GwtWidget<GwtWidgetFinder<?>>(getDriver(), elt));
		}
		return children;
	}

	/**
	 * Convenience method to find a specific child/descendant widgets and wrap it. Same 
	 * semantics as {@link WebElement#findElement(By)} - will throw an exception if nothing 
	 * matches.
	 * 
	 * @param by
	 * @return
	 */
	public GwtWidget<?> findWidget(By by) {
		WebElement elt = getElement().findElement(new ByChained(/*by, */new ByWidget(getDriver())));
		return new GwtWidget<GwtWidgetFinder<?>>(getDriver(), elt);
	}

}
