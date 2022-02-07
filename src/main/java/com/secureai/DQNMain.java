package com.secureai;

import com.secureai.model.actionset.ActionSet;
import com.secureai.model.topology.Topology;
import com.secureai.nn.FilteredMultiLayerNetwork;
import com.secureai.nn.NNBuilder;
import com.secureai.rl.abs.ParallelDQN;
import com.secureai.rl.abs.SparkDQN;
import com.secureai.system.SystemEnvironment;
import com.secureai.system.SystemState;
import com.secureai.utils.ArgsUtils;
import com.secureai.utils.RLStatTrainingListener;
import com.secureai.utils.TimeUtils;
import com.secureai.utils.YAML;
import org.apache.log4j.BasicConfigurator;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.rl4j.util.DataManagerTrainingListener;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class DQNMain {

    public static void main(String... args) throws IOException {
    /**
    * Usage example:
    * java  -jar target/secureai.jar \
        --seed 50 \
        --maxEpochStep 1500 \
        --learningRate 0.000035 \
        --topology 2-containers \
        --actionSet 2-containers
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

        QLearning.QLConfiguration qlConfiguration = new QLearning.QLConfiguration(
                Integer.parseInt(argsMap.getOrDefault("seed", "42")),                //Random seed
                Integer.parseInt(argsMap.getOrDefault("maxEpochStep", "1000")),      //Max step By epoch
                Integer.parseInt(argsMap.getOrDefault("maxStep", "15000")),          //Max step
                Integer.parseInt(argsMap.getOrDefault("expRepMaxSize", "5000")),      //Max size of experience replay
                Integer.parseInt(argsMap.getOrDefault("batchSize", "128")),           //size of batches
                Integer.parseInt(argsMap.getOrDefault("targetDqnUpdateFreq", "500")), //target update (hard)
                Integer.parseInt(argsMap.getOrDefault("updateStart", "100")),         //num step noop warmup
                Double.parseDouble(argsMap.getOrDefault("rewardFactor", "1")),        //reward scaling
                Double.parseDouble(argsMap.getOrDefault("gamma", "0.75")),            //gamma
                Double.parseDouble(argsMap.getOrDefault("errorClamp", "0.5")),        //td-error clipping
                Float.parseFloat(argsMap.getOrDefault("minEpsilon", "0.01")),         //min epsilon
                Integer.parseInt(argsMap.getOrDefault("epsilonNbStep", "15000")),     //num step for eps greedy anneal
                Boolean.parseBoolean(argsMap.getOrDefault("doubleDQN", "false"))      //double DQN
        );
        System.out.println("qlConfig:" + qlConfiguration);
        SystemEnvironment mdp = new SystemEnvironment(topology, actionSet);
        FilteredMultiLayerNetwork nn = new NNBuilder().build(mdp.getObservationSpace().size(),
                mdp.getActionSpace().getSize(),
                Integer.parseInt(argsMap.getOrDefault("layers", "3")),
                Integer.parseInt(argsMap.getOrDefault("hiddenSize", "16")),
                Double.parseDouble(argsMap.getOrDefault("learningRate", "0.0001")));             
        //nn.setMultiLayerNetworkPredictionFilter(input -> mdp.getActionSpace().actionsMask(input));
        nn.setListeners(new ScoreIterationListener(100));
        //nn.setListeners(new PerformanceListener(1, true, true));
        System.out.println(nn.summary());


        String dqnType = argsMap.getOrDefault("dqn", "standard");
        QLearningDiscreteDense<SystemState> dql = new QLearningDiscreteDense<>(mdp, dqnType.equals("parallel") ? new ParallelDQN<>(nn) : dqnType.equals("spark") ? new SparkDQN<>(nn) : new DQN<>(nn), qlConfiguration);
        DataManager dataManager = new DataManager(true);
        dql.addListener(new DataManagerTrainingListener(dataManager));
        dql.addListener(new RLStatTrainingListener(dataManager.getInfo().substring(0, dataManager.getInfo().lastIndexOf('/'))));
        long startTime = System.nanoTime();
        dql.train();
        long endTime = System.nanoTime();
        long trainingTime =(endTime-startTime)/1000000000;
        Logger.getAnonymousLogger().info("[Time] Total training time (seconds):"+trainingTime);
    }
}
