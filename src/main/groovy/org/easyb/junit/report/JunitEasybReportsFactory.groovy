package org.easyb.junit.report

import org.easyb.report.HtmlReportWriter
import org.easyb.report.TxtSpecificationReportWriter
import org.easyb.report.TxtStoryReportWriter

public class JunitEasybReportsFactory {
  public static final String XML = "xml";
  public static final String PLAIN = "plain";
  public static final String HTML = "html";

  protected final File reportsDir;

  public JunitEasybReportsFactory(File reportsDir) {
    this.reportsDir = reportsDir;
  }

  protected def createReport() {
    def reportsWriter = []

    reportsWriter << new HtmlReportWriter(location: "$reportsDir/html/easyb.html")
    reportsWriter << new TxtStoryReportWriter(location: "$reportsDir/plain/easyb-stories.txt")
    reportsWriter << new TxtSpecificationReportWriter(location: "$reportsDir/plain/easyb-specifications.txt")
    //TODO grails core dont create reportsDir/xml, so this results in exception, fix somehow
    //reportsWriter << new XmlReportWriter(location: "$reportsDir/xml/easyb-${phaseName}.xml")

    return reportsWriter
  }

  public void produceReports(def results) {
    def reportsWriter = createReport()
    reportsWriter.each { rw ->
      rw.writeReport(results)
    }
  }
}