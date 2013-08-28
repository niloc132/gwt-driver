package org.senchalabs.gwt.gwtdriver;

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


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Tools to get info about the current modules present on the page, with the GwtDriver module
 * inherited.
 *
 */
public class ModuleUtilities {
	private static final Logger LOGGER = Logger.getLogger(ModuleUtilities.class.getName());
	private static final String NO_DEFAULT_ERROR = "error: no default module";
	public static final String EXPORTED_FUNCTION_SCRIPT = "(!!window.default_module_se)" +
			"?" +
				"window.default_module_se.apply(this, arguments)" +
			":" +
				"arguments[arguments.length-1](['"+NO_DEFAULT_ERROR+"'])";

	/**
	 * Examines the current page for any window with a $moduleName defined on it. Should return
	 * a list of names if any are present - typically will return only one entry, or none if GWT
	 * isn't in use on this page.
	 * 
	 * @param driver
	 * @return a list of module names
	 */
	public static List<String> findModules(WebDriver driver) {
		JavascriptExecutor exec = (JavascriptExecutor) driver;

		@SuppressWarnings("unchecked")
		List<String> modulesPresent = (List<String>) exec.executeScript("var n=[];" +
				"function a(c) {c.$moduleName && window['_' + c.$moduleName + '_se'] && n.push(c.$moduleName)};" +
				"a(window);" +
				"try {" +
					"for (var b=0; b<frames.length;b++) {" +
						"a(frames[b]);" +
					"}" +
				"} catch (ignore) {" +
					//ignore because it means cross domain activity, and that means not a module
				"}" +
				"return n;");

		LOGGER.fine("Found modules:" + modulesPresent);
		return modulesPresent;
	}

	public static Object executeExportedFunction(String method, WebDriver driver, Object... args) {
		if (LOGGER.isLoggable(Level.FINEST)) {
			LOGGER.finest("running " + method + "(");
			for (Object obj : args) {
				LOGGER.finest(obj + ", ");
			}
			LOGGER.finest(")");
		}
		Object[] allArgs = new Object[args.length + 1];
		allArgs[0] = method;
		System.arraycopy(args, 0, allArgs, 1, args.length);
		List<?> ret;
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		ret = (List<?>) executor.executeAsyncScript(EXPORTED_FUNCTION_SCRIPT, allArgs);
		if (ret.size() == 1 && ret.get(0).equals(NO_DEFAULT_ERROR)) {
			//error, try again after wiring up module
			if (setDefaultModule(findModules(driver).get(0), executor)) {
				//success, try one more time...
				ret = (List<?>) executor.executeAsyncScript(EXPORTED_FUNCTION_SCRIPT, allArgs);
			} else {
				throw new RuntimeException("Unable to find a module to talk with - did you add an inherits for SeleniumExporter?");
			}
		}
		if (ret.size() == 1) {
			//error, log and throw
			LOGGER.warning("Error executing script: " + ret.get(0));
			throw new RuntimeException(ret.get(0).toString());
		}

		if (LOGGER.isLoggable(Level.FINEST)) {
			LOGGER.finest("\t" + ret);
		}
		return ret.get(1);
	}

	public static Object executeExportedFunction(String module, String method, WebDriver driver, Object... args) {
		setDefaultModule(module, (JavascriptExecutor) driver);
		return executeExportedFunction(method, driver, args);
	}

	private static boolean setDefaultModule(String module, JavascriptExecutor driver) {
		LOGGER.fine("Setting default module to " + module + " with " + driver);
		return (Boolean) driver.executeScript("return !!(window.default_module_se=window._" + module + "_se);");
	}
}
