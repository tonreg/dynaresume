/*******************************************************************************
 * Copyright (c) 2010, Original authors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR <angelo.zerr@gmail.com>
 *******************************************************************************/
package org.eclipse.core.databinding.pojo.bindable.initializer;

import static org.eclipse.core.databinding.pojo.bindable.internal.util.StringUtils.isEmpty;
import static org.eclipse.core.databinding.pojo.bindable.internal.util.StringUtils.isTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.core.databinding.pojo.bindable.BindableStrategy;
import org.eclipse.core.databinding.pojo.bindable.DefaultBindableStrategy;
import org.eclipse.core.databinding.pojo.bindable.initializer.instrument.agent.BindableInitializerAgent;
import org.eclipse.core.databinding.pojo.bindable.initializer.instrument.agent.BindablePackagesRequiredException;
import org.eclipse.core.databinding.pojo.bindable.initializer.osgi.OSGiBindableInitializer;
import org.eclipse.core.databinding.pojo.bindable.logs.Policy;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Base class used in any environnment to initialize Pojo Bindable.
 * 
 * @see {@link BindableInitializerAgent}
 * @see {@link OSGiBindableInitializer}.
 */
public abstract class AbstractBindableInitializer implements
		BindableInitializer, BindableInitializerConstants {

	protected static final String BINDABLE_PROPERTIES_FILE = "bindable.properties";

	/**
	 * Configure Pojo Bindable.
	 */
	public void start() {
		// Get BindableStrategy instance
		BindableStrategy bindableStrategy = getBindableStrategy();

		// Check if BindableStrategy instance is not null.
		checkRequiredBindableStrategy(bindableStrategy);

		// Initialize Pojo Bindable switch the environnment (Java Agent, OSGi
		// context...) by using BindableStrategy instance.
		initialize(bindableStrategy);

		// Trace information about BindableStrategy instance (if debugMode).
		trace(bindableStrategy);
	}

	/**
	 * Throw {@link BindableStrategyRequiredException} if bindableStrategy is
	 * null.
	 * 
	 * @param bindableStrategy
	 */
	protected void checkRequiredBindableStrategy(
			BindableStrategy bindableStrategy) {
		if (bindableStrategy == null) {
			throw new BindableStrategyRequiredException();
		}

	}

	/**
	 * Create instance of {@link BindableStrategy} and configure it.
	 * 
	 * @param packages
	 * @param useAnnotation
	 * @param genBaseDir
	 * @param debugMode
	 * @param slashIt
	 * @return
	 */
	protected BindableStrategy createBindableStrategy(String packages,
			boolean useAnnotation, String genBaseDir, boolean debugMode,
			boolean slashIt) {
		// -Dbindable.packages=<packages name>
		if (isEmpty(packages)) {
			throw new BindablePackagesRequiredException();
		}
		String[] splittedPackages = packages.split(";");

		// Create BindableStrategy and add Bindable ClassFileTransformer to
		// Instrumentation instance.
		DefaultBindableStrategy bindableStrategy = createBindableStrategy(
				splittedPackages, slashIt);
		bindableStrategy.setUseAnnotation(useAnnotation);
		bindableStrategy.setGenBaseDir(genBaseDir);
		bindableStrategy.setDebugMode(debugMode);

		return bindableStrategy;
	}

	/**
	 * Create instance of {@link DefaultBindableStrategy}.
	 * 
	 * @param splittedPackages
	 * @param slashIt
	 * @return
	 */
	protected DefaultBindableStrategy createBindableStrategy(
			String[] splittedPackages, boolean slashIt) {
		return new DefaultBindableStrategy(splittedPackages, slashIt);
	}

	/**
	 * Load BindableStrategy instance from the file bindable.properties.s
	 * 
	 * @return
	 */
	protected BindableStrategy loadBindableStrategyFromBindablePropertiesFile(
			boolean slashIt) {
		BindableStrategy bindableStrategy = null;
		Enumeration<URL> bindableProperties = null;
		ClassLoader cl = getClass().getClassLoader();
		try {
			bindableProperties = cl == null ? ClassLoader
					.getSystemResources(BINDABLE_PROPERTIES_FILE) : cl
					.getResources(BINDABLE_PROPERTIES_FILE);
		} catch (IOException e) {
			Policy.getLog().log(
					new Status(IStatus.ERROR, BindableStrategy.POJO_BINDABLE,
							IStatus.ERROR, "getResources error on "
									+ BINDABLE_PROPERTIES_FILE, e));
			return null;
		}

		while (bindableProperties.hasMoreElements()) {
			URL url = bindableProperties.nextElement();
			// check each file for a bindable.properties property
			bindableStrategy = createBindableStrategyFromBindablePropertiesFile(
					url, slashIt);
			if (bindableStrategy != null) {
				return bindableStrategy;
			}
		}
		if (bindableStrategy == null) {
			// Call this method to throw BindablePackagesRequiredException
			// exception.
			return createBindableStrategy(null, false, null, false, false);
		}
		return bindableStrategy;
	}

	/**
	 * Create {@link BindableStrategy} instance from an URL url of
	 * bindable.properties file.
	 * 
	 * @param url
	 * @param required
	 * @return
	 */
	private BindableStrategy createBindableStrategyFromBindablePropertiesFile(
			URL url, boolean slashIt) {
		Properties bindableProps = new Properties();
		try {
			if (url != null) {
				bindableProps.load(url.openStream());
			}
			// bindable.packages=<packages name>
			String packages = bindableProps.getProperty(BINDABLE_PACKAGES);

			// bindable.use_annotation=<true|false>
			boolean useAnnotation = isTrue(bindableProps
					.getProperty(BINDABLE_USE_ANNOTATION));

			// bindable.gen_basedir=<path of base dir where class
			// transformed must
			// be generated>
			String genBaseDir = bindableProps.getProperty(BINDABLE_GEN_BASEDIR);

			// bindable.debug=><true|false>
			boolean debugMode = isTrue(bindableProps
					.getProperty(BINDABLE_DEBUG));

			BindableStrategy bindableStrategy = createBindableStrategy(
					packages, useAnnotation, genBaseDir, debugMode, slashIt);
			intializeOtherProperties(bindableStrategy, bindableProps);
			return bindableStrategy;
		} catch (IOException e) {
			Policy.getLog().log(
					new Status(IStatus.ERROR, BindableStrategy.POJO_BINDABLE,
							IStatus.ERROR, "getResources error on "
									+ BINDABLE_PROPERTIES_FILE, e));
			return null;
		}
	}

	protected void intializeOtherProperties(BindableStrategy bindableStrategy,
			Properties bindableProps) {
		// Do Nothing

	}

	/**
	 * Trace information about BindableStrategy instance (if debugMode).
	 * 
	 * @param bindableStrategy
	 */
	private void trace(BindableStrategy bindableStrategy) {
		boolean debugMode = bindableStrategy.isDebugMode();
		if (debugMode) {
			// Debug mode, print info about BindableStrategy

			StringBuffer message = buildTrace(bindableStrategy);
			// Trace
			bindableStrategy.log(IStatus.INFO, message.toString(), null);
		}

	}

	/**
	 * Create the message to use to trace configuration of
	 * {@link BindableStrategy}.
	 * 
	 * @param bindableStrategy
	 * @return
	 */
	protected StringBuffer buildTrace(BindableStrategy bindableStrategy) {
		StringBuffer message = new StringBuffer();
		message.append(getHeaderTrace());

		Collection<String> packageNames = bindableStrategy.getPackageNames();
		addParams(message, BINDABLE_PACKAGES, packageNames);

		boolean useAnnotation = bindableStrategy.isUseAnnotation();
		addParam(message, BINDABLE_USE_ANNOTATION, useAnnotation);
		addParam(message, BINDABLE_DEBUG, true);
		String genBaseDir = bindableStrategy.getGenBaseDir();
		addParam(message, BINDABLE_GEN_BASEDIR, genBaseDir);
		addParam(message, BINDABLE_STRATEGY_PROVIDER, System
				.getProperty(BINDABLE_STRATEGY_PROVIDER));

		return message;
	}

	/**
	 * Add list of parameters into message.
	 * 
	 * @param message
	 * @param paramName
	 * @param paramValues
	 */
	protected void addParams(StringBuffer message, String paramName,
			Collection<String> paramValues) {
		addParam(message, paramName, "");
		int p = 0;
		for (String packageName : paramValues) {
			if (p > 0) {
				message.append("\n");
				for (int i = 0; i < paramName.length() + 1; i++) {
					message.append(" ");
				}
			}
			message.append(packageName);
			p++;
		}
	}

	/**
	 * Add parameter into message.
	 * 
	 * @param message
	 * @param paramName
	 * @param paramValue
	 */
	protected void addParam(StringBuffer message, String paramName,
			Object paramValue) {
		message.append("\n");
		message.append(paramName);
		message.append("=");
		message.append(paramValue);
	}

	/**
	 * Initialize Pojo Bindable by using BindableStrategy information.
	 * 
	 * @param bindableStrategy
	 */
	protected abstract void initialize(BindableStrategy bindableStrategy);

	/**
	 * Return the Header to display into log when BindableStrategy is traced.
	 * 
	 * @return
	 */
	protected abstract String getHeaderTrace();

}
