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

    public static boolean training = true;

    public static void main(String... args) throws IOException {



        System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
        System.setProperty("org.bytedeco.javacpp.maxbytes", "0");
        BasicConfigurator.configure();
        TimeUtils.setupStartMillis();

        Map<String, String> argsMap = ArgsUtils.toMap(args);

        Topology topology = YAML.parse(String.format("data/topologies/topology-%s.yml", argsMap.getOrDefault("topology", "1-vms")), Topology.class);
        ActionSet actionSet = YAML.parse(String.format("data/action-sets/action-set-%s.yml", argsMap.getOrDefault("actionSet", "1-vms")), ActionSet.class);

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

        SystemEnvironment mdp = new SystemEnvironment(topology, actionSet);
        FilteredMultiLayerNetwork nn = new NNBuilder().build(mdp.getObservationSpace().size(),
                mdp.getActionSpace().getSize(),
                Integer.parseInt(argsMap.getOrDefault("layers", "3")),
                Integer.parseInt(argsMap.getOrDefault("hiddenSize", "64")),
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
        training = false;

        System.out.println("[Play] Starting experiment");
        int EPISODES = 10;
        double rewards = 0;
        for (int i = 0; i < EPISODES; i++) {
            mdp.reset();
            System.out.println("play policy");
            double reward = dql.getPolicy().play(mdp);
            rewards += reward;
            Logger.getAnonymousLogger().info("[Evaluate] Reward: " + reward);
        }
        Logger.getAnonymousLogger().info("[Evaluate] Average reward: " + rewards / EPISODES);
    }
}
