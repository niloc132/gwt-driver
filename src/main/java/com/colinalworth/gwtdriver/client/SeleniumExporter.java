package com.colinalworth.gwtdriver.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class SeleniumExporter {
	public static final void export() {
		export(GWT.getModuleName());
	}
	private static native void export(String moduleName) /*-{
		$wnd['_' + moduleName + '_se'] = $entry(function() {
			@com.colinalworth.gwtdriver.client.SeleniumExporter::invoke(*)(arguments);
		});
	}-*/;
	private final static class Callback extends JavaScriptObject {
		protected Callback() {
		}
		public native void invoke(String result) /*-{
//			debugger;
			this(result);
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
				
				if (method.equals("getClass")) {
					Object obj = get(args, 1);
					callback.invoke(obj.getClass().getName());
				} else if (method.equals("instanceof")) {
					String type = (args.<JsArrayString>cast().get(1));
					Object instance = (get(args, 2));
					
					Class<?> currentType = instance.getClass();
					while (currentType != null && !currentType.getName().equals("java.lang.Object")) {
						if (type.equals(currentType.getName())) {
							callback.invoke("true");
							return;
						}
						currentType = currentType.getSuperclass();
					}
					callback.invoke("false");
				} else if (method.equals("instanceofwidget")) {
					String type = (args.<JsArrayString>cast().get(1));
					Element elt = args.get(2).cast();
					
					Object instance = DOM.getEventListener(elt);
					Class<?> currentType = instance.getClass();
					while (currentType != null && !currentType.getName().equals("java.lang.Object")) {
						if (type.equals(currentType.getName())) {
							callback.invoke("true");
							return;
						}
						currentType = currentType.getSuperclass();
					}
					callback.invoke("false");
				}
			}
			
			@Override
			public void onFailure(Throwable reason) {
				callback.invoke("Call failed: " + reason.getMessage());
			}
		});
	}
}
