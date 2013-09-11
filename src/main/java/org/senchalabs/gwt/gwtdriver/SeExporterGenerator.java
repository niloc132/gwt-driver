package org.senchalabs.gwt.gwtdriver;

/*
 * #%L
 * GWT bindings for WebDriver
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayBoolean;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.util.Name;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import org.senchalabs.gwt.gwtdriver.client.SeleniumExporter.Function;
import org.senchalabs.gwt.gwtdriver.client.SeleniumExporter.Method;
import org.senchalabs.gwt.gwtdriver.client.SeleniumExporter.MethodsFor;

import java.io.PrintWriter;
import java.util.List;

/**
 *
 */
public class SeExporterGenerator extends Generator {
	private static final String SELENIUM_METHODS = "gwtdriver.methods";
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		TypeOracle oracle = context.getTypeOracle();

		JClassType jso = oracle.findType(Name.getSourceNameForClass(JavaScriptObject.class));

		JClassType toGenerate = oracle.findType(typeName).isClass();

		String packageName = toGenerate.getPackage().getName();
		String simpleSourceName = toGenerate.getName().replace('.', '_') + "Impl";
		PrintWriter pw = context.tryCreate(logger, packageName, simpleSourceName);
		if (pw == null) {
			return packageName + "." + simpleSourceName;
		}

		ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(packageName, simpleSourceName);
		factory.setSuperclass(typeName);
		SourceWriter sw = factory.createSourceWriter(context, pw);

		List<String> exportedTypes;
		try {
			exportedTypes = context.getPropertyOracle().getConfigurationProperty(SELENIUM_METHODS).getValues();
		} catch (BadPropertyValueException e) {
			logger.log(TreeLogger.Type.ERROR, "Can't find any config property for " + SELENIUM_METHODS + " declared", e);
			throw new UnableToCompleteException();
		}
		sw.println("protected void exportRegisteredTypes() {");
		sw.indent();
		//for each type set up in a config property,
		for (String exportedType : exportedTypes) {
			JClassType toExport = oracle.findType(exportedType);
			if (toExport == null) {
				logger.log(TreeLogger.Type.ERROR, "Cannot find " + exportedType + " be sure it is a valid GWT type");
				throw new UnableToCompleteException();
			}
			MethodsFor refersToType = toExport.getAnnotation(MethodsFor.class);
			if (refersToType == null) {
				logger.log(Type.ERROR, "Type " + exportedType + " is declared as having webdriver methods, but has no @MethodsFor annotation");
				throw new UnableToCompleteException();
			}
			//verify a default ctor - if not, methods must be static
			boolean requireStatic = toExport.getConstructors().length != 0 && toExport.findConstructor(new JType[]{}) == null;
			if (requireStatic) {
				logger.log(Type.INFO, "No default constructor found, all marked methods must be static");
			}
			TreeLogger typeLogger = logger.branch(TreeLogger.Type.DEBUG, "Exporting methods in " + exportedType);
			//iterate through the methods
			for (JMethod m : toExport.getInheritableMethods()) {
				Method refersToMethod = m.getAnnotation(Method.class);
				if (refersToMethod == null) {
					continue;
				}
				TreeLogger methodLogger = typeLogger.branch(Type.DEBUG, "Examining " + m.getName());
				if (requireStatic && !m.isStatic()) {
					typeLogger.log(Type.ERROR, "No default constructor found for " + exportedType + ", can't export instance method" + m.getName());
					typeLogger.log(Type.ERROR, "Either mark the method as static, or ensure there is a default constructor.");
					throw new UnableToCompleteException();
				}
				//verify that the method matches exactly one method in the webdriver side
				//TODO make this a little faster
				String matchingMethod = null;
				for (java.lang.reflect.Method exportableMethod : refersToType.value().getMethods()) {
					if (refersToMethod.value().equals(exportableMethod.getName())) {
						if (matchingMethod != null) {
							methodLogger.log(Type.ERROR, "Multiple methods found that match " + refersToMethod.value());
							throw new UnableToCompleteException();
						}
						matchingMethod = refersToMethod.value();
					}
				}
				if (matchingMethod == null) {
					methodLogger.log(Type.ERROR, "Can't find a method that matches " + refersToMethod.value());
					throw new UnableToCompleteException();
				}

				//emit a registerFunction call wrapping it
				sw.println("registerFunction(\"%1$s\", \"%2$s\", new %3$s() {",
						escape(refersToType.value().getName()),
						escape(matchingMethod),
						Name.getSourceNameForClass(Function.class));
				sw.indent();
				sw.println("public Object apply(%1$s<?> args) {", Name.getSourceNameForClass(JsArray.class));
				sw.indent();
				JType retType = m.getReturnType();
				if (retType.isPrimitive() != null) {
					switch (retType.isPrimitive()) {
						case VOID:
							//do nothing
							break;
						case INT:
						case DOUBLE:
						case BOOLEAN:
							sw.print("return \"\" + ");
							break;
						default:
							methodLogger.log(Type.ERROR, "Can't return primitive " + retType + " from exported method");
							throw new UnableToCompleteException();
					}
				} else if (retType.isClass() != null && retType.getQualifiedSourceName().equals("java.lang.String") ||
						((retType.isClass() != null) && retType.isClass().isAssignableTo(jso)) ||
						((retType.isInterface() != null) && oracle.getSingleJsoImplInterfaces().contains(retType))) {
					sw.print("return ");
				} else {
					methodLogger.log(Type.ERROR, "Can't return non-jso, non-supported primitive " + retType + " from exported method");
					throw new UnableToCompleteException();
				}
				if (m.isStatic()) {
					sw.print(exportedType);
				} else {
					sw.print("%1$s.<%2$s>create(%2$s.class)", GWT.class.getName(), exportedType);
				}
				sw.print(".%1$s(", matchingMethod);
				//iterate through the arguments
				//verify the arg type is legal
				JType[] erasedParameterTypes = m.getErasedParameterTypes();
				for (int i = 0; i < erasedParameterTypes.length; i++) {
					JType type = erasedParameterTypes[i];

					if (type.isPrimitive() != null || type.getQualifiedSourceName().equals("java.lang.String")) {
						//cast uglyness
						sw.print("args.<%2$s>cast().get(%1$d)", i, getJsArray(type));
					} else if (type.isClass() != null && type.isClass().isAssignableTo(jso)) {
						//normal array plus cast() trickery
						sw.print("args.get(%1$d).<%2$s>cast()", i, type.getQualifiedSourceName());
					} else if (type.isInterface() != null && oracle.getSingleJsoImplInterfaces().contains(type.isInterface())) {
						//single jso cast thing
						sw.print("args.get(%1$d).<%2$s>cast()", i, oracle.getSingleJsoImpl(type.isInterface()).getQualifiedSourceName());
					} else {//TODO goktug's magic new jsinterface
						methodLogger.log(Type.ERROR, "Can't handle argument of type " + type);
						throw new UnableToCompleteException();
					}
					if (i != erasedParameterTypes.length - 1) {
						sw.println(",");
					}
				}
				sw.println(");");

				if (m.getReturnType() == JPrimitiveType.VOID) {
					sw.println("return null;");
				}

				sw.outdent();
				sw.println("}");
				sw.outdent();
				sw.println("});");
			}
		}
		sw.outdent();
		sw.println("}");

		sw.commit(logger);

		return factory.getCreatedClassName();	}

	private String getJsArray(JType type) {
		if (type.getQualifiedSourceName().equals("java.lang.String")) {
			return Name.getSourceNameForClass(JsArrayString.class);
		} else if (type == JPrimitiveType.BOOLEAN) {
			return Name.getSourceNameForClass(JsArrayBoolean.class);
		} else if (type == JPrimitiveType.INT || type == JPrimitiveType.DOUBLE) {
			return Name.getSourceNameForClass(JsArrayNumber.class);
		}
		return null;
	}
}
