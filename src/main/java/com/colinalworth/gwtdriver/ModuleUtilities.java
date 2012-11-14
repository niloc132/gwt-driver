package com.colinalworth.gwtdriver;

import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Tools to get info about the current modules
 * @author colin
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

		return (List<String>) exec.executeScript("var n=[];" +
				"function a(c) {c.$moduleName && n.push(c.$moduleName)};" +
				"a(window);" +
				"for (var b=0; b<frames.length;b++) {" +
					"a(frames[b]);" +
				"}" +
				"return n;");
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
