package com.colinalworth.gwtdriver.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.colinalworth.gwtdriver.invoke.ClientMethodsFactory;
import com.colinalworth.gwtdriver.invoke.ExportedMethods;
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

	public <W extends GwtWidget<T>, T extends GwtWidgetFinder<W>> T find(Class<W> widgetType) {
		Type i = widgetType;
		do {
			if (i instanceof ParameterizedType) {
				ParameterizedType t = (ParameterizedType) i;
				if (t.getRawType() == GwtWidget.class) {
					@SuppressWarnings("unchecked")
					Class<T> finderType = (Class<T>) t.getActualTypeArguments()[0];
					
					T instance = createInstance(finderType);
					if (instance != null) {
						instance.withDriver(getDriver());
						instance.withElement(getElement());
						return instance;
					}
				}
			}
			i = (i instanceof Class) ? ((Class<?>)i).getGenericSuperclass() : null;
		} while(i != null);
		return null;
	}
	protected <T extends GwtWidgetFinder<?>> T createInstance(Class<T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Inherited
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ForWidget {
		Class<? extends Widget> value();
	}

	public <W extends GwtWidget<?>> W as(Class<W> clazz) {
		try {
			W instance;
			ForWidget widgetType = clazz.getAnnotation(ForWidget.class);
			if (widgetType != null) {
				ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
				String is = m.instanceofwidget(element, widgetType.value().getName());
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
		private final String type;
		public ByWidget(WebDriver driver) {
			this(driver, Widget.class);
		}
		public ByWidget(WebDriver driver, Class<? extends Widget> widgetType) {
			this.driver = driver;
			this.type = widgetType.getName();
		}
		@Override
		public List<WebElement> findElements(SearchContext context) {
			List<WebElement> elts = context.findElements(By.xpath(".//*"));

			System.out.println("Searching in " + context + " for any widget");
			List<WebElement> ret = new ArrayList<WebElement>();
			ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
			for (WebElement elt : elts) {
				String matches = m.instanceofwidget(elt, type);

				System.out.println("ByWidget\t" + matches + "\t" + elt.getTagName() + ": " + elt.getText());
				if ("true".equals(matches)) {
					ret.add(elt);
				}
			}
			System.out.println("Done, found " + ret.size());

			return ret;
		}
		@Override
		public WebElement findElement(SearchContext context) {
			List<WebElement> elts = context.findElements(By.xpath(".//*"));
			ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
			for (WebElement elt : elts) {
				String matches = m.instanceofwidget(elt, type);

				System.out.println("ByWidget\t" + matches + "\t" + elt.getTagName() + ": " + elt.getText());
				if ("true".equals(matches)) {
					return elt;
				}
			}
			throw new NoSuchElementException("Can't find widget of type " + type);
		}
		@Override
		public String toString() {
			return "isWidget(" + type + ")";
		}
	}
}
