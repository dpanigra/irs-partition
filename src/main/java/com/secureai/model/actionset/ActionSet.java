package com.secureai.model.actionset;


import com.secureai.system.SystemDefinition;
import lombok.Data;
import com.secureai.model.topology.Task;
import java.util.Map;


@Data
public class ActionSet {
    private String id;

    private Map<String, Action> actions;

    // variable action set
    public int actionSpaceSize(SystemDefinition systemDef){
        int sum = 0;
        for(Action a : this.actions.values()){
            for (String id : a.getTaskList()){
                for(Map.Entry<String, Task> entry : systemDef.getTopology().getTasks().entrySet()){
                    if(entry.getKey().equals(id)){
                        sum += entry.getValue().getReplication();
                        break;
                    }
                }
            }
        }
        return sum;
    }
}
