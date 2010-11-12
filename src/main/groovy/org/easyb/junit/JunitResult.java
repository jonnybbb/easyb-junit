package org.easyb.junit;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: johannes
 * Date: 11/11/10
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class JunitResult extends Result {

    private org.easyb.result.Result easyResult;
    private Description description;

    public JunitResult(org.easyb.result.Result easyResult, Description description) {
        this.easyResult = easyResult;
        this.description = description;
    }

    @Override
    public int getRunCount() {
        return 1;
    }

    @Override
    public int getFailureCount() {
        return easyResult.failed() ? 1 : 0;
    }

    @Override
    public long getRunTime() {
        return super.getRunTime();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public List<Failure> getFailures() {
        return Arrays.asList(new Failure(description,easyResult.cause()));
    }

    @Override
    public int getIgnoreCount() {
        return easyResult.ignored() ? 1:0;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean wasSuccessful() {
        return easyResult.succeeded();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
