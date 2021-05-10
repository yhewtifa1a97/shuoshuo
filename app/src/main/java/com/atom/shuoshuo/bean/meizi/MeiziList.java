package com.atom.shuoshuo.bean.meizi;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/9.
 */

public class MeiziList implements Serializable {
    public boolean error;

    public ArrayList<Meizi> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<Meizi> getResults() {
        return results;
    }

    public void setResults(ArrayList<Meizi> results) {
        this.results = results;
    }
}
