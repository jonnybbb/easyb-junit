package org.easyb.junit;

import org.easyb.BehaviorRunner;
import org.easyb.Configuration;
import org.easyb.listener.ExecutionListener;
import org.easyb.listener.ListenerBuilder;
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
    private final RunNotifierReplay runNotifierReplay = new RunNotifierReplay();
    private final DescriptionCreator descriptionCreator;
    private Description description;
    private final EasybSuite suite;
    private final Configuration configuration;
    private JunitResult junitResult;


    public EasybJUnitRunner(Class<? extends EasybSuite> testClass) throws Exception {
        super(testClass.getName());
        suite = testClass.newInstance();
        descriptionCreator = new DescriptionCreator(suite.baseDir());
        configuration = new Configuration(getFilePaths(), getReports(new File(".")));

        //reportsFactory = new JunitEasybReportsFactory(new File("."));
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


    public Description getDescription() {
        if (description == null) {
            description = createSuiteDescription(suite.description());
        }
        return description;
    }

    public void run(final RunNotifier notifier) {
        BehaviorRunner br = new BehaviorRunner(configuration, new ListenerBuilder() {
            public ExecutionListener get() {
                return new BehavoiurExecutionListener(notifier);
            }
        });
        try {
            notifier.fireTestRunStarted(description);

            final boolean successful = br.runBehaviors(BehaviorRunner.getBehaviors(configuration.getFilePaths()));


        } catch (Throwable e) {
            // notifier.fireTestFailure(new Failure(getDescription(), e));
        }
    }

    /*
   private void executeBehaviors(RunNotifier notifier) {
      for (Behavior behavior : behaviors()) {
         Description behaviorDescription = descriptionCreator.create(behavior);
         description.addChild(behaviorDescription);
         executeBehavior(behavior, behaviorDescription, notifier);
      }
   }

   private void executeBehavior(Behavior behavior, final Description behaviorDescription, final RunNotifier notifier) {

       ListenerBuilder lb = new ListenerBuilder() {
           public ExecutionListener get() {
               return new JUnitExecutionListener(behaviorDescription, notifier, reportsFactory);
           }
       };
       ListenerFactory.registerBuilder(lb);
       try {
         behavior.execute();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public void run(RunNotifier notifier) {
      if(isEclipse())
         runNotifierReplay.replay(notifier, suite.trackTime());
      else
         executeBehaviors(notifier);
   }

   private List<Behavior> behaviors() {
      return behaviors != null ? behaviors : (behaviors = getBehaviors(getFilePaths()));
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
   */


}
