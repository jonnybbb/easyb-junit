package org.easyb.junit;

import org.easyb.BehaviorRunner;
import org.easyb.Configuration;
import org.easyb.domain.Behavior;
import org.easyb.junit.report.JunitEasybReportsFactory;
import org.easyb.listener.ExecutionListener;
import org.easyb.listener.ListenerBuilder;
import org.easyb.listener.ListenerFactory;
import org.easyb.listener.ResultsAmalgamator;
import org.easyb.report.HtmlReportWriter;
import org.easyb.report.ReportWriter;
import org.easyb.report.TxtSpecificationReportWriter;
import org.easyb.report.TxtStoryReportWriter;
import org.junit.internal.runners.CompositeRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.runner.Description.createSuiteDescription;

public class EasybJUnitRunner extends CompositeRunner {
    private final EasybSuite suite;
    private final Configuration configuration;
    private List<Behavior> behaviors;
    private Description description;
    private JunitEasybReportsFactory reportsFactory;
    private JunitExecutionListenerRegistry listenerRegistry;
    private DescriptionCreator descriptionCreator;


    public EasybJUnitRunner(Class<? extends EasybSuite> testClass) throws Exception {
        super(testClass.getName());
        suite = testClass.newInstance();
        configuration = new Configuration(getFilePaths(), getReports(new File(".")));
        listenerRegistry = new JunitExecutionListenerRegistry();
        ListenerFactory.registerBuilder(new ListenerBuilder() {
            public ExecutionListener get() {
                return listenerRegistry;
            }
        });
        descriptionCreator = new DescriptionCreator(suite.baseDir());
        behaviors = BehaviorRunner.getBehaviors(configuration.getFilePaths());
        reportsFactory = new JunitEasybReportsFactory(new File("reports"));
    }

    public Description getDescription() {
        if (description == null) {
            description = createSuiteDescription(suite.description());
        }
        return description;
    }

    private String[] getFilePaths() {
        List<String> filePaths = new ArrayList<String>();
        listFiles(suite.searchDir(), filePaths);
        return filePaths.toArray(new String[filePaths.size()]);
    }

    private void listFiles(File dir, List<String> files) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                listFiles(file, files);
            } else if (isBehavior(file)) {
                files.add(file.getAbsolutePath());
            }
        }
    }

    private boolean isBehavior(File file) {
        return file.getName().endsWith(".story") || file.getName().endsWith(".specification");
    }

    private List<ReportWriter> getReports(File reportsDir) {
        File html = new File(reportsDir, "html");
        html.mkdirs();
        File plain = new File(reportsDir, "plain");
        plain.mkdirs();

        List<ReportWriter> reports = new ArrayList<ReportWriter>();
        reports.add(new HtmlReportWriter(html.getAbsolutePath() + "/easyb.html"));
        reports.add(new TxtStoryReportWriter(plain.getAbsolutePath() + "/easyb-stories.txt"));
        reports.add(new TxtSpecificationReportWriter(plain.getAbsolutePath() + "/easyb-specifications.txt"));
        return reports;
    }


    public void run(final RunNotifier notifier) {
        for (Behavior behavior : behaviors) {
            Description behaviorDescription = descriptionCreator.create(behavior);
            description.addChild(behaviorDescription);
            final EasybBehaviorJunitRunner runner = new EasybBehaviorJunitRunner(behavior, listenerRegistry, behaviorDescription);
            add(runner);

        }

        try {
            runChildren(notifier);
        } finally {
            reportsFactory.produceReports(new ResultsAmalgamator(behaviors));
        }


    }


}
