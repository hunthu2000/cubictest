/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class SeleniumClasspathContainerInitializer extends
		ClasspathContainerInitializer {

	public static final String CUBICTEST_SELENIUM = "CUBICTEST_SELENIUM";

	private static final String curVersion = "2.0.6";
	private static final String junitName = "/lib/junit-4.10.jar";
	private static final String junitSrcName = "/lib/junit-4.10-src.jar";
	private static final String exporterName = "/lib/cubictest-exporter-"
			+ curVersion + ".jar";
	private static final String seleniumRCName = "/lib/cubictest-selenium-rc-"
			+ curVersion + ".jar";
	private static final String pluginName = "/lib/cubictest-plugin-"
			+ curVersion + ".jar";

	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {

		final IPath libPath = JavaCore.getClasspathVariable(CUBICTEST_SELENIUM);
		final IClasspathEntry cubicCoreEntry = JavaCore.newLibraryEntry(
				libPath.append(pluginName), null, null);
		final IClasspathEntry cubicSeleniumExporterEntry = JavaCore
				.newLibraryEntry(libPath.append(exporterName),
						libPath.append(exporterName), null);
		final IClasspathEntry cubicSeleniumExporterServerEntry = JavaCore
				.newLibraryEntry(libPath.append(seleniumRCName),
						libPath.append(seleniumRCName), null);
		final IClasspathEntry jUnitEntry = JavaCore.newLibraryEntry(
				libPath.append(junitName), libPath.append(junitSrcName), null);
		JavaCore.setClasspathContainer(new Path(CUBICTEST_SELENIUM),
				new IJavaProject[] { project }, // value for 'myProject'
				new IClasspathContainer[] { new IClasspathContainer() {
					public IClasspathEntry[] getClasspathEntries() {
						return new IClasspathEntry[] { cubicCoreEntry,
								jUnitEntry, cubicSeleniumExporterEntry,
								cubicSeleniumExporterServerEntry };
					}

					public String getDescription() {
						return "CubicTest Selenium Library";
					}

					public int getKind() {
						return IClasspathContainer.K_APPLICATION;
					}

					public IPath getPath() {
						return libPath;
					}
				} }, null);
	}

}
