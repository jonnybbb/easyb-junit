/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.easyb.junit4;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easyb.domain.Behavior;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>
 * SpringJUnit4ClassRunner is a custom extension of {@link org.junit.internal.runners.JUnit4ClassRunner}
 * which provides functionality of the <em>Spring TestContext Framework</em>
 * to standard JUnit 4.4+ tests by means of the {@link org.springframework.test.context.TestContextManager} and
 * associated support classes and annotations.
 * </p>
 * <p>
 * The following list constitutes all annotations currently supported directly
 * by SpringJUnit4ClassRunner.
 * <em>(Note that additional annotations may be supported by various
 * {@link org.springframework.test.context.TestExecutionListener TestExecutionListeners})</em>
 * </p>
 * <ul>
 * <li>{@link org.junit.Test#expected() @Test(expected=...)}</li>
 * <li>{@link org.springframework.test.annotation.ExpectedException @ExpectedException}</li>
 * <li>{@link org.junit.Test#timeout() @Test(timeout=...)}</li>
 * <li>{@link org.springframework.test.annotation.Timed @Timed}</li>
 * <li>{@link org.springframework.test.annotation.Repeat @Repeat}</li>
 * <li>{@link org.junit.Ignore @Ignore}</li>
 * <li>{@link org.springframework.test.annotation.ProfileValueSourceConfiguration @ProfileValueSourceConfiguration}</li>
 * <li>{@link org.springframework.test.annotation.IfProfileValue @IfProfileValue}</li>
 * </ul>
 *
 * @author Sam Brannen
 * @author Juergen Hoeller
 * @see org.springframework.test.context.TestContextManager
 * @since 2.5
 */
public class EasybJUnit4ClassRunner extends JUnit4ClassRunner {

    private static final Log logger = LogFactory.getLog(EasybJUnit4ClassRunner.class);


    /**
     * Constructs a new <code>SpringJUnit4ClassRunner</code> and initializes a
     * {@link org.springframework.test.context.TestContextManager} to provide Spring testing functionality to
     * standard JUnit tests.
     *
     * @param clazz the Class object corresponding to the test class to be run
     */
    public EasybJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug("SpringJUnit4ClassRunner constructor called with [" + clazz + "].");
        }
    }


    @Override
    /**
     * Check whether the test is enabled in the first place. This prevents classes with
     * a non-matching <code>@IfProfileValue</code> annotation from running altogether,
     * even skipping the execution of <code>prepareTestInstance</code> listener methods.
     * @see org.springframework.test.annotation.IfProfileValue
     * @see org.springframework.test.context.TestExecutionListener
     */
    public void run(RunNotifier notifier) {

        super.run(notifier);
    }

    /**
     * Delegates to {@link org.junit.internal.runners.JUnit4ClassRunner#createTest()} to create the test
     * instance and then to a {@link org.springframework.test.context.TestContextManager} to
     * {@link org.springframework.test.context.TestContextManager#prepareTestInstance(Object) prepare} the test
     * instance for Spring testing functionality.
     *
     * @see org.junit.internal.runners.JUnit4ClassRunner#createTest()
     * @see org.springframework.test.context.TestContextManager#prepareTestInstance(Object)
     */
    @Override
    protected Behavior createTest() throws Exception {
        return null
                ;
    }


    /**
     * Invokes the supplied {@link java.lang.reflect.Method test method} and notifies the supplied
     * {@link org.junit.runner.notification.RunNotifier} of the appropriate events.
     *
     * @see #createTest()
     * @see org.junit.internal.runners.JUnit4ClassRunner#invokeTestMethod(java.lang.reflect.Method, org.junit.runner.notification.RunNotifier)
     */
    @Override
    protected void invokeTestMethod(Method method, RunNotifier notifier) {
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking test method [" + method.toGenericString() + "]");
        }

        // The following is a 1-to-1 copy of the original JUnit 4.4 code, except
        // that we use custom implementations for TestMethod and MethodRoadie.

        Description description = methodDescription(method);
        Object testInstance;
        try {
            testInstance = createTest();
        } catch (InvocationTargetException ex) {
            notifier.testAborted(description, ex.getCause());
            return;
        } catch (Exception ex) {
            notifier.testAborted(description, ex);
            return;
        }

        EasybTestMethod testMethod = new EasybTestMethod(method, getTestClass());
        //new EasybMethodRoadie(testInstance, testMethod, notifier, description).run();
    }

}
