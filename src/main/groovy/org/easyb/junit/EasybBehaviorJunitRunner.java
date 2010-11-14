package org.easyb.junit;

import org.easyb.domain.Behavior;
import org.easyb.listener.ExecutionListener;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import java.io.IOException;


public class EasybBehaviorJunitRunner extends Runner {
    private Description description;
    private Behavior behavior;
    private final JunitExecutionListenerRegistry listenerRegistry;

    public EasybBehaviorJunitRunner(Behavior behavior, JunitExecutionListenerRegistry registry) {
        listenerRegistry = registry;
        this.description =  Description.createTestDescription(behavior.getClass(),behavior.getPhrase());
        this.behavior = behavior;
    }


    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        ExecutionListener junitLister = new JUnitExecutionListener(description, notifier);
        listenerRegistry.registerListener(junitLister);

        try {
            behavior.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            listenerRegistry.unregisterListener(junitLister);
        }


    }
}
