import com.charles.spider.store.entity.Module;
import org.junit.Test;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.util.Properties;

/**
 * Created by lq on 17-6-22.
 */
public class DatanucleusTest {


    @Test
    public void sqlite(){

        PersistenceManagerFactory factory = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");




        PersistenceManager pm = factory.getPersistenceManager();



        Transaction transaction = pm.currentTransaction();
        transaction.begin();

        Module entity = new Module();

        entity.setName("test-module");
        entity.setPath("c:\\mds");



        pm.makePersistent(entity);

        transaction.commit();
    }


}
