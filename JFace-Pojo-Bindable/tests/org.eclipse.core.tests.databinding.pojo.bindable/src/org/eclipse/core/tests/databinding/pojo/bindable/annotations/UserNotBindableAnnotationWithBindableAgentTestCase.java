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
package org.eclipse.core.tests.databinding.pojo.bindable.annotations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import junit.framework.TestCase;

import org.eclipse.core.databinding.pojo.bindable.BindableAware;
import org.eclipse.core.databinding.pojo.bindable.BindableHelper;
import org.eclipse.core.databinding.pojo.bindable.annotation.Bindable;
import org.eclipse.core.tests.databinding.pojo.bindable.domain.annotations.UserNotBindable;

/**
 * Use Bindable Java Agent and {@link Bindable} annotation set to false into
 * POJO Class. Call this test with JVM Parameters
 * 
 * -javaagent:lib/org.eclipse.core.databinding.pojo.bindable_1.0.0.jar
 * -Dbindable.packages=org.eclipse.core.tests.databinding.pojo.bindable.domain.
 * annotations -Dbindable.use_annotation=true
 * 
 * See the launch/UserNotBindableAnnotationWithBindableAgentTestCase.launch
 * 
 */
public class UserNotBindableAnnotationWithBindableAgentTestCase extends
		TestCase {

	private PropertyChangeEvent testEvent = null;;

	/**
	 * Test with Bindable(value=false) into Class.
	 */
	public void testNameBindableAware() {

		UserNotBindable user = new UserNotBindable();

		// Instrumentation add BindableAware interface
		assertEquals(true, user instanceof BindableAware);
		assertEquals(true, BindableHelper.isBindableAware(user));

		BindableAware userBindable = (BindableAware) user;

		// Instrumentation was done with successfull, add Listener
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				System.out.println("---------- Property User changed --------");
				System.out.println("  PropertyName=" + event.getPropertyName());
				System.out.println("  OldValue=" + event.getOldValue());
				System.out.println("  NewValue=" + event.getNewValue());
				System.out
						.println("------------------------------------------");
				testEvent = event;
			}
		};

		// addPropertyChangeListener and change user name
		userBindable.addPropertyChangeListener("name", listener);
		/*
		 * Same code like reflection : try { Method m =
		 * user.getClass().getMethod("addPropertyChangeListener", String.class,
		 * PropertyChangeListener.class); m.invoke(user, "login", listener); }
		 * catch (Exception e) {
		 * 
		 * }
		 */
		user.setName("Name 1");
		assertNull(testEvent);
		testEvent = null;

		// Change user name
		user.setName("Name 2");
		assertNull(testEvent);
		testEvent = null;

		// removePropertyChangeListener and change user name
		userBindable.removePropertyChangeListener("name", listener);
		/*
		 * Same code like reflection : try { Method m =
		 * user.getClass().getMethod("removePropertyChangeListener",
		 * String.class, PropertyChangeListener.class); m.invoke(user, "login",
		 * listener); } catch (Exception e) { }
		 */
		user.setName("Name 3");
		assertNull(testEvent);

		// addPropertyChangeListener and change user name
		userBindable.addPropertyChangeListener("name", listener);
		user.setName("Name 3");
		assertNull(testEvent); // No change

		// Change user name
		user.setName("Name 4");
		assertNull(testEvent);
		testEvent = null;

	}

	/**
	 * Test with Bindable(value=false) into Class and Bindable(value=true) into
	 * setLogin method.
	 */
	public void testLoginBindableAware() {

		UserNotBindable user = new UserNotBindable();

		// Instrumentation add BindableAware interface
		assertEquals(true, user instanceof BindableAware);
		BindableAware userBindable = (BindableAware) user;

		// Instrumentation was done with successfull, add Listener
		PropertyChangeListener listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				System.out.println("---------- Property User changed --------");
				System.out.println("  PropertyName=" + event.getPropertyName());
				System.out.println("  OldValue=" + event.getOldValue());
				System.out.println("  NewValue=" + event.getNewValue());
				System.out
						.println("------------------------------------------");
				testEvent = event;
			}
		};

		// addPropertyChangeListener and change user login
		userBindable.addPropertyChangeListener("login", listener);
		/*
		 * Same code like reflection : try { Method m =
		 * user.getClass().getMethod("addPropertyChangeListener", String.class,
		 * PropertyChangeListener.class); m.invoke(user, "login", listener); }
		 * catch (Exception e) {
		 * 
		 * }
		 */
		user.setLogin("Login 1");
		assertNotNull(testEvent);
		assertNull(testEvent.getOldValue());
		assertEquals("Login 1", testEvent.getNewValue());
		testEvent = null;

		// Change user login
		user.setLogin("Login 2");
		assertNotNull(testEvent);
		assertEquals("Login 1", testEvent.getOldValue());
		assertEquals("Login 2", testEvent.getNewValue());
		testEvent = null;

		// removePropertyChangeListener and change user name
		userBindable.removePropertyChangeListener("login", listener);
		/*
		 * Same code like reflection : try { Method m =
		 * user.getClass().getMethod("removePropertyChangeListener",
		 * String.class, PropertyChangeListener.class); m.invoke(user, "login",
		 * listener); } catch (Exception e) { }
		 */
		user.setLogin("Login 3");
		assertNull(testEvent);

		// addPropertyChangeListener and change user name
		userBindable.addPropertyChangeListener("login", listener);
		user.setLogin("Login 3");
		assertNull(testEvent); // No change

		// Change user name
		user.setLogin("Login 4");
		assertNotNull(testEvent);
		assertEquals("Login 3", testEvent.getOldValue());
		assertEquals("Login 4", testEvent.getNewValue());
		testEvent = null;

	}

}
