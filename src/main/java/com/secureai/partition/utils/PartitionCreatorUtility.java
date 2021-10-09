package com.secureai.partition.utils;

import com.secureai.partition.system.PartitionSystemEnvironment;
import com.secureai.model.actionset.Action;
import com.secureai.model.actionset.ActionSet;
import com.secureai.model.topology.Connection;
import com.secureai.model.topology.Task;
import com.secureai.system.SystemEnvironment;
import com.secureai.model.topology.Topology;
import com.secureai.partition.model.MasterMDPHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author dpani
 */
public class PartitionCreatorUtility {
    
    public static List<PartitionSystemEnvironment> createPartitions(SystemEnvironment systemModel) {
        MasterMDPHolder masterMDPHolder = new MasterMDPHolder();
        masterMDPHolder.setActionSet(systemModel.getActionSet());
        masterMDPHolder.setActionSpace(systemModel.getActionSpace());
        masterMDPHolder.setObservationSpace(systemModel.getObservationSpace());
        masterMDPHolder.setSystemDefinition(systemModel.getSystemDefinition());
        masterMDPHolder.setSystemState(systemModel.getSystemState());
        
        
        ActionSet allActionSet =  systemModel.getActionSet();

        Topology fullTopology = systemModel.getSystemDefinition().getTopology();
        //for each partition prepare id, task, and the connections
        List<PartitionSystemEnvironment> partitionMdp = new ArrayList<>();
        
        for (Map.Entry<String, Task> eachTask: fullTopology.getTasks().entrySet()) {
            //gather appropriate Actions for the partition
            Map<String, Action> partitionActions = new HashMap<>();
            //find out if there are any actions defined
            //for this partition/component
            boolean actionsFound = false;
            for (Map.Entry<String, Action> eachAction: allActionSet.getActions().entrySet()) {
                //get the task lists (check if the componenet is available for 
                // the current partition
                for (String eachComponent: eachAction.getValue().getTaskList()){
                    if (eachComponent.equals(eachTask.getKey())) {
                        partitionActions.put(eachAction.getKey(), eachAction.getValue());
                        actionsFound = true;
                        break;
                    } //end of checking if the component can take this action
                } //end of iterating through all the components
            }//end of iterating through all the actions
            
            //if there is no action defined for this particular component
            //  then there is no need to partition and to train a model.
            //  This could happen only if the .yml file has no components
            //  for any of the actions
            if (actionsFound) {
                ActionSet partitionActionSet = new ActionSet();
                partitionActionSet.setId(eachTask.getKey());
                partitionActionSet.setActions(partitionActions);

                Topology partitionTopology = new Topology(); 

                //create the task only for the current partition
                Map<String, Task> partitionTask = new HashMap<>();
                partitionTask.put(eachTask.getKey(), eachTask.getValue());

                //create the connection only for the current partition
                Map<String, Connection> partitionConnection = new HashMap<>();
                for (Map.Entry<String, Connection> eachConnection: fullTopology.getConnections().entrySet()) {
                    if (eachConnection.getKey().startsWith(eachTask.getKey())){
                        partitionConnection.put(eachConnection.getKey(), eachConnection.getValue());
                    }
                }

                partitionTopology.setId(eachTask.getKey()); //set the partition id
                partitionTopology.setTasks(partitionTask); //set the tasks for the partition
                partitionTopology.setConnections(partitionConnection); 


                partitionMdp.add(new PartitionSystemEnvironment(partitionTopology, partitionActionSet, masterMDPHolder));
            }// end of testing if a partition has any actions
        }

        return partitionMdp;
    }
}
