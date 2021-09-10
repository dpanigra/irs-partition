package com.secureai.partition.system;

import com.secureai.model.actionset.ActionSet;
import com.secureai.model.topology.Topology;
import com.secureai.partition.model.MasterMDPHolder;
import com.secureai.utils.Stat;
import lombok.Getter;

/**
 *
 * @author dpani
 */
public class PartitionSystemEnvironment extends com.secureai.system.SystemEnvironment{
    
    @Getter
    private final MasterMDPHolder masterMDPHolder;
    
    @Getter
    private final PartitionSystemTerminateFunction systemTerminateFunction;

    /**
     *
     * @param topology - topology of the partition
     * @param actionSet - action set of the partition
     * @param masterMDPHolder - the master mdp which holds 
     *  the entire system's topology and system's action set
     */
    public PartitionSystemEnvironment(Topology topology, ActionSet actionSet, MasterMDPHolder masterMDPHolder) {
        super(topology, actionSet);
        super.stat = new Stat<>("output/mdp/" + topology.getId(), true);
        this.masterMDPHolder = masterMDPHolder;
        this.systemTerminateFunction = new PartitionSystemTerminateFunction(this, masterMDPHolder);
    }    
}
