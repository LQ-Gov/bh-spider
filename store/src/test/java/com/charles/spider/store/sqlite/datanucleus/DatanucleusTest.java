package com.charles.spider.store.sqlite.datanucleus;

import com.charles.spider.store.datanucleus.Inventory;
import org.junit.Test;

import javax.jdo.*;

public class DatanucleusTest {


    @Test
    public void  test(){
        PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("Tutorial");
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {

            tx.begin();
            Inventory inventory = new Inventory("test");
            pm.makePersistent(inventory);
            tx.commit();
        }
        finally
        {
            if (tx.isActive())
            {
                tx.rollback();
            }
            pm.close();
        }
    }
}
