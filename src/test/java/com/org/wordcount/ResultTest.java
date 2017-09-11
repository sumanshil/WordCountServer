package com.org.wordcount;

import com.org.data.Result;
import com.org.physical.FileStorage;
import junit.framework.Assert;
import junit.framework.TestCase;


public class ResultTest extends TestCase {

    public void testResult(){
        FileStorage.getInstance().initialize();
        Result result = Result.getInstance();
        result.add("111111111");
        result.add("222222222");
        Result.ResultObject resultObject = result.getResultObject();
        Assert.assertTrue(resultObject.getUnique() == 2);
        Assert.assertTrue(resultObject.getDuplicate() == 0);
        Assert.assertTrue(resultObject.getUniqueTotal() == 2);

        result.add("111111111");
        result.add("222222222");
        resultObject = result.getResultObject();
        Assert.assertTrue(resultObject.getUnique() == 0);
        Assert.assertTrue(resultObject.getDuplicate() == 2);
        Assert.assertTrue(resultObject.getUniqueTotal() == 2);

        result.add("010101010");
        result.add("020202020");
        resultObject = result.getResultObject();
        Assert.assertTrue(resultObject.getUnique() == 2);
        Assert.assertTrue(resultObject.getDuplicate() == 0);
        Assert.assertTrue(resultObject.getUniqueTotal() == 4);

        FileStorage.cleanup();
    }
}
