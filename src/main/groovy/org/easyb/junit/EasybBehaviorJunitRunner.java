package org.easyb.junit;

import org.easyb.domain.Behavior;
import org.easyb.junit.report.JunitEasybReportsFactory;
import org.easyb.listener.ExecutionListener;
import org.junit.internal.runners.CompositeRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.io.IOException;

public class EasybBehaviorJunitRunner extends CompositeRunner {
    private Description description;
    private Behavior behavior;
    private JunitEasybReportsFactory reportsFactory;
    private DescriptionCreator descriptionCreator;

    public EasybBehaviorJunitRunner(Behavior behavior, JunitEasybReportsFactory reportsFactory, Description suiteDescription, DescriptionCreator descriptionCreator) {
        super(suiteDescription.getDisplayName());
        this.description = suiteDescription;
        this.behavior = behavior;
        this.reportsFactory = reportsFactory;
        this.descriptionCreator = descriptionCreator;
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        ExecutionListener junitLister = new JUnitExecutionListener(description, notifier, reportsFactory);
        behavior.getBroadcastListener().registerListener(junitLister);

        try {
            behavior.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            behavior.getBroadcastListener().unregisterListener(junitLister);
        }


    }
}
