package org.mycocosm.framework.reflection;

import java.lang.reflect.Method;

public class MethodDeclaration {
	public final String name;
	public final Class<?>[] parameters;
	public MethodDeclaration(String name, Class<?>... parameters) {
		this.name = name;
		if (parameters!=null) {
			this.parameters = parameters;
		} else {
			this.parameters = new Class[0];
		}
	}
	public Method getMethod(Class<?> clasz) throws NoSuchMethodException, SecurityException {
		return clasz.getDeclaredMethod(name, parameters);
	}
}
