package com.newcoder.async;

import java.util.List;

/**
 * Created by Administrator on 2017/2/27.
 */
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();

}
