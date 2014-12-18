/*   
 * Copyright 2014 Parsons Brinckerhoff

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License
   *
   */

package org.sandag.popsyn.popGenerator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jppf.client.JPPFJob;
import org.jppf.client.event.TaskResultEvent;
import org.jppf.client.event.TaskResultListener;
import org.jppf.client.persistence.JobPersistence;
import org.jppf.client.persistence.JobPersistenceException;
import org.jppf.server.protocol.JPPFTask;


public class MyResultListener implements TaskResultListener {
	
    private transient Logger logger = Logger.getLogger(MyResultListener.class);

    protected int count;
	protected int pendingCount = 0;
	protected final JPPFJob job;
	
	
	// Initialize this collector with the specified job
	public MyResultListener(JPPFJob job) {
		this.job = job;
		count = job.getTasks().size() - job.getResults().size();
		pendingCount = count;
	}
	
	
	// Called to notify that the results of a number of tasks
	// have been received from the server
	public synchronized void resultsReceived(TaskResultEvent event) {
		
		if (event.getThrowable() == null) {
			
			List<JPPFTask> tasks = event.getTaskList();
			
			for ( JPPFTask task : tasks ) {
				
		    	// if the task execution resulted in an exception
		    	Exception e = task.getException();
				if ( e != null ) {
					logger.error( "Exception returned instead of result object for " + task.getId(), e );
					throw new RuntimeException();
				}
				
				logger.info ( task.getId() + ", " + (count - pendingCount + 1) + " of " + count + " completed." );
				pendingCount--;
			}

			// update the job's results
			job.getResults().putResults(tasks);
			
			// notify the threads waiting in waitForResults()
			notifyAll();

			// store the results if a persistence manager is present
			if (job.getPersistenceManager() != null) {
				JobPersistence<Object> pm = job.getPersistenceManager();
				try {
					pm.storeJob(pm.computeKey(job), job, tasks);
				}
				catch (JobPersistenceException e) {
					e.printStackTrace();
				}
			}
			
		}
		else {
			// reset this object's state to prepare for job resubmission
			count = job.getTasks().size() - job.getResults().size();
			pendingCount = count;
		}
		
	}

	
	// Wait until all results of a request have been collected
	public synchronized List<JPPFTask> waitForResults() {
		while (pendingCount > 0) {
			try {
				wait();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		return getResults();
	}

	
	// Get the list of final results
	public List<JPPFTask> getResults() {
		return new ArrayList<JPPFTask>(job.getResults().getAll());
	}
	
}
