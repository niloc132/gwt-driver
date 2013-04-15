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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.senchalabs.gwt.gwtdriver.invoke.ClientMethodsFactory;
import org.senchalabs.gwt.gwtdriver.invoke.ExportedMethods;
import org.senchalabs.gwt.gwtdriver.models.GwtWidget.ForWidget;

import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a GWT Widget class. Subclasses should add appropriate methods to enable basic interaction
 * with the represented client widget. This class provides access to the WebDriver, to the root WebElement,
 * but everything else should be handled by subclasses, or can be dealt with by dealing directly with the
 * WebElement.
 * <p/>
 * Each GwtWidget may declare a corresponding {@link GwtWidgetFinder} type. This should have a no-arg,
 * public constructor so it can be automatically created, and should follow the basic builder pattern to 
 * enable finding that type of widget in predictable ways.
 * <p/>
 * The {@code find} methods in this class are used to find some widget using that widget's finder type.
 * <p/>
 * A GwtWidget subclass may be annotated with {@code @ForWidget}to indicate a specific widget
 * type that it represents. This is used as a check, to make sure that the dom element the GwtWidget wraps
 * is connected to the right type of Widget.
 * <p/>
 * The {@link #is(Class)} and {@link #as(Class)} methods are modeled after GWT's Element classes, and their
 * checks that any given JavaScriptObject is indeed of the type expected, as a JavaScriptObject can be of
 * any of any type and cast arbitrarily. In the same way, a GwtWidget might be mapped onto any element or
 * widget, even if it doesn't map. The {@link #is(Class)} method allows a test to verify that it is of the
 * type specified, and the {@link #as(Class)} method creates an object of the specified type by wrapping 
 * the current element and driver in the new type. This is usually not required when working with a
 * {@link GwtWidgetFinder} and the {@code find} methods, as those typically only return elements that already
 * match the widget.
 *
 */
@ForWidget(Widget.class)
public class GwtWidget<F extends GwtWidgetFinder<?>> {
	private final WebDriver driver;
	private final WebElement element;

	/**
	 * Creates a new GwtWidget type. Subclasses must invoke this with non-null arguments, and must declare
	 * (at least) a constructor accepting the same arguments.
	 * @param driver a webdriver instance to use when interacting with the widget or finding other widgets
	 * @param element the element to wrap with a GwtDriver instance
	 */
	public GwtWidget(WebDriver driver, WebElement element) {
		assert element != null && driver != null;
		this.driver = driver;
		this.element = element;
		
		assert is(getClass()) : "";
	}

	/**
	 * Gets the root WebElement being used as a GWT Widget
	 * @return the root element of this widget
	 */
	public WebElement getElement() {
		return element;
	}
	/**
	 * Gets the current webdriver instance used to interact with this widget. Not typically used except
	 * internally to avoid passing in a driver in instance methods.
	 * @return the driver used when creating this widget
	 */
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * Starting point to find a widget using the finder dsl - pass in the GwtWidget subclass and an instance
	 * of its basic finder will be created, with the current element and driver to act as a starting point.
	 * <p/>
	 * A find() call is followed by specific invocations on that finder to specify exactly what is being
	 * found, and is followed by a {@code done()} call, which then tries to find the actual widget.
	 * 
	 * @see GwtWidgetFinder
	 * @param widgetType the type of widget to start finding
	 * @return a finder able to return that type of widget when done() is invoked
	 */
	public <W extends GwtWidget<T>, T extends GwtWidgetFinder<W>> T find(Class<W> widgetType) {
		return find(widgetType, getDriver(), getElement());
	}
	/**
	 * Static helper method to enable finding without an initial widget to start from. Useful to start 
	 * writing a test or to find some other high level widget to work with.
	 * <p/>
	 * This method takes no WebElement, which usually means that either the finder will find its own, or
	 * that {@link GwtWidgetFinder#withElement(WebElement)} will be invoked.
	 * 
	 * @see #find(Class)
	 * @see #find(Class, WebDriver, WebElement)
	 * @param widgetType
	 * @param driver the WebDriver instance to use when finding
	 * @return a finder able to return that type of widget when done() is invoked.
	 */
	public static <W extends GwtWidget<T>, T extends GwtWidgetFinder<W>> T find(Class<W> widgetType, WebDriver driver) {
		return find(widgetType, driver, null);
	}
	/**
	 * Static helper method to enable finding without an initial widget to work from. Useful to start
	 * writing a test or to find some other high level widget to work with.
	 * 
	 * @see #find(Class)
	 * @param widgetType the type of widget to start finding
	 * @param driver the WebDriver instance to use when finding
	 * @param element the element to start finding from
	 * @return a finder able to return that type of widget when done() is invoked
	 */
	public static <W extends GwtWidget<T>, T extends GwtWidgetFinder<W>> T find(Class<W> widgetType, WebDriver driver, WebElement element) {
		Type i = widgetType;
		do {
			if (i instanceof ParameterizedType) {
				ParameterizedType t = (ParameterizedType) i;
				if (t.getRawType() == GwtWidget.class) {
					@SuppressWarnings("unchecked")
					Class<T> finderType = (Class<T>) t.getActualTypeArguments()[0];

					T instance = createInstance(finderType);
					if (instance != null) {
						instance.withDriver(driver);
						instance.withElement(element);
						return instance;
					}
				}
			}
			i = (i instanceof Class) ? ((Class<?>)i).getGenericSuperclass() : null;
		} while (i != null);
		return null;
	}

	private static <T extends GwtWidgetFinder<?>> T createInstance(Class<T> type) {
		try {
			return type.newInstance();
		} catch (IllegalAccessException e) {
			// type is not public
			e.printStackTrace();
		} catch (InstantiationException e) {
			// type is abstract, or type doesn't have a no-arg ctor
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Declares that the annotated type is a GwtWidget for a particular Widget subclass. Allows
	 * multiple GwtWidget models to target the same client widget type, adding different features.
	 *
	 */
	@Documented
	@Inherited
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ForWidget {
		Class<? extends Widget> value();
	}

	/**
	 * Re-wraps the widget's element as some other kind of widget. Requires that the actual widget
	 * matches the provided class's {@literal @}ForWidget annotation.
	 * 
	 * @param clazz the type of widget to create
	 * @return a new instance of the class wrapping the current element and driver.
	 * @throws IllegalArgumentException if the element is not of the specified type
	 */
	public <W extends GwtWidget<?>> W as(Class<W> clazz) {
		if (!is(clazz)) {
			ForWidget widgetType = clazz.getAnnotation(ForWidget.class);
			throw new IllegalArgumentException("Cannot complete as(" + clazz.getSimpleName() + ".class), element isn't a " + widgetType.value().getName());
		}
		try {
			return clazz.getConstructor(WebDriver.class, WebElement.class).newInstance(getDriver(), getElement());
		} catch (NoSuchMethodException ex) {
			//w doesn't have a WebDriver,WebElement ctor
			ex.printStackTrace();
		} catch (InstantiationException e) {
			//w is abstract
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			//w's ctor is protected
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//ctor params don't match
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			//w's ctor throew an exception
			e.printStackTrace();
		} catch (SecurityException e) {
			//security manage says no
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if this widget is of the specified type and so can 
	 * @param clazz the type of widget to test against
	 * @return true if this instance wraps an element that 
	 */
	public <W extends GwtWidget<?>> boolean is(Class<W> clazz) {
		ForWidget widgetType = clazz.getAnnotation(ForWidget.class);
		if (widgetType == null) {
			throw new IllegalArgumentException("Class " + clazz + " is not annotated with ForWidget, cannot check its type");
		}
		ExportedMethods m = ClientMethodsFactory.create(ExportedMethods.class, driver);
		String is = m.instanceofwidget(element, widgetType.value().getName());
		
		return is.equals("true");//other values are false (i.e. wrong widget type), or null (i.e. not a widget)
	}

	/**
	 * Helper method to generate a string literal that can be used in an xpath
	 * @param str a string to be escaped for use as an xpath expression
	 * @return a properly escaped string 
	 */
	protected static String escapeToString(String str) {
		if (!str.contains("'")) {
			return "'" + str + "'";
		}
		if (!str.contains("\"")) {
			return "\"" + str + "\"";
		}
		return "concat('" + str.replace("\'", "',\"'\",'") +"')";
	}
}
