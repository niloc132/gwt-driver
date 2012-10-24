package com.colinalworth.gwtdriver.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.colinalworth.gwtdriver.ModuleUtilities;
import com.colinalworth.gwtdriver.invoke.ClientMethodsFactory;
import com.colinalworth.gwtdriver.models.GwtWidget.ForWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a GWT Widget class, allowing some 
 * @author colin
 *
 */
@ForWidget(Widget.class)
public class GwtWidget<F extends GwtWidgetFinder<?>> {
	private final WebDriver driver;
	private final WebElement element;

	public GwtWidget(WebDriver driver, WebElement element) {
		assert element != null && driver != null;
		this.driver = driver;
		this.element = element;
	}

	public WebElement getElement() {
		return element;
	}
	public WebDriver getDriver() {
		return driver;
	}


	@Inherited
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ForWidget {
		Class<? extends Widget> value();
	}

	public <W extends GwtWidget> W as(Class<W> clazz) {
		try {
			W instance;
			ForWidget widgetType = clazz.getAnnotation(ForWidget.class);
			if (widgetType != null) {
//				String module = ModuleUtilities.findModules(getDriver()).get(0);
				String is = (String) ModuleUtilities.executeExportedFunction("instanceofwidget", driver, getElement(), widgetType.value().getName());
//				String is = (String)((JavascriptExecutor)getDriver()).executeAsyncScript("_"+module+"_se.apply(this, arguments)", "instanceofwidget", getElement(), widgetType.value().getName());
				if (!"true".equals(is)) {
					throw new IllegalArgumentException("Cannot complete as(" + clazz.getSimpleName() + ".class), element isn't a " + widgetType.value().getName());
				}
			}
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

			List<WebElement> elts = context.findElements(By.tagName("*"));

			System.out.println();
			System.out.println("Searching in " + context + " for any widget");
			List<WebElement> ret = new ArrayList<WebElement>();
//			String module = ModuleUtilities.findModules(driver).get(0);
			for (WebElement elt : elts) {
				String matches = (String) ModuleUtilities.executeExportedFunction("isWidget", driver, elt);
//				String matches = (String) ((JavascriptExecutor)driver).executeAsyncScript("_" + module + "_se.apply(this, arguments)", "isWidget", elt);

				System.out.println("ByWidget  " + matches + "  " + elt.getTagName() + ": " + elt.getText());
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
