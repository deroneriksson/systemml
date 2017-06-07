/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sysml.test.integration.functions.binary.matrix;

import java.util.HashMap;

import org.junit.Test;

import org.apache.sysml.api.DMLScript;
import org.apache.sysml.api.RuntimePlatform.ExecutionMode;
import org.apache.sysml.lops.LopProperties.ExecType;
import org.apache.sysml.runtime.matrix.data.MatrixValue.CellIndex;
import org.apache.sysml.test.integration.AutomatedTestBase;
import org.apache.sysml.test.integration.TestConfiguration;
import org.apache.sysml.test.utils.TestUtils;

/**
 * 
 */
public class CentralMomentTest extends AutomatedTestBase 
{
	
	private final static String TEST_NAME = "CentralMoment";
	private final static String TEST_DIR = "functions/binary/matrix/";
	private final static String TEST_CLASS_DIR = TEST_DIR + CentralMomentTest.class.getSimpleName() + "/";
	private final static double eps = 1e-10;
	
	private final static int rows = 1871;
	private final static int maxVal = 7; 
	private final static double sparsity1 = 0.65;
	private final static double sparsity2 = 0.05;
	
	
	@Override
	public void setUp() 
	{
		TestUtils.clearAssertionInformation();
		addTestConfiguration(TEST_NAME, new TestConfiguration(TEST_CLASS_DIR, TEST_NAME, new String[] { "R" })   ); 
	}
	
	@Test
	public void testCentralMoment2DenseCP() 
	{
		runCentralMomentTest(2, false, ExecType.CP);
	}
	
	@Test
	public void testCentralMoment3DenseCP() 
	{
		runCentralMomentTest(3, false, ExecType.CP);
	}
	
	@Test
	public void testCentralMoment4DenseCP() 
	{
		runCentralMomentTest(4, false, ExecType.CP);
	}
	
	@Test
	public void testCentralMoment2SparseCP() 
	{
		runCentralMomentTest(2, true, ExecType.CP);
	}
	
	@Test
	public void testCentralMoment3SparseCP() 
	{
		runCentralMomentTest(3, true, ExecType.CP);
	}
	
	@Test
	public void testCentralMoment4SparseCP() 
	{
		runCentralMomentTest(4, true, ExecType.CP);
	}
	
	@Test
	public void testCentralMoment2DenseMR() 
	{
		runCentralMomentTest(2, false, ExecType.MR);
	}
	
	@Test
	public void testCentralMoment3DenseMR() 
	{
		runCentralMomentTest(3, false, ExecType.MR);
	}
	
	@Test
	public void testCentralMoment4DenseMR() 
	{
		runCentralMomentTest(4, false, ExecType.MR);
	}
	
	@Test
	public void testCentralMoment2SparseMR() 
	{
		runCentralMomentTest(2, true, ExecType.MR);
	}
	
	@Test
	public void testCentralMoment3SparseMR() 
	{
		runCentralMomentTest(3, true, ExecType.MR);
	}
	
	@Test
	public void testCentralMoment4SparseMR() 
	{
		runCentralMomentTest(4, true, ExecType.MR);
	}
	
	@Test
	public void testCentralMoment2DenseSP() 
	{
		runCentralMomentTest(2, false, ExecType.SPARK);
	}
	
	@Test
	public void testCentralMoment3DenseSP() 
	{
		runCentralMomentTest(3, false, ExecType.SPARK);
	}
	
	@Test
	public void testCentralMoment4DenseSP() 
	{
		runCentralMomentTest(4, false, ExecType.SPARK);
	}
	
	@Test
	public void testCentralMoment2SparseSP() 
	{
		runCentralMomentTest(2, true, ExecType.SPARK);
	}
	
	@Test
	public void testCentralMoment3SparseSP() 
	{
		runCentralMomentTest(3, true, ExecType.SPARK);
	}
	
	@Test
	public void testCentralMoment4SparseSP() 
	{
		runCentralMomentTest(4, true, ExecType.SPARK);
	}

	/**
	 * 
	 * @param sparseM1
	 * @param sparseM2
	 * @param instType
	 */
	private void runCentralMomentTest( int order, boolean sparse, ExecType et)
	{
		//rtplatform for MR
		ExecutionMode platformOld = rtplatform;
		switch( et ){
			case MR: rtplatform = ExecutionMode.HADOOP; break;
			case SPARK: rtplatform = ExecutionMode.SPARK; break;
			default: rtplatform = ExecutionMode.HYBRID; break;
		}
	
		boolean sparkConfigOld = DMLScript.USE_LOCAL_SPARK_CONFIG;
		if( rtplatform == ExecutionMode.SPARK )
			DMLScript.USE_LOCAL_SPARK_CONFIG = true;
		
		try
		{
			getAndLoadTestConfiguration(TEST_NAME);
			
			String HOME = SCRIPT_DIR + TEST_DIR;
			fullDMLScriptName = HOME + TEST_NAME + ".dml";
			programArgs = new String[]{"-args", input("A"),
				Integer.toString(order), output("R")};
			
			fullRScriptName = HOME + TEST_NAME + ".R";
			rCmd = "Rscript" + " " + fullRScriptName + " " + 
				inputDir() + " " + order + " "+ expectedDir();
	
			//generate actual dataset (always dense because values <=0 invalid)
			double sparsitya = sparse ? sparsity2 : sparsity1;
			double[][] A = getRandomMatrix(rows, 1, 1, maxVal, sparsitya, 7); 
			writeInputMatrixWithMTD("A", A, true);
			
			runTest(true, false, null, -1); 
			runRScript(true); 
			
			//compare matrices 
			HashMap<CellIndex, Double> dmlfile = readDMLMatrixFromHDFS("R");
			HashMap<CellIndex, Double> rfile  = readRMatrixFromFS("R");
			TestUtils.compareMatrices(dmlfile, rfile, eps, "Stat-DML", "Stat-R");
		}
		finally
		{
			rtplatform = platformOld;
			DMLScript.USE_LOCAL_SPARK_CONFIG = sparkConfigOld;
		}
	}

}