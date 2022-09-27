package com.student.attendance.app.models;

public class About {
    private String content;
    private String conditions;
    private String objectives;

    public About() {
    }

    public About(String content, String conditions, String objectives) {
        this.content = content;
        this.conditions = conditions;
        this.objectives = objectives;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }
}
