package org.bakalab.app.items;

public class RozvrhTimeItem {
    private String begintime, endtime, caption; //these terrible variable names are what the system returns, I'm just keeping them for consistency
    private boolean expanded;

    public RozvrhTimeItem() {
    }


    public RozvrhTimeItem(String begintime, String endtime, String caption) {
        this.begintime = begintime;
        this.endtime = endtime;
        this.caption = caption;
    }

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String toString(){
        return begintime + "|" + endtime + "|" + caption;
    }
}
