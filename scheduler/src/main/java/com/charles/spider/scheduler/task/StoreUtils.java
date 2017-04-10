package com.charles.spider.scheduler.task;

import com.charles.common.task.Task;
import com.charles.spider.store.base.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lq on 17-4-10.
 */
public class StoreUtils {
    public static Field[] build(Task task){
        List<Field> list = new ArrayList<>();
        list.add(new Field(Field.ID,task.getId()));
        list.add(new Field(Field.URL,task.getUrl()));

        return (Field[]) list.toArray();

    }
}
