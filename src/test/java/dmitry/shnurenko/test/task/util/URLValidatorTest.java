package dmitry.shnurenko.test.task.util;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Dmitry Shnurenko
 */
@RunWith(DataProviderRunner.class)
public class URLValidatorTest {

    @DataProvider
    public static Object[][] preparedData() {
        return new Object[][]{
                {"http://www.test.ts", true},
                {"https://www.test.ts", true},
                {"http://test.ts", true},
                {"http://www.test.ts/est", true},
                {"http://www.t.ts", true},
                {"http://t.ts", true},
                {"www.test.ts", true},
                {"www.test", false},
                {"http://www.test", false},
                {"http://test", false}
        };
    }

    @Test
    @UseDataProvider("preparedData")
    public void urlShouldBeValidated(String url, boolean validationResult) {
        assertThat(URLValidator.isUrlValid(url), is(validationResult));
    }
}