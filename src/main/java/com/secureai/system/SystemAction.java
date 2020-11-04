package com.secureai.system;

import com.secureai.DQNMain;
import com.secureai.DynDQNMain;
import com.secureai.model.actionset.Action;
import com.secureai.model.stateset.State;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemAction {

    private String resourceId;
    private String actionId;

    public void run(SystemEnvironment environment) {
        boolean print = false;
        if(!DynDQNMain.training)
            print = true;
        Action action = environment.getActionSet().getActions().get(this.actionId);
        if(print)
            System.out.println("Evaluating Action: "+this.actionId+" : "+this.resourceId);
        if (action.getPreCondition().run(environment.getSystemState(), this.resourceId)){
            if(print)
                System.out.println("RUN Action: "+this.actionId+" : "+this.resourceId);
            action.getPostCondition().run(environment.getSystemState(), this.resourceId);
            //printResourceState(environment);
        }
    }

    public boolean checkPreconditions(SystemEnvironment environment) {
        Action action = environment.getActionSet().getActions().get(this.actionId);
        return action.getPreCondition().run(environment.getSystemState(), this.resourceId) == true ? true : false; // do not return null
    }

    private void printResourceState(SystemEnvironment environment){
        // Print resource state
        System.out.print(resourceId+":: ");
        for (State s: State.values()) {
            if( environment.getSystemState().get(resourceId, s) != null)
                System.out.print(s+":"+environment.getSystemState().get(resourceId, s)+"; ");
        }
        System.out.print("\n");
    }

}
