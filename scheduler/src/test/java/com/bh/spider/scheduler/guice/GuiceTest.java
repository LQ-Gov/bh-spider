package com.bh.spider.scheduler.guice;

import com.google.inject.*;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * @author liuqi19
 * @version GuiceTest, 2019-06-04 13:46 liuqi19
 **/
public class GuiceTest {



    @Test
    public void test(){

        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();

                bind(Base.class).to(ChildOne.class);
            }
        };


        List<Element> elements = Elements.getElements(module);

        Injector injector = Guice.createInjector(module);



        Map<Key<?>, Binding<?>> map = injector.getBindings();



        injector = injector.createChildInjector(new AbstractModule() {
            @Override
            protected void configure() {
                super.configure();
//                bind(BaseOther.class).
//                bind(BaseOther.class).to(BaseOtherOne.class);

            }
        });


        Base o = injector.getInstance(Base.class);

        BaseOther oo = injector.getInstance(BaseOther.class);


        o.print();

        oo.print();




        int a = 0;

    }
}
