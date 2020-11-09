package com.secureai.system;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.secureai.model.actionset.Action;

public class SystemActionSpace extends DiscreteSpace {

    @Getter
    private final SystemEnvironment environment;

    @Getter
    private List<String> map;

    //public SystemActionSpace(SystemEnvironment environment, int size) { // variable action set // variable action set
    public SystemActionSpace(SystemEnvironment environment) {
        super(environment.getSystemDefinition().getResources().size() * environment.getActionSet().getActions().size());
        //super(size); // variable action set
        this.rnd.setSeed(12345);
        this.environment = environment;
        this.map = this.environment.getSystemDefinition().getResources().stream().flatMap(resourceId -> environment.getActionSet().getActions().keySet().stream().map(actionId -> String.format("%s.%s", resourceId, actionId))).collect(Collectors.toList());

        /* // variable action set
        this.map = new ArrayList<>();
        for(String resourceId : this.environment.getSystemDefinition().getResources()){
            for(Map.Entry<String , Action> entry : environment.getActionSet().getActions().entrySet()){
                for(String taskId : entry.getValue().getTaskList()){
                    if(taskId.equals(resourceId.substring(0, resourceId.lastIndexOf('.')))){
                        this.map.add(String.format("%s.%s", resourceId,  entry.getKey()));
                    }
                }
            }
        }*/

        for(String s : this.map)
            System.out.println("--- Action: "+s);

    }

    @Override
    public SystemAction encode(Integer a) {
        String systemActionId = this.map.get(a);
        String resourceId = systemActionId.substring(0, systemActionId.lastIndexOf('.'));
        String actionId = systemActionId.substring(systemActionId.lastIndexOf('.') + 1);
        return new SystemAction(resourceId, actionId);
    }

    public Integer decode(SystemAction systemAction) {
        return this.map.indexOf(String.format("%s.%s", systemAction.getResourceId(), systemAction.getActionId()));
    }

    public Double[] actionsMask(int encodedState) {
        return this.actionsMask(this.environment.getSystemState().newInstance(encodedState));
    }

    public Double[] actionsMask(SystemState systemState) {
        return IntStream.range(0, this.map.size()).mapToObj(i -> {
            SystemAction systemAction = this.encode(i);
            return this.environment.getActionSet().getActions().get(systemAction.getActionId()).getPreCondition().run(systemState, systemAction.getResourceId()) ? 1d : Double.NEGATIVE_INFINITY; //.99d : .01d for soft or 1d : 0d for hard filter
        }).toArray(Double[]::new);
    }

    public INDArray actionsMask(INDArray input) {
        INDArray[] labelsMask = IntStream.range(0, input.rows()).mapToObj(i -> Nd4j.create(ArrayUtils.toPrimitive(this.actionsMask(this.environment.getObservationSpace().encode(ArrayUtils.toObject(input.getRow(i).toDoubleVector())))))).toArray(INDArray[]::new);
        return Nd4j.vstack(labelsMask);
    }

}
