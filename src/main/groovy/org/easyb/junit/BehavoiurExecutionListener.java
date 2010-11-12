package org.easyb.junit;

import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.listener.ExecutionListener;
import org.easyb.result.ReportingTag;
import org.easyb.result.Result;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

/**
 * Created by IntelliJ IDEA.
 * User: johannes
 * Date: 12/11/10
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
class BehavoiurExecutionListener implements ExecutionListener {

    private String description;
    private Description behaviourDescription;
    private final RunNotifier notifier;
    private JunitResult junitResult;

    public BehavoiurExecutionListener(RunNotifier notifier) {
        this.notifier = notifier;
    }

    public static Description createBehaviourDescription(Behavior behavior) {
        return Description.createTestDescription(behavior.getClass(), behavior.getPhrase());
    }

    public static Description createStepDescription(BehaviorStep step) {
        return Description.createTestDescription(step.getClass(), step.getCurrentStepName());
    }


    public void startBehavior(Behavior behavior) {
        System.out.println(behavior);

        behaviourDescription = createBehaviourDescription(behavior);
        // suiteDescription.addChild(behaviourDescription);

        notifier.fireTestStarted(behaviourDescription);

    }

    public void startStep(BehaviorStep step) {
        System.out.println("current step " + step);


    }

    public void describeStep(String description) {
        System.out.println("Step Description");
        this.description = description;
    }

    public void gotResult(Result result) {
        System.out.println("got result: " + result);
        //  fireTestResult(result);


    }

    public void stopStep() {
        System.out.println("stop step");
    }

    public void stopBehavior(BehaviorStep step, Behavior behavior) {
        final Result result = step.getResult();
        junitResult = new JunitResult(result, createBehaviourDescription(behavior));


    }

    private void fireTestResult(final JunitResult junitResult) {

        if (junitResult.getFailureCount() > 0) {
            notifier.fireTestFailure(junitResult.getFailures().get(0));
        } else {
            notifier.fireTestFinished(behaviourDescription);
        }
    }

    public void tag(ReportingTag tag) {
        System.out.println("Tag: " + tag);

    }

    public void completeTesting() {
        System.out.println("finished testing");
        notifier.fireTestRunFinished(junitResult);
    }
}
