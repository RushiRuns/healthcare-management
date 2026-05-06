package com.rushi.healthcare_app;

import com.google.gson.annotations.SerializedName;

public class UpdateFollowUpRequest {
    @SerializedName("followup_id")
    private String followupId;

    @SerializedName("status")
    private String status;

    public UpdateFollowUpRequest(String followupId, String status) {
        this.followupId = followupId;
        this.status = status;
    }

    public String getFollowupId() {
        return followupId;
    }

    public void setFollowupId(String followupId) {
        this.followupId = followupId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
