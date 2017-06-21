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

package org.apache.sysml.conf;

import org.apache.hadoop.mapred.JobConf;

public class HadoopConfigurationManager {
	/** Global cached job conf for read-only operations */
	private static JobConf _rJob = new JobConf();

	/**
	 * Obtain a cached JobConf object, intended for global use by all operations
	 * with read-only access to job conf. This prevents reading the hadoop conf
	 * files over and over again.
	 * 
	 * @return the cached JobConf
	 */
	public static JobConf getCachedJobConf() {
		return _rJob;
	}

	public static void setCachedJobConf(JobConf job) {
		_rJob = job;
	}

}
