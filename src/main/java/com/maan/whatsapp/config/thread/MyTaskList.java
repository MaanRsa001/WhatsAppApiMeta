package com.maan.whatsapp.config.thread;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyTaskList extends RecursiveTask<Object> {

	private List<Callable<Object>> queue = null;

	private ConcurrentLinkedQueue<Future<Object>> result = null;

	private static final long serialVersionUID = 1L;

	private Logger log = LogManager.getLogger(getClass());

	public MyTaskList(List<Callable<Object>> queue) {
		this.queue = queue;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object compute() {

		if (queue.size() == 1) {
			ExecutorService executor = Executors.newFixedThreadPool(1);
			try {
				Callable<Object> callable = queue.get(0);
				Future<Object> submit = executor.submit(callable);
				while (!(submit.isDone())) {
					Thread.sleep(100);
				}
				result = new ConcurrentLinkedQueue<Future<Object>>();
				result.add(submit);
				return result;
			} catch (Exception e) {
				log.error(e);
			} finally {

				if (executor != null) {
					executor.shutdown();
					try {
						executor.awaitTermination(50L, TimeUnit.SECONDS);
					} catch (Exception e) {
						log.error(e);
					}
				}
			}
		} else {

			int mid = queue.size() / 2;
			MyTaskList left = new MyTaskList(queue.subList(0, mid));
			MyTaskList right = new MyTaskList(queue.subList(mid, queue.size()));

			try {
				left.fork();
			} catch (Exception e) {
				log.error(e);
			}

			// try{right.compute();}catch (Exception e) {}
			/*
			 * if(result==null){ result=new ArrayList<Future<Object>>(); }
			 * result.add((Future<Object>)left.join());
			 */
			// return left.join();
			// invokeAll(left,right);
			/*
			 * int i=0; while(i<queue.size()){ List<Callable<Object>> subList =
			 * queue.subList(i,(i+1) > queue.size()?queue.size():(i+1)); i++; MyTaskList
			 * taskList=new MyTaskList(subList); taskList.fork();
			 * //System.out.println("taskList.join()"+taskList.join()); if(result==null){
			 * result=new ArrayList<Future<Object>>(); }
			 * result.add((Future<Object>)taskList.join()); }
			 */

			ConcurrentLinkedQueue<Object> queue = new ConcurrentLinkedQueue<Object>();
			queue.addAll((ConcurrentLinkedQueue<Object>) right.compute());
			queue.addAll((ConcurrentLinkedQueue<Object>) left.join());

			return queue;
		}
		// taskList.joi
		return result;
	}

}
