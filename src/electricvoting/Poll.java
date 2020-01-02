/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electricvoting;

import java.util.ArrayList;

/**
 *
 * @author Prantik
 */
public class Poll {

    private int id;
    private String title;
    private String creator;
    private boolean multiple, editable, statsHidden, canAdd, filter, protection;
    private String emails, pw;
    private String created;
    private ArrayList<Option> options;

    private Poll() {
    }

    public Poll(int id, String title, String creator, boolean multiple, boolean editable, boolean canAdd, boolean statsHidden, boolean filter, String emails, boolean protection, String pw, String created) {
        this.id = id;
        this.title = title;
        this.creator = creator;
        this.multiple = multiple;
        this.editable = editable;
        this.statsHidden = statsHidden;
        this.canAdd = canAdd;
        this.filter = filter;
        this.protection = protection;
        this.emails = emails;
        this.pw = pw;
        this.created = created;
        this.options = options;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCreator() {
        return creator;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public boolean isEditable() {
        return editable;
    }

    public boolean isStatsHidden() {
        return statsHidden;
    }

    public boolean isCanAdd() {
        return canAdd;
    }

    public boolean isFilter() {
        return filter;
    }

    public boolean isProtection() {
        return protection;
    }

    public String getEmails() {
        return emails;
    }

    public String getPw() {
        return pw;
    }

    public String getCreated() {
        return created;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<Option> options) {
        this.options = options;
    }
}
