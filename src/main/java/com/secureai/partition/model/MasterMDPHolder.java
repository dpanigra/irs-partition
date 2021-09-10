package com.secureai.partition.model;

import com.secureai.model.actionset.ActionSet;
import com.secureai.system.SystemActionSpace;
import com.secureai.system.SystemDefinition;
import com.secureai.system.SystemState;
import com.secureai.system.SystemStateSpace;
import lombok.Data;

/**
 *
 * @author dpani
 */
@Data
public class MasterMDPHolder {
    private SystemActionSpace actionSpace;
    private ActionSet actionSet;
    private SystemStateSpace observationSpace;
    private SystemState systemState;
    private SystemDefinition systemDefinition;
}
