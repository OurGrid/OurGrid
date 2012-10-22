/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.ourgrid.common.jdlTests.WorkerTest;
import org.ourgrid.common.spec.job.JobSpecTest;
import org.ourgrid.common.spec.job.TaskSpecTest;
import org.ourgrid.common.spec.main.JDF2JDLTest;
import org.ourgrid.common.spec.main.JDLCompilerTest;
import org.ourgrid.common.spec.main.JDLSemanticAnalyzerTest;
import org.ourgrid.common.spec.main.JDLSyntacticalAnalyzerTest;
import org.ourgrid.common.spec.main.JDLTagsPublisherTest;
import org.ourgrid.common.spec.main.SDFClassAdSemanticalAnalyzerTest;
import org.ourgrid.common.spec.main.SDFClassAdSyntacticalAnalyzerTest;
import org.ourgrid.peer.controller.allocation.PriorityProcessorTest;
import org.ourgrid.peer.controller.matcher.jdl.NewMatcherImplTest;
import org.ourgrid.peer.jdl.WorkerSpecTest;

/**
 * since 21/08/2007
 */

@RunWith(Suite.class)  
@SuiteClasses({
	WorkerTest.class,
	JobSpecTest.class,
	TaskSpecTest.class,
	JDLCompilerTest.class,
	JDLSemanticAnalyzerTest.class,
	JDLSyntacticalAnalyzerTest.class,
	JDLTagsPublisherTest.class,
	SDFClassAdSemanticalAnalyzerTest.class,
	SDFClassAdSyntacticalAnalyzerTest.class,
	PriorityProcessorTest.class,
	NewMatcherImplTest.class,
	WorkerSpecTest.class,
	JDF2JDLTest.class
	})  
public class AllJDLUnitTests {
}
