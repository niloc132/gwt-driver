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

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Tools to get info about the current modules present on the page, with the GwtDriver module
 * inherited.
 *
 */
public class ModuleUtilities {
	/**
	 * Examines the current page for any window with a $moduleName defined on it. Should return
	 * a list of names if any are present - typically will return only one entry, or none if GWT
	 * isn't in use on this page.
	 * 
	 * @param driver
	 * @return a list of module names
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findModules(WebDriver driver) {
		JavascriptExecutor exec = (JavascriptExecutor) driver;

		List<String> modulesPresent = (List<String>) exec.executeScript("var n=[];" +
				"function a(c) {c.$moduleName && window['_' + c.$moduleName + '_se'] && n.push(c.$moduleName)};" +
				"a(window);" +
				"for (var b=0; b<frames.length;b++) {" +
					"a(frames[b]);" +
				"}" +
				"return n;");

		return modulesPresent;
	}

	public static Object executeExportedFunction(String method, WebDriver driver, Object... args) {
		return executeExportedFunction(findModules(driver).get(0), method, driver, args);
	}

	public static Object executeExportedFunction(String module, String method, WebDriver driver, Object... args) {
		System.out.print("running " + method + "(");
		for (Object obj : args) {
			System.out.print(obj + ", ");
		}
		System.out.println(")");
		Object[] allArgs = new Object[args.length + 1];
		allArgs[0] = method;
		System.arraycopy(args, 0, allArgs, 1, args.length);
		Object ret = ((JavascriptExecutor)driver).executeAsyncScript("_"+module+"_se.apply(this, arguments)", allArgs);
		System.out.println("\t" + ret);
		return ret;
	}
}
