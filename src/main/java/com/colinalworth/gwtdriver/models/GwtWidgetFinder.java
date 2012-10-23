package com.colinalworth.gwtdriver.models;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GwtWidgetFinder<W extends GwtWidget<?>> {
	protected WebDriver driver;
	protected WebElement elt;
	public GwtWidgetFinder<W> withDriver(WebDriver driver) {
		this.driver = driver;
		return this;
	}
	public GwtWidgetFinder<W> withElement(WebElement element) {
		this.elt = element;
		return this;
	}
	public W done() {
		assert getClass() == GwtWidgetFinder.class : "GwtWidgetFinder.done() must be overridden in all subclasses";
		return (W) new GwtWidget<GwtWidgetFinder<?>>(driver, elt);
	}
}
