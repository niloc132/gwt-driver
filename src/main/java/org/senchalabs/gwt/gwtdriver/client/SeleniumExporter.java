package org.senchalabs.gwt.gwtdriver.client;

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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import org.senchalabs.gwt.gwtdriver.invoke.ExportedMethods;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class SeleniumExporter implements EntryPoint {
	public @interface MethodsFor {
		Class<?> value();
	}
	public @interface Method {
		String value();
	}
	private static final Logger logger = Logger.getLogger(SeleniumExporter.class.getName());
	private static final Map<String, Function> functions = new HashMap<String, Function>();

	@Override
	public void onModuleLoad() {
		export(GWT.getModuleName());
		exportRegisteredTypes();
	}
	@MethodsFor(ExportedMethods.class)
	public static class DefaultExportedMethods {
		@Method("isWidget")
		public boolean isWidget(Element elt) {
			EventListener listener = DOM.getEventListener(elt);
			return listener instanceof Widget;
		}
		@Method("instanceofwidget")
		public boolean instanceofwidget(Element elt, String type) {
			Object instance = DOM.getEventListener(elt);
			if (instance == null) {
				return false;
			}
			return isOfType(type, instance);
		}
		@Method("getContainingWidgetClass")
		public String getContainingWidgetClass(Element elt) {
			EventListener listener = DOM.getEventListener(elt);
			while (listener instanceof Widget == false) {
				if (elt == null) {
					return null;
				}
				elt = elt.getParentElement().cast();
				listener = DOM.getEventListener(elt);
			}
			return listener.getClass().getName();
		}
		@Method("getContainingWidgetEltOfType")
		public Element getContainingWidgetEltOfType(Element elt, String type) {
			EventListener listener = DOM.getEventListener(elt);
			while (listener instanceof Widget == false) {
				if (elt == null) {
					return null;
				}
				elt = elt.getParentElement().cast();
				if (elt == elt.getOwnerDocument().cast()) {
					return null;
				}
				listener = DOM.getEventListener(elt);
			}
			//found a real widget
			Widget w = (Widget) listener;
			while (w != null && !isOfType(type, w)) {
				w = w.getParent();
			}
			return w == null ? null : w.getElement();
		}
	}
	protected abstract void exportRegisteredTypes();

	private static boolean isOfType(String type, Object instance) {
		Class<?> currentType = instance.getClass();
		while (currentType != null && !currentType.getName().equals(Object.class.getName())) {
			if (type.equals(currentType.getName())) {
				return true;
			}
			currentType = currentType.getSuperclass();
		}
		return false;
	}
	public static void registerFunction(String declaringType, String methodName, Function func) {
		functions.put(declaringType + "::" + methodName, func);
	}
	private static native void export(String moduleName) /*-{
		$wnd['_' + moduleName + '_se'] = $entry(function() {
			@org.senchalabs.gwt.gwtdriver.client.SeleniumExporter::invoke(*)(arguments);
		});
	}-*/;
	private final static class Callback extends JavaScriptObject {
		protected Callback() {
		}
		public native void invoke(Object result) /*-{
			this(['success', result]);
		}-*/;
		public native void fail(String message) /*-{
			this(['error: ' + message]);
		}-*/;
	}
	private static native Object get(JsArray<?> array, int i) /*-{
		return array[i];
	}-*/;
	static void invoke(final JsArray<?> args) {
		final Callback callback = args.get(args.length() - 1).cast();
		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				final String method = args.<JsArrayString>cast().get(0);
				logger.info("running method: " + method);
				JsArray<?> functionArgs = splice(args);
				if (functions.containsKey(method)) {
					try {
						Object response = functions.get(method).apply(functionArgs);
						logger.info("response ready: " + response);
						callback.invoke(response);
					} catch (Exception e) {
						logger.severe("Error occurred: " + e.getMessage());
						callback.fail("Error occurred: " + e.getMessage());
					}
				} else {
					logger.severe("Method could not be invoked: " + method);
					callback.fail("Error: could not find method '" + method + "'");
				}
			}

			private native JsArray<?> splice(JsArray<?> args) /*-{
				return $wnd.Array.prototype.splice.call(args, 1, args.length - 2);
			}-*/;

			@Override
			public void onFailure(Throwable reason) {
				callback.fail("Call failed: " + reason.getMessage());
			}
		});
	}


	/**
	 * From selenium's JavascriptExecutor:
	 * 
	 *" Script arguments must be a number, a boolean, a String, WebElement, or a List of any
	 * combination of the above. An exception will be thrown if the arguments do not meet these
	 * criteria. The arguments will be made available to the JavaScript via the "arguments"
	 * variable."
	 * 
	 * ...
	 * 
	 * "{@literal @}return One of Boolean, Long, String, List, WebElement, or null."
	 * 
	 */
	public interface Function {
		Object apply(JsArray<?> args);
		
	}
}
