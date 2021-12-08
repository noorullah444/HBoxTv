
package com.example.hboxtv.model.Epg;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpgModel {

    @SerializedName("epgResponse")
    @Expose
    private List<EpgResponse> epgResponse = null;

    public List<EpgResponse> getResponse() {
        return epgResponse;
    }

    public void setResponse(List<EpgResponse> epgResponse) {
        this.epgResponse = epgResponse;
    }

}
