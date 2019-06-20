package com.bh.spider.scheduler.component;

import com.bh.spider.common.component.Component;
import com.google.common.base.Preconditions;
import com.google.inject.internal.Annotations;
import com.google.inject.name.Named;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * @author liuqi19
 * @version GuiceKey, 2019-06-05 10:07 liuqi19
 **/
public class GuiceName implements Named, Serializable {




    private final String value;
    private final Component component;
    private static final long serialVersionUID = 0L;

    public GuiceName(Component component, String value) {
        this.value = Preconditions.checkNotNull(value, "name");

        this.component =component;
    }

    @Override
    public String value() {
        return value;
    }


    public int hashCode() {
        return 127 * "value".hashCode() ^ this.value.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof Named)) {
            return false;
        } else {
            Named other = (Named)o;
            return this.value.equals(other.value());
        }
    }

    public String toString() {
        return "@" + Named.class.getName() + "(value=" + Annotations.memberValueString(this.value) + ")";
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return this.getClass();
    }


    public Component component(){
        return component;
    }
}
