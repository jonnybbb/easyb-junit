package org.easyb.junit4;

import org.easyb.domain.Behavior;
import org.junit.Assume.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

public class EasybMethodRoadie {
	private final Behavior fTest;
	private final RunNotifier fNotifier;
	private final Description fDescription;
	private EasybTestMethod fTestMethod;

	public EasybMethodRoadie(Behavior test, EasybTestMethod method, RunNotifier notifier, Description description) {
		fTest= test;
		fNotifier= notifier;
		fDescription= description;
		fTestMethod= method;
	}

	public void run() {
		if (fTestMethod.isIgnored()) {
			fNotifier.fireTestIgnored(fDescription);
			return;
		}
		fNotifier.fireTestStarted(fDescription);
		try {
			long timeout= fTestMethod.getTimeout();
			if (timeout > 0)
				runWithTimeout(timeout);
			else
				runTest();
		} finally {
			fNotifier.fireTestFinished(fDescription);
		}
	}

	private void runWithTimeout(final long timeout) {
		runBeforesThenTestThenAfters(new Runnable() {
		
			public void run() {
				ExecutorService service= Executors.newSingleThreadExecutor();
				Callable<Object> callable= new Callable<Object>() {
					public Object call() throws Exception {
						runTestMethod();
						return null;
					}
				};
				Future<Object> result= service.submit(callable);
				service.shutdown();
				try {
					boolean terminated= service.awaitTermination(timeout,
							TimeUnit.MILLISECONDS);
					if (!terminated)
						service.shutdownNow();
					result.get(0, TimeUnit.MILLISECONDS); // throws the exception if one occurred during the invocation
				} catch (TimeoutException e) {
					addFailure(new Exception(String.format("test timed out after %d milliseconds", timeout)));
				} catch (Exception e) {
					addFailure(e);
				}				
			}
		});
	}
	
	public void runTest() {
		runBeforesThenTestThenAfters(new Runnable() {
			public void run() {
				runTestMethod();
			}
		});
	}

	public void runBeforesThenTestThenAfters(Runnable test) {
		try {
			test.run();
		}  catch (Exception e) {
			throw new RuntimeException("test should never throw an exception to this level");
		} finally {
			//todo  cleanup??;
		}		
	}
	
	protected void runTestMethod() {
		try {
			fTestMethod.invoke(fTest);
			if (fTestMethod.expectsException())
				addFailure(new AssertionError("Expected exception: " + fTestMethod.getExpectedException().getName()));
		} catch (InvocationTargetException e) {
			Throwable actual= e.getTargetException();
			if (actual instanceof AssumptionViolatedException)
				return;
			else if (!fTestMethod.expectsException())
				addFailure(actual);
			else if (fTestMethod.isUnexpected(actual)) {
				String message= "Unexpected exception, expected<" + fTestMethod.getExpectedException().getName() + "> but was<"
					+ actual.getClass().getName() + ">";
				addFailure(new Exception(message, actual));
			}
		} catch (Throwable e) {
			addFailure(e);
		}
	}
	



	protected void addFailure(Throwable e) {
		fNotifier.fireTestFailure(new Failure(fDescription, e));
	}
}

