package org.ourgrid.acceptance.broker;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.ourgrid.acceptance.util.JDLCompliantTest;

/**
 * @author Ricardo Araujo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
@RunWith(Categories.class)
@ExcludeCategory(JDLCompliantTest.class)
@SuiteClasses({  
              Req_301_Test.class,  
              Req_302_Test.class,
              Req_303_Test.class,
              Req_304_Test.class,
              Req_305_Test.class,
              Req_309_Test.class,
              Req_311_Test.class,
              Req_312_Test.class,
              Req_313_Test.class,
              Req_314_Test.class,
              Req_315_Test.class,
              Req_316_Test.class,
              Req_319_Test.class,
              Req_320_Test.class,
              Req_321_Test.class,
              Req_322_Test.class,
              Req_324_Test.class,
              Req_325_Test.class,
              Req_326_Test.class,
              Req_327_Test.class,
              Req_328_Test.class,
              Req_329_Test.class,
              Req_330_Test.class,
              TestReplication.class
              })  
public class AllBrokerAcceptanceTests {

}
