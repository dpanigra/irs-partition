/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.secureai.partition.main;

import com.secureai.model.actionset.ActionSet;
import com.secureai.model.topology.Topology;
import com.secureai.nn.FilteredMultiLayerNetwork;
import com.secureai.nn.NNBuilder;
import com.secureai.partition.utils.PartitionCreatorUtility;
import com.secureai.partition.system.PartitionSystemEnvironment;
import com.secureai.rl.abs.ParallelDQN;
import com.secureai.rl.abs.SparkDQN;
import com.secureai.system.SystemEnvironment;
import com.secureai.system.SystemState;
import com.secureai.utils.ArgsUtils;
import com.secureai.utils.RLStatTrainingListener;
import com.secureai.utils.TimeUtils;
import com.secureai.utils.YAML;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.rl4j.util.DataManagerTrainingListener;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.log4j.BasicConfigurator;

public class PartitionDQNMain {

    public static void main(String... args) throws IOException {
    /**
    * Usage example:
    * java  -jar target/secureai.jar \
        --seed 50 \
        --maxEpochStep 1500 \
        --learningRate 0.000035 \
        --topology 2-containers \
        --actionSet 2-containers
        --partition frontend-service \
    **/
        System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
        System.setProperty("org.bytedeco.javacpp.maxbytes", "0");
        BasicConfigurator.configure();
        TimeUtils.setupStartMillis();
        System.out.println(TimeUtils.getStartMillis());
        

        Map<String, String> argsMap = ArgsUtils.toMap(args);
        String topoloy_file = String.format("data/topologies/topology-%s.yml", argsMap.getOrDefault("topology", "3-containers"));
        String actionset_file = String.format("data/action-sets/action-set-%s.yml", argsMap.getOrDefault("actionSet", "3-containers"));
        Topology topology = YAML.parse(topoloy_file, Topology.class);
        ActionSet actionSet = YAML.parse(actionset_file, ActionSet.class);
        System.out.println("topoloy_file:"+topoloy_file);
        System.out.println("actionset_file:"+actionset_file);
        
        SystemEnvironment systemModel = new SystemEnvironment(topology, actionSet);
        System.out.println(systemModel.getSystemDefinition());
        List<PartitionSystemEnvironment> allPartitions = PartitionCreatorUtility.createPartitions(systemModel);
        for (PartitionSystemEnvironment partitionSystemModel: allPartitions){ //train on nn for each partition
            
            //train only a single partition
            if (argsMap.get("partition") != null) {
                if (!partitionSystemModel.getSystemDefinition().getTopology().getId().equals(argsMap.get("partition"))) {
                    continue;
                }
            }
            
            QLearning.QLConfiguration qlConfiguration = new QLearning.QLConfiguration(
                    Integer.parseInt(argsMap.getOrDefault("seed", "42")),                //Random seed
                    Integer.parseInt(argsMap.getOrDefault("maxEpochStep", "500")),      //Max step By epoch                    
                    Integer.parseInt(argsMap.getOrDefault("maxStep", "15000")),          //Max step
                    Integer.parseInt(argsMap.getOrDefault("expRepMaxSize", "5000")),      //Max size of experience replay
                    Integer.parseInt(argsMap.getOrDefault("batchSize", "128")),           //size of batches
                    Integer.parseInt(argsMap.getOrDefault("targetDqnUpdateFreq", "500")), //target update (hard)
                    Integer.parseInt(argsMap.getOrDefault("updateStart", "100")),         //num step noop warmup
                    Double.parseDouble(argsMap.getOrDefault("rewardFactor", "1")),        //reward scaling
                    Double.parseDouble(argsMap.getOrDefault("gamma", "0.75")),            //gamma
                    Double.parseDouble(argsMap.getOrDefault("errorClamp", "0.5")),        //td-error clipping
                    Float.parseFloat(argsMap.getOrDefault("minEpsilon", "0.01")),         //min epsilon
                    Integer.parseInt(argsMap.getOrDefault("epsilonNbStep", "15000")),     //num step for eps greedy a:qnneal
                    Boolean.parseBoolean(argsMap.getOrDefault("doubleDQN", "false"))      //double DQN
            );
            System.out.println("qlConfig:" + qlConfiguration);
            FilteredMultiLayerNetwork nn = new NNBuilder().build(partitionSystemModel.getObservationSpace().size(),
                    partitionSystemModel.getActionSpace().getSize(),
                    Integer.parseInt(argsMap.getOrDefault("layers", "3")),
                    Integer.parseInt(argsMap.getOrDefault("hiddenSize", "16")),
                    Double.parseDouble(argsMap.getOrDefault("learningRate", "0.0001")));             
            nn.setListeners(new ScoreIterationListener(100));
            System.out.println(nn.summary());

            System.out.println("Training for " + partitionSystemModel.getSystemDefinition().getTopology().getId());


            String dqnType = argsMap.getOrDefault("dqn", "standard");
            QLearningDiscreteDense<SystemState> dql = new QLearningDiscreteDense<>(partitionSystemModel, dqnType.equals("parallel") ? new ParallelDQN<>(nn) : dqnType.equals("spark") ? new SparkDQN<>(nn) : new DQN<>(nn), qlConfiguration);
            DataManager dataManager = new DataManager(true);
            dql.addListener(new DataManagerTrainingListener(dataManager));
            dql.addListener(new RLStatTrainingListener(dataManager.getInfo().substring(0, dataManager.getInfo().lastIndexOf('/'))));
            long startTime = System.nanoTime();
            dql.train();
            long endTime = System.nanoTime();
            long trainingTime =(endTime-startTime)/1000000000;
            Logger.getAnonymousLogger().info("[Time] Total training time (seconds):"+trainingTime);
//            break; //after the first partition is trained; for testing
        } //train one nn for each partition
    } //end of main 
}
