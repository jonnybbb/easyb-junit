package org.easyb.junit;

import org.easyb.BehaviorStep;
import org.easyb.domain.Behavior;
import org.easyb.junit.report.JunitEasybReportsFactory;
import org.easyb.listener.ResultsAmalgamator;
import org.easyb.listener.ResultsCollector;
import org.easyb.result.Result;
import org.easyb.util.BehaviorStepType;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static org.easyb.junit.RunProperties.isIde;
import static org.easyb.util.BehaviorStepType.*;

public class JUnitExecutionListener extends ResultsCollector {

    private static final List<BehaviorStepType> typesToTrack = Arrays.asList(SCENARIO, GIVEN, WHEN, THEN, AND, IT, BEFORE, AFTER);
    private final Description behaviorDescription;
    private Description scenarioDescription;
    private final RunNotifier notifier;
    private JunitEasybReportsFactory reportsFactory;
    private Stack<BehaviorStep> behaviorStep = new Stack<BehaviorStep>();
    private boolean stepRunning;
    private List<Behavior> behaviors = new ArrayList<Behavior>();
    private static int counter;

    public JUnitExecutionListener(Description behaviorDescription, RunNotifier notifier, JunitEasybReportsFactory reportsFactory) {
        this.behaviorDescription = behaviorDescription;
        this.notifier = notifier;
        this.reportsFactory = reportsFactory;
    }

    public void gotResult(Result result) {
        testForFailure(result);
    }

    private void testForFailure(Result result) {
        if (result.failed()) {
            final BehaviorStep peek = behaviorStep.peek();
            notifier.fireTestFailure(new Failure(createStepDescription(peek), result.cause));
            System.out.print(" -> Failed: " + result.cause.getMessage());
        }
    }

    public void startStep(BehaviorStep behaviorStep) {
        if (shouldStart(behaviorStep)) {
            pushStep(behaviorStep);
        }
    }

    private void pushStep(BehaviorStep currentStep) {
        this.behaviorStep.push(currentStep);
       //
       stopStepIfRunning();
        if (currentStep.getStepType() == SCENARIO)
            System.out.println("start Scenario: "+ this.currentStep.getCurrentStepName());
        if (shouldPrintDescription(currentStep))
            System.out.print(getStepDescriptionText(currentStep));

        notifier.fireTestStarted(createStepDescription(currentStep));
        stepRunning = true;
    }

    @Override
    public void completeTesting() {
        super.completeTesting();
        reportsFactory.produceReports(new ResultsAmalgamator(behaviors.toArray(new Behavior[behaviors.size()])));
    }

    private boolean shouldStart(BehaviorStep behaviorStep) {
        return typesToTrack.contains(behaviorStep.getStepType());
    }

    @Override
    public void startBehavior(Behavior behavior) {
        behaviors.add(behavior);

    }

    private boolean shouldPrintDescription(BehaviorStep behaviorStep) {
        return behaviorStep.getStepType() != BEFORE && behaviorStep.getStepType() != AFTER;
    }

    public void stopStep() {
        stopStepIfRunning();
    }

    private void stopStepIfRunning() {
        if (stepRunning) {
            final BehaviorStep pop = behaviorStep.pop();
            notifier.fireTestFinished(createStepDescription(pop));
            stepRunning = false;
            if (shouldPrintDescription(pop))
                System.out.println();
        }
    }

    private Description addStepDescription(BehaviorStep step) {
        if (step.getParentStep() == null) {
            return behaviorDescription;
        } else {
            final Description testDescription = createStepDescription(step);
            behaviorDescription.addChild(testDescription);
            return testDescription;
        }
//
//        if (step.getStepType() == SCENARIO && isIde()) {
//            scenarioDescription = createSuiteDescription(getStepDescriptionText());
//            behaviorDescription.addChild(scenarioDescription);
//            currentDescription = scenarioDescription;
//        } else {
//            currentDescription = createSuiteDescription(getStepDescriptionText() + "(" + getBehaviorHiddenName() + ")");
//            if (scenarioDescription == null) {
//                behaviorDescription.addChild(currentDescription);
//            } else {
//                scenarioDescription.addChild(currentDescription);
//            }
//        }
    }

    private Description createStepDescription(BehaviorStep step) {
        final Description testDescription = Description.createTestDescription(step.getClass(), getStepDescriptionText(step));
        behaviorDescription.addChild(testDescription);
        return behaviorDescription.getChildren().get(0);
    }

    /*
    * This is a bit of a hack, but in order to make sure the description for a
    * step is unique in Eclipse here we are just incrementing a number as the
    * behavior hidden name. But we don't want to do this when jUnit is being
    * run through Ant because ant can handle duplicate descriptions names just
    * fine, and using the counter will mess up the Ant output.
    */
    private String getBehaviorHiddenName() {
        return isIde() ? String.valueOf(counter++) : behaviorDescription.getDisplayName();
    }

    private String getStepDescriptionText(BehaviorStep step) {
        return format(step.getStepType()) + " " + step.getName();
    }

    private String format(BehaviorStepType type) {
        return typeShouldHaveSemiColon(type) ? type.type() + ":" : type.type();
    }

    private boolean typeShouldHaveSemiColon(BehaviorStepType type) {
        return type == SCENARIO || type == BEFORE || type == AFTER;
    }

    public void stopBehavior(BehaviorStep behaviorStep, Behavior behavior) {
        stopStepIfRunning();
        System.out.println();
        System.out.println();
    }
}
