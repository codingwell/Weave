package net.codingwell.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool
{
	ExecutorService newThreadPool( int poolSize, long keepAliveTime, TimeUnit unit )
	{
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor tpe = new ThreadPoolExecutor( poolSize, poolSize, keepAliveTime, unit, queue );
		tpe.allowCoreThreadTimeOut(true);
		return tpe;
	}
}
