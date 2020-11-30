package com.secureai.system;

import com.secureai.rl.abs.DiscreteState;
import com.secureai.utils.RandomUtils;
import org.apache.commons.lang3.ArrayUtils;
import com.secureai.model.stateset.State;

import java.util.Arrays;
import java.util.stream.IntStream;

public class SystemState extends DiscreteState {

    private SystemEnvironment environment;

    public SystemState(SystemEnvironment environment, int size) {
        super(size);
        this.environment = environment;
    }

    @Override
    public void reset() {
        super.reset();
        this.worst();
    }

    public void reset(boolean rnd) {
        super.reset();

        if(rnd)
            this.random();
        else
            this.worst();
    }



    public void random() {
        this.environment.getSystemDefinition().getResources().forEach(resourceId -> {
            // Model 1 VMs

            //-------------------------------------------------------------------------------------
/*
            // Model 2 containers
            this.set(resourceId, State.active, RandomUtils.getRandom().nextDouble() < 0.5);
            this.set(resourceId, State.restarted, RandomUtils.getRandom().nextDouble() < 0.5);
            this.set(resourceId, State.corrupted, RandomUtils.getRandom().nextDouble() < 0.5);
            this.set(resourceId, State.shellCorrupted, RandomUtils.getRandom().nextDouble() < 0.5);
            this.set(resourceId, State.cartCorrupted, true);
            this.set(resourceId, State.confidentialityVulnerability, true);
            this.set(resourceId, State.integrityVulnerability, true);
            this.set(resourceId, State.passwordRequired, false);
            this.set(resourceId, State.dangerousCmdEnabled, true);
            this.set(resourceId, State.accessRestricted, false);*/
        });
    }

    public void worst() {
        this.environment.getSystemDefinition().getResources().forEach(resourceId -> {
/*
         // Model 1 VMs
            this.set(resourceId, State.active, false);
            this.set(resourceId, State.firewallBlockICMP, false);
            this.set(resourceId, State.firewallSoftBandwidthLimit, false);
            this.set(resourceId, State.firewallSoftBandwidthLimit, false);
            this.set(resourceId, State.appAvailable, false);
            this.set(resourceId, State.restarted, false);
            this.set(resourceId, State.corrupted, true);
            this.set(resourceId, State.dockerRuncUpdated, false);
            this.set(resourceId, State.dockerRuncUpgradable, true);
            this.set(resourceId, State.dockerExecAvailable, true);
            this.set(resourceId, State.containerCorrupted, true);
*/
            //-------------------------------------------------------------------------------------

            // Model 2 containers
            this.set(resourceId, State.active, false);
            this.set(resourceId, State.restarted, false);
            this.set(resourceId, State.corrupted, true);
            this.set(resourceId, State.shellCorrupted, true);
            this.set(resourceId, State.cartCorrupted, true);
            this.set(resourceId, State.confidentialityVulnerability, true);
            this.set(resourceId, State.integrityVulnerability, true);
            this.set(resourceId, State.passwordRequired, false);
            this.set(resourceId, State.dangerousCmdEnabled, true);
            this.set(resourceId, State.accessRestricted, false);

        });
    }

    public Boolean get(String resourceId, State state) {
        //System.out.println("Resource ID:"+resourceId+" ; value: "+state.getValue());
        // aggiungi check se la variabile di stato esiste per questa risorsa
        if(this.environment.getObservationSpace().getMap().indexOf(String.format("%s.%s", resourceId, state)) >= 0)
            return this.get(this.environment.getObservationSpace().getMap().indexOf(String.format("%s.%s", resourceId, state))) == 1;
        else
            return null;
    }

    public SystemState set(String resourceId, State state, boolean value) {
        if(this.environment.getObservationSpace().getMap().indexOf(String.format("%s.%s", resourceId, state)) >= 0)
            this.set(value ? 1 : 0, this.environment.getObservationSpace().getMap().indexOf(String.format("%s.%s", resourceId, state)));
        return this;
    }

    public SystemState newInstance() {
        return new SystemState(this.environment, this.environment.getSystemDefinition().getSystemStateSize());
    }

    /*
    public SystemState newInstance(int value) {
        SystemState result = this.newInstance();
        result.setFromInt(value);
        return result;
    }*/

    public SystemState newInstance(int[] values) {
        SystemState result = this.newInstance();
        IntStream.range(0, this.environment.getSystemDefinition().getSystemStateSize()).forEach(i -> result.set(values[i], i));
        return result;
    }

    @Override
    public double[] toArray() {
        return ArrayUtils.toPrimitive(this.environment.getObservationSpace().decode(this));
    }

    protected SystemState copy() {
      //return this.newInstance(this.toInt());
        return this.newInstance(this.toIntArray());
    }



}
