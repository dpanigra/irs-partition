package com.secureai.model.topology;

import lombok.Data;
import com.secureai.model.stateset.State;

import java.util.List;

@Data
public class Task {
    private String type;
    private Integer replication;
    private List<State> state;
}

