package com.zpedroo.skulleater.objects;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private Integer skulls;
    private Boolean update;

    public PlayerData(UUID uuid, Integer skulls) {
        this.uuid = uuid;
        this.skulls = skulls;
        this.update = false;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Integer getSkulls() {
        return skulls;
    }

    public Boolean isQueueUpdate() {
        return update;
    }

    public void addSkulls(Integer skulls) {
        this.skulls += skulls;
        this.update = true;
    }

    public void removeSkulls(Integer skulls) {
        this.skulls -= skulls;
        this.update = true;
    }

    public void setSkulls(Integer skulls) {
        this.skulls = skulls;
        this.update = true;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }
}