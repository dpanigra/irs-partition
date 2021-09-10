package com.secureai.partition.system;

import com.secureai.model.stateset.State;
import com.secureai.partition.model.MasterMDPHolder;
import com.secureai.system.SystemState;
import com.secureai.system.SystemTerminateFunction;
import lombok.Getter;

/**
 *
 * @author dpani
 */
public class PartitionSystemTerminateFunction extends SystemTerminateFunction{
    @Getter
    private PartitionSystemEnvironment environment;
    
    @Getter
    private MasterMDPHolder masterMDPHolder;
    
    public PartitionSystemTerminateFunction(PartitionSystemEnvironment environment, MasterMDPHolder masterMDPHolder) {
        super(environment);
        this.masterMDPHolder = masterMDPHolder;
    }    
    
    @Override
    public boolean terminated(SystemState systemState) {

        // Model 1 VMs
//        Boolean active, corrupted, appAvailable, dockerRuncUpdated, dockerExecAvailable;
//        for (String resourceId : this.environment.getSystemDefinition().getResources()){
//            active = systemState.get(resourceId, State.active);
//            corrupted = systemState.get(resourceId, State.corrupted);
//            appAvailable= systemState.get(resourceId, State.appAvailable);
//            dockerRuncUpdated = systemState.get(resourceId, State.dockerRuncUpdated);
//            dockerExecAvailable = systemState.get(resourceId, State.dockerExecAvailable);
//            if (    (active != null && !active ) ||
//                    ( appAvailable != null && !appAvailable) ||
//                    ( corrupted != null && corrupted) ||
//                    ( dockerRuncUpdated != null && dockerExecAvailable != null && !(dockerRuncUpdated || !dockerExecAvailable)))
//                return false;
//        }
//        return true;

        //-------------------------------------------------------------------------------------

        // Model 2 containers
        // use masterMDPHolder system state for all states except only for 
        // state attributes that is relevant to the current from the partiotion
        // enviornment
        Boolean active, corrupted, shellCorrupted, cartCorrupted, confidentialityVuln, integrityVuln;
        for (String resourceId : this.masterMDPHolder.getSystemDefinition().getResources()){
            active = this.masterMDPHolder.getSystemState().get(resourceId, State.active);
            corrupted = this.masterMDPHolder.getSystemState().get(resourceId, State.corrupted);
            shellCorrupted = this.masterMDPHolder.getSystemState().get(resourceId, State.shellCorrupted);
            cartCorrupted = this.masterMDPHolder.getSystemState().get(resourceId, State.cartCorrupted);
            confidentialityVuln = this.masterMDPHolder.getSystemState().get(resourceId, State.confidentialityVulnerability);
            integrityVuln = this.masterMDPHolder.getSystemState().get(resourceId, State.integrityVulnerability);
            
            if (resourceId.equals(this.environment.getSystemDefinition().getTopology().getId())){
                active = systemState.get(resourceId, State.active);
                corrupted = systemState.get(resourceId, State.corrupted);
                shellCorrupted = systemState.get(resourceId, State.shellCorrupted);
                cartCorrupted = systemState.get(resourceId, State.cartCorrupted);
                confidentialityVuln = systemState.get(resourceId, State.confidentialityVulnerability);
                integrityVuln = systemState.get(resourceId, State.integrityVulnerability);
            } //end of state from the current partition

            if (    ( active != null && !active ) ||
                    ( corrupted != null && corrupted) ||
                    ( shellCorrupted != null && shellCorrupted) ||
                    ( cartCorrupted != null && cartCorrupted) ||
                    ( confidentialityVuln != null && confidentialityVuln) ||
                    ( integrityVuln != null && integrityVuln) )
                return false;
        }
        return true;
    }    
}
