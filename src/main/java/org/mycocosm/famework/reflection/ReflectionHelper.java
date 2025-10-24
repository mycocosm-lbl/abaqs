package org.mycocosm.famework.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.mycocosm.famework.time.TimeInterval;

public class ReflectionHelper {

	public static final String LIST_DELIMITER = ",";

	@SuppressWarnings("unchecked")
	public static final <T> Class<T> getClass(String name) throws ClassNotFoundException {
		return (Class<T>)Thread.currentThread().getContextClassLoader().loadClass(name);
	}

	@SuppressWarnings("unchecked")
	public static final <T> Class<? extends T> getClass(Class<T> clasz, String name) throws ClassNotFoundException {
		return (Class<? extends T>) Thread.currentThread().getContextClassLoader().loadClass(name);
	}

	public static final <T> T newInstance(Class<T> clasz) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<T> c = clasz.getDeclaredConstructor(new Class[] {});
		return c.newInstance(new Object[] {});
	}

	public static final Object newInstance(String className) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Class<?> clasz = getClass(className);
		Constructor<?> c = clasz.getDeclaredConstructor(new Class[] {});
		return c.newInstance(new Object[] {});

	}

	public static Object evaluateMethod(Object object, String name, Object params[]) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return evaluateMethod(object.getClass(), object, name, params);
	}

	public static final Object evaluateMethod(Class<?> clasz, Object object, String name, Object params[]) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		Class<?> paramsClasses[];
		if (params != null && params.length > 0) {
			paramsClasses = new Class[params.length];
			for (int index = 0; index < params.length; index++) {
				if (params[index] != null) {
					paramsClasses[index] = params[index].getClass();
				} else {
					paramsClasses[index] = Object.class;
				}
			}
		} else {
			paramsClasses = new Class[0];
		}
		try {
			Method method = clasz.getDeclaredMethod(name, paramsClasses);
			Object ret = method.invoke(object, params);
			return ret;
		} catch (NoSuchMethodException e) {
			Class<?> superClass = clasz.getSuperclass();
			if (superClass != null) {
				return evaluateMethod(superClass, object, name, params);
			} else {
				throw e;
			}
		}
	}

	public static final Object evaluateMethod(Class<?> clasz, Object object, String name, Object params[], Class<?> paramsClasses[]) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,	InvocationTargetException, ClassNotFoundException {
		try {
			Method method = clasz.getDeclaredMethod(name, paramsClasses);
			Object ret = method.invoke(object, params);
			return ret;
		} catch (NoSuchMethodException e) {
			Class<?> superClass = clasz.getSuperclass();
			if (superClass != null) {
				return evaluateMethod(superClass, object, name, params);
			} else {
				throw e;
			}
		}
	}

	public static final Object evaluateMethod(Class<?> clasz, Object object, MethodDeclaration method, Object params[]) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return evaluateMethod(clasz, object, method.name, params, method.parameters);
	}	

	public static final boolean hasMethod(Class<?> clasz, Object object, String name, Class<?> paramsClasses[]) {
		try {
			return clasz.getDeclaredMethod(name, paramsClasses) != null;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	public static final Object getField(Class<?> clasz, Object object, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		try {
			Field field = clasz.getDeclaredField(name);
			field.setAccessible(true);
			Object fieldValue = field.get(object);
			return fieldValue;
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clasz.getSuperclass();
			if (superClass != null) {
				return getField(superClass, object, name);
			} else {
				throw e;
			}
		}
	}

	public static final Object getField(Object object, String name) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Class<?> clasz = object.getClass();
		return getField(clasz, object, name);
	}

	public static final boolean isTransient(Field field) {
		return Modifier.isTransient(field.getModifiers());
	}

	public static final boolean isStatic(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	private static final void loadListOfFieldsStartingFromClass(Class<?> clasz, List<Field> lst) {
		for (Field field : clasz.getDeclaredFields()) {
			if (!isTransient(field)) {
				lst.add(field);
			}
		}
		Class<?> superClass = clasz.getSuperclass();
		if (!Object.class.equals(superClass)) {
			loadListOfFieldsStartingFromClass(superClass, lst);
		}
	}

	public static final List<Field> getFields(Object object) {
		List<Field> ret = new ArrayList<>();
		Class<?> clasz = object.getClass();
		loadListOfFieldsStartingFromClass(clasz, ret);
		return ret;
	}

	public static final Object castTo(Object src, Class<?> resultClass) throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		if (src == null) {
			if (resultClass.getName().equals("long")) {
				return 0L;
			} else if (resultClass.getName().equals("int")) {
				return 0;
			} else if (resultClass.getName().equals("float")) {
				return 0.0F;
			} else if (resultClass.getName().equals("double")) {
				return 0.0;
			} else if (resultClass.getName().equals("boolean")) {
				return false;
			} else if (resultClass.getName().equals("short")) {
				return 0;
			} else if (resultClass.getName().equals("char")) {
				return 0;
			} else if (resultClass.getName().equals("byte")) {
				return 0;
			} else {
				return null;
			}
		}
		if (resultClass.isInstance(src)) {
			return resultClass.cast(src);
		} else if (resultClass.getName().equals("long")) {
			return ((Long) castTo(src, Long.class)).longValue();
		} else if (resultClass.getName().equals("int")) {
			return ((Integer) castTo(src, Integer.class)).intValue();
		} else if (resultClass.getName().equals("float")) {
			return ((Float) castTo(src, Float.class)).floatValue();
		} else if (resultClass.getName().equals("double")) {
			return ((Double) castTo(src, Double.class)).doubleValue();
		} else if (resultClass.getName().equals("boolean")) {
			return ((Boolean) castTo(src, Boolean.class)).booleanValue();
		} else if (resultClass.getName().equals("short")) {
			return ((Short) castTo(src, Short.class)).shortValue();
		} else if (resultClass.getName().equals("char")) {
			return ((Character) castTo(src, Character.class)).charValue();
		} else if (resultClass.getName().equals("byte")) {
			return ((Byte) castTo(src, Byte.class)).byteValue();
		} else if (resultClass.getName().equals("java.util.Date")) {
			try {
				DateFormat fmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
				return fmt.parse(src.toString());
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		} else if (resultClass.isAssignableFrom(Path.class)) {
			return Paths.get(src.toString());
		} else if (resultClass.isAssignableFrom(Duration.class)) {
			return TimeInterval.valueOf(src.toString()).toDuration();
		} else if (resultClass.isAssignableFrom(Pattern.class)) {
			return Pattern.compile(src.toString());
		} else {
			Object params[] = new Object[1];
			params[0] = src;
			try {
				return evaluateMethod(resultClass, null, "valueOf", params);
			} catch (NoSuchMethodException e) {
				try {
					Constructor<?> c = resultClass.getConstructor(src.getClass());
					return c.newInstance(src);
				} catch (NoSuchMethodException ee) {
					throw new RuntimeException("Object " + src + " cannot be instatiated because " + resultClass.getName() + " has no constructor with string argument");
				} catch (InstantiationException ee) {
					throw new RuntimeException("Object " + src + " cannot be cast to " + resultClass.getName());
				}
			}
		}
	}

	public static final void setField(Object object, String name, Object value) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {
		Class<?> clasz = getClass(object.getClass().getName());
		setField(clasz, object, name, value);
	}

	public static final void setField(Class<?> clazz, Object object, String name, Object value) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException {

		Field field = getField(clazz, name);
		Class<?> fieldType = field.getType();

		String setter = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
		Class<?> methodParams[] = new Class[1];
		methodParams[0] = fieldType;
		try {
			Method method = clazz.getDeclaredMethod(setter, methodParams);
			method.setAccessible(true);
			method.invoke(object, castTo(value, fieldType));
		} catch (NoSuchMethodException e) {
			field.setAccessible(true);
			field.set(object, castTo(value, fieldType));
		}
		
	}

	@SuppressWarnings("unchecked")
	public static final void setListField(Object containingObject, String fieldName, Object value) throws SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		try {
			Class<?> containingClass = getClass(containingObject.getClass().getName());
			Field field = getField(containingClass, fieldName);
			// get the collection from the containing object
			Collection<Object> resultCollection = null;
			String getter = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			try {
				Method method = containingClass.getDeclaredMethod(getter, new Class[0]);
				resultCollection = (Collection<Object>) method.invoke(containingObject, new Object[0]);
			} catch (NoSuchMethodException e) {
				field.setAccessible(true);
				resultCollection = (Collection<Object>) field.get(containingObject);
			}

			// it must be initialized by the containing object
			if (resultCollection == null) {
				throw new RuntimeException("Error in ReflectionHelper.setCollectionField: " + fieldName + " is null. In order to set a Collection field using" + "this facility, the field must be initialized (as a concrete object) in the containing bean.");
			}

			// get the type for the generic, split the list, cast its items, and
			// add them to collection
			Type fieldType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
			Class<?> fieldClazz = ReflectionHelper.getClass(fieldType.toString().replaceFirst(".+ ", ""));

			String[] itemList = ((String) castTo(value, java.lang.String.class)).split(LIST_DELIMITER);
			for (String item : itemList) {
				resultCollection.add(castTo(item, fieldClazz));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final Field getField(Class<?> clazz, String name) throws SecurityException, NoSuchFieldException, ClassNotFoundException {

		try {
			Field field = clazz.getDeclaredField(name);
			return field;
		} catch (NoSuchFieldException e) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null) {
				return getField(superClass, name);
			} else {
				throw e;
			}
		}
	}

	public static final Class<?> getFieldClass(Class<?> clazz, String name) throws SecurityException, NoSuchFieldException, ClassNotFoundException {
		return getField(clazz, name).getType();
	}

	public static final String dumpAllObjectNonTransientFields(Object obj, char separator, char fieldSeparator) throws IllegalArgumentException, IllegalAccessException {
		StringBuilder ret = new StringBuilder();
		Iterator<Field> fi = getFields(obj).iterator();
		while (fi.hasNext()) {
			Field field = fi.next();
			if (field.canAccess(obj) && !isStatic(field) && !isTransient(field)) {
				ret.append(field.getName());
				ret.append(separator);
				ret.append(field.get(obj).toString());
				if (fi.hasNext()) {
					ret.append(fieldSeparator);
				}
			}
		}
		return ret.toString();
	}

	public static final boolean hasAnnotation(Class<?> clasz, Class<? extends Annotation> annotationClass) {
		return clasz.getAnnotation(annotationClass) != null;
	}

	public static final Iterable<Method> getAllMethodsByAnnotation(Class<?> clasz, Class<? extends Annotation> annotationClass) {
		List<Method> ret = new ArrayList<Method>();
		for (Method method : clasz.getDeclaredMethods()) {
			if (method.getAnnotation(annotationClass) != null) {
				ret.add(method);
			}
		}
		return ret;
	}
}
