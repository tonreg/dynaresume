/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Angelo Zerr <angelo.zerr@gmail.com> - Jetty packages
 *******************************************************************************/
package org.eclipse.jst.server.jetty.core.command;

import org.eclipse.jst.server.jetty.core.IJettyConfigurationWorkingCopy;
import org.eclipse.jst.server.jetty.core.WebModule;
import org.eclipse.jst.server.jetty.core.internal.Messages;
/**
 * Command to remove a web module.
 */
public class RemoveWebModuleCommand extends ConfigurationCommand {
	protected int index;
	protected WebModule module;

	/**
	 * RemoveWebModuleCommand constructor comment.
	 * 
	 * @param configuration a jetty configuration
	 * @param index an index
	 */
	public RemoveWebModuleCommand(IJettyConfigurationWorkingCopy configuration, int index) {
		super(configuration, Messages.configurationEditorActionRemoveWebModule);
		this.index = index;
	}

	/**
	 * Execute the command.
	 */
	public void execute() {
		module = (WebModule) configuration.getWebModules().get(index);
		configuration.removeWebModule(index);
	}

	/**
	 * Undo the command.
	 */
	public void undo() {
		configuration.addWebModule(index, module);
	}
}