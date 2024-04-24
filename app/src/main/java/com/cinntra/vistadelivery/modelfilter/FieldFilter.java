package com.cinntra.vistadelivery.modelfilter;

import java.util.ArrayList;

public class FieldFilter {
    public ArrayList<String> assignedTo_id__in;
    public ArrayList<String> source__in;

    public FieldFilter() {

    }


    public FieldFilter(ArrayList<String> assignedTo_id__in, ArrayList<String> source__in) {
        this.assignedTo_id__in = assignedTo_id__in;
        this.source__in = source__in;
    }




    public ArrayList<String> getAssignedTo_id__in() {
        return assignedTo_id__in;
    }

    public void setAssignedTo_id__in(ArrayList<String> assignedTo_id__in) {
        this.assignedTo_id__in = assignedTo_id__in;
    }

    public ArrayList<String> getSource__in() {
        return source__in;
    }

    public void setSource__in(ArrayList<String> source__in) {
        this.source__in = source__in;
    }


}
