package com.colinalworth.gwtdriver;

import java.util.Collections;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.colinalworth.gwtdriver.invoke.ClientMethodsFactory;
import com.colinalworth.gwtdriver.invoke.ExportedMethods;
import com.google.gwt.user.client.ui.Widget;

public class ByNearestWidget extends By {
	private final WebDriver driver;
	private final Class<?> widget;
	public ByNearestWidget(WebDriver driver) {
		this(driver, Widget.class);
	}
	public ByNearestWidget(WebDriver driver, Class<? extends Widget> type) {
		this.widget = type;
		this.driver = driver;
	}

	@Override
	public List<WebElement> findElements(SearchContext context) {
		WebElement elt = findElement(context);
		if (elt != null) {
			return Collections.singletonList(elt);
		}
		return Collections.emptyList();

		//		do {
		//			String matches = (String) ((JavascriptExecutor)driver).executeAsyncScript("_"+moduleName+"_se.apply(this, arguments)", "instanceofwidget", elt, widget.getName());
		//			if ("true".equals(matches)) {
		//				System.out.println("...correct");
		//				return Collections.singletonList(elt);
		//			}
		//			List<WebElement>elts = elt.findElements(By.xpath("parent::node()[(not(html))]"));
		//			elt = elts.isEmpty() ? null : elts.get(0);
		//		} while (null != elt);
	}

	@Override
	public WebElement findElement(SearchContext context) {
		WebElement elt = context.findElement(By.xpath("."));
		ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
		return m.getContainingWidgetEltOfType(elt, widget.getName());
	}

	@Override
	public String toString() {
		return "ByNearestWidget" + (widget == Widget.class ? "" : " " + widget.getName());
	}

}
