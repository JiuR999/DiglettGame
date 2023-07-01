package cn.swust.firstcold.source;

public class Level {
    private String name;
    //是否解锁
    private boolean locked;
    //是否通关
    private boolean completed;

    public Level(String name, boolean locked, boolean completed){
        this.name = name;
        this.locked = locked;
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
