package spec.easyb.junit;

import org.easyb.junit.EasybSuite;

import java.io.File;

public class EasybJunitBehaviors extends EasybSuite {

    @Override
    protected File baseDir() {
        return new File("src/test/resources");
    }
}
