package org.senchalabs.gwt.gwtdriver.invoke;

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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sourceforge.htmlunit.corejs.javascript.ConsString;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.senchalabs.gwt.gwtdriver.ModuleUtilities;


/**
 * Allows simple invocation of exported methods from GWT. Must follow the same
 * rules as {@link JavascriptExecutor#executeAsyncScript(String, Object...)} in
 * both the Java/Test and Java/Gwt/Client code in terms of arguments passed.
 *
 */
public class ClientMethodsFactory {
	private static final class InvocationHandlerImplementation implements
	InvocationHandler {
		private final WebDriver driver;
		private final String module;

		private InvocationHandlerImplementation(WebDriver driver, String module) {
			this.driver = driver;
			this.module = module;
		}

		@Override
		public Object invoke(Object instance, Method method, Object[] args) throws Throwable {
			Object ret;
			if (module != null) {
				ret = ModuleUtilities.executeExportedFunction(module, method.getName(), driver, args);
			} else {
				ret = ModuleUtilities.executeExportedFunction(method.getName(), driver, args);
			}
			//normalize string, apparently htmlunit gives us junk values from time to time
			if (ret instanceof ConsString) {
				ret = ret.toString();
			}
			return ret;
		}
	}

	/**
	 * Creates an instance of the ClientMethods type on the first available module.
	 *
	 * @param type
	 * @param driver
	 * @return
	 */
	public static <T extends ClientMethods> T create(Class<T> type, WebDriver driver) {
		return create(type, driver, null);
	}

	/**
	 * Creates an instance of the ClientMethods type on the given module name.
	 * 
	 * @param type
	 * @param driver
	 * @param moduleName
	 * @return
	 */
	public static <T extends ClientMethods> T create(Class<T> type, WebDriver driver, String moduleName) {
		assert driver instanceof JavascriptExecutor;
		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, new InvocationHandlerImplementation(driver, moduleName));
		return proxy;
	}
}
