package org.mycocosm.framework.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.mycocosm.framework.utils.ExceptionsHelper;

public class BatchHelper {

	public static final String IGNORE_ORGANISMS_PROPERTY="gov.doe.jgi.portal.framework.IgnoreOrganisms";

	private static final void init() {
//		LogConfig.reloadBatchLoggingConfiguration();
//		System.setProperty(CliHelper.BATCH_PROPERTY, Boolean.TRUE.toString());
//		CdiHelper.initSeContainer();
	}

	private static final void preProcess(Object processOrClassOrName) {
//		Logger logger = LoggerFactory.getLogger(BatchHelper.class);
//		config.initBeansSynchronous();
//		boolean needToLoadOrganisms = false;
//		Object processOrClass;
//		if (String.class.isAssignableFrom(processOrClassOrName.getClass())) {
//			processOrClass = config.getOrCreateBean((String)processOrClassOrName);
//		} else {
//			processOrClass = processOrClassOrName;
//		}
//		if (processOrClass!=null) {
//			if (OrganismsAware.class.isAssignableFrom(processOrClass.getClass())) {
//				needToLoadOrganisms = !((OrganismsAware)processOrClass).ignoreOrganisms();
//			} else if (Class.class.isAssignableFrom(processOrClass.getClass())) {
//				needToLoadOrganisms = !ignoreOrganisms((Class<?>)processOrClass);
//			} else {
//				needToLoadOrganisms = !ignoreOrganisms(processOrClass.getClass());
//			}
//			if (needToLoadOrganisms) {
//				logger.log(Level.INFO,"Reloading db organisms");
//				config.reloadDBOrganisms(null,true,false,false);
//				logger.log(Level.INFO,"Reloading db organisms done");
//			} else {
//				logger.log(Level.INFO,"NO reloading of db organisms");
//			}
//		} else {
//			String message = "Process or class '"+processOrClassOrName+"' not exists";
//			logger.log(Level.SEVERE,message);
//			throw new IllegalArgumentException(message);
//		}
	}


	private static final void postProcess() {
//		if (!PortalConfig.isStopped()) {
//			ThreadLifecycleAwareHelper.threadEnd();
//			CdiHelper.shutdownSeContainer();
//		}
	}

//	public static final void setIgnoreOrganisms(boolean ignore) {
//		System.setProperty(IGNORE_ORGANISMS_PROPERTY, String.valueOf(ignore));
//	}

//	public static void runInContainer(BatchRunnableMain process, String[] args){
//		init();
//		ErrorExitException error=null;
//		try {
//			preProcess(process);
//			process.runBatch(args);
//		} catch (Exception e) {
//			if (ExceptionsHelper.getRootCause(e) instanceof ErrorExitException) {
//				error = (ErrorExitException) e.getCause();
//			} else {
//				throw e;
//			}
//		} finally {
//			postProcess();
//		}
//		if (error!=null) {
//			if (error.message!=null) {
//				System.err.println(error.message);
//			}
//			System.exit(error.errorCode);
//		}
//	}
	public static void runCli(BatchRunnableCli process, String[] args) throws ParseException {
		init();
		ErrorExitException error=null;
		CommandLine cmd = CliHelper.parseCli(process, args);
		try {
			preProcess(process);
			process.runBatch(cmd);
		} catch (Exception e) {
			if (ExceptionsHelper.getRootCause(e) instanceof ErrorExitException) {
				error = (ErrorExitException) e.getCause();
			} else {
				throw e;
			}
		} finally {
			postProcess();
		}
		if (error!=null) {
			if (error.message!=null) {
				System.err.println(error.message);
			}
			System.exit(error.errorCode);
		}
	}

	public static Option createOption(String shortName, String fullName, boolean hasArgument, String description, boolean required) {
		return CliHelper.createOption(shortName, fullName, hasArgument, description, required);
	}
	public static Option createOption(String shortName, String fullName, String description, int nArg) {
		return CliHelper.createOption(shortName, fullName, description, nArg);
	}

//
//	private static final String collectArguments(String[] arguments) {
//		StringBuilder ret = new StringBuilder();
//		if (arguments!=null) {
//			Iterator<String> it = CollectionsHelper.asList(arguments).iterator();
//			while (it.hasNext()) {
//				String next = it.next();
//				if (!TextHelper.isNullOrEmpty(next)) {
//					ret.append(next);
//				}
//				if (it.hasNext()) {
//					ret.append(' ');
//				}
//			}
//		}
//		return ret.toString();
//	}
//
//	private static final boolean isDebugRealm() {
//		String realm = System.getProperty("gov.doe.jgi.portal.bootstrap.realm");
//		if (!TextHelper.isNullOrEmpty(realm)) {
//			return realm.toLowerCase().contains("debug");
//		} else {
//			return false;
//		}
//	}
//	
//	public static final boolean isCurrentRevisionSameAsWasDuringBoot() {
//		try {
//			Class<?> batchBootstrapClass = ReflectionHelper.getClass("gov.doe.jgi.portal.bootstrap.BatchBootstrap");
//			return (boolean)ReflectionHelper.evaluateMethod(batchBootstrapClass, null, "isCurrentRevisionSameAsWasDuringBoot", new Object[0]);
//		} catch (ClassNotFoundException e) {
//			// Not called by BatchBootstrap
//			return true;
//		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
//			LoggerFactory.logException(BatchHelper.class, Level.WARNING, e, "Ignored exception while checking for realm");
//			return true;
//		}
//	}	
//
//	public static final String getBootsrap(Class<?> batchRunnableClass, String... arguments) {
//		if (isDebugRealm()) {
//			return getBootsrap(batchRunnableClass, BOOTSRTAP_SUFFIX_NODEBUG, arguments);
//		} else {
//			return getBootsrap(batchRunnableClass, BOOTSRTAP_SUFFIX, arguments);
//		}
//	}
//
//	public static final String getBootsrapLomem(Class<?> batchRunnableClass, String... arguments) {
//		return getBootsrap(batchRunnableClass, BOOTSRTAP_SUFFIX_LOMEM, arguments);
//	}
//
//	private static final String getBootsrap(Class<?> batchRunnableClass, String suffix, String... arguments) {
//		StringBuilder ret = new StringBuilder();
//		Path bootstrapPath = getBootstrapPath(BOOTSRTAP_PREFIX+suffix);
//		ret.append(bootstrapPath.toString());
//		ret.append(' ');
//		ret.append(batchRunnableClass.getName());
//		String params = collectArguments(arguments);
//		if (!TextHelper.isNullOrEmpty(params)) {
//			ret.append(' ');
//			ret.append(params);
//		}
//		return ret.toString();
//	}	
//	private static final Pattern SPACE = Pattern.compile("\\s");
//	public static final void appendNotNull(List<String> args, String name, Object obj) {
//		if (obj!=null) {
//			args.add(name);
//			String str = obj.toString();
//			if (SPACE.matcher(str).find()) {
//				args.add("'"+str+"'");
//			} else {
//				args.add(str);
//			}
//		}
//	}
}
