package com.newcoder.async;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/26.
 */
public class EventModel {
    public EventType type;
    private int actorId;
    private int EntityId;
    private int EntityType;
    private int entityOwnerId;
    private Map<String,String> exts=new HashMap<>();
    public EventModel(EventType type){
        this.type=type;
    }
    public EventModel(){

    }
    public String getExt(String name){
        return exts.get(name);
    }
    public EventModel setExt(String name,String value){
        exts.put(name,value);
        return this;
    }
    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityId() {
        return EntityId;
    }

    public EventModel setEntityId(int entityId) {
        EntityId = entityId;
        return this;
    }

    public int getEntityType() {
        return EntityType;
    }

    public EventModel setEntityType(int entityType) {
        EntityType = entityType;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public void setExts(Map<String, String> exts) {
        this.exts = exts;
    }
}
