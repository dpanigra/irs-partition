package com.secureai.partition.main;

import com.secureai.Config;
import com.secureai.model.actionset.ActionSet;
import com.secureai.model.topology.Topology;
import com.secureai.partition.system.PartitionSystemEnvironment;
import com.secureai.partition.utils.PartitionCreatorUtility;
import com.secureai.rl.vi.ValueIteration;
import com.secureai.system.SystemEnvironment;
import com.secureai.system.SystemState;
import com.secureai.utils.*;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PartitionVIMain {

    public static void main(String... args) throws IOException {
        System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
        System.setProperty("org.bytedeco.javacpp.maxbytes", "0");
        BasicConfigurator.configure();
        TimeUtils.setupStartMillis();
        System.out.println(TimeUtils.getStartMillis());

        Map<String, String> argsMap = ArgsUtils.toMap(args);
        Config.SEED = Integer.parseInt(argsMap.getOrDefault("seed", Config.DEFAULT_SEED));

        String topoloy_file = String.format("data/topologies/topology-%s.yml", argsMap.getOrDefault("topology", "3-containers"));
        String actionset_file = String.format("data/action-sets/action-set-%s.yml", argsMap.getOrDefault("actionSet", "3-containers"));
        Topology topology = YAML.parse(topoloy_file, Topology.class);
        ActionSet actionSet = YAML.parse(actionset_file, ActionSet.class);
        System.out.println("topoloy_file:"+topoloy_file);
        System.out.println("actionset_file:"+actionset_file);

        SystemEnvironment systemModel = new SystemEnvironment(topology, actionSet);

        List<PartitionSystemEnvironment> allPartitions = PartitionCreatorUtility.createPartitions(systemModel);
        for (PartitionSystemEnvironment partitionSystemModel: allPartitions){ //train on nn for each partition
            
            //train only a single partition
            if (argsMap.get("partition") != null) {
                if (!partitionSystemModel.getSystemDefinition().getTopology().getId().equals(argsMap.get("partition"))) {
                    continue;
                }
            }
            ValueIteration.VIConfiguration viConfiguration = new ValueIteration.VIConfiguration(
                    Config.SEED,      //Random seed
                    Integer.parseInt(argsMap.getOrDefault("iterations", "5")),  //iterations
                    Double.parseDouble(argsMap.getOrDefault("gamma", "0.75")),  //gamma
                    Double.parseDouble(argsMap.getOrDefault("epsilon", "1e-8")) //epsilon
            );

            System.out.println("viConfig:"+viConfiguration);

            ValueIteration<SystemState> vi = new ValueIteration<>(partitionSystemModel, viConfiguration);
            //vi.setValueIterationFilter(input -> ArrayUtils.toPrimitive(systemModel.getActionSpace().actionsMask(input)));

            vi.solve();

            double result = vi.evaluate(Integer.parseInt(argsMap.getOrDefault("evalSteps", "5")));
            ValueWriter.writeValue("output/value_iteration.txt", new Timestamped<>(result));
        }
    }
}
