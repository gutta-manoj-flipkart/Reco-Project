package com.flipkart.reco.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigFilterData {
    private String dc;
    private String fromDateTime_UNIX;
    private String toDateTime_UNIX;
}
