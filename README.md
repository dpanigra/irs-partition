## License
```
Copyright 2021
Revision: V1

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

## Parent repo
The repo is forked from ['SecureAI: Deep Reinforcement Learning for Self-Protection in Non-Stationary Cloud Architectures.'](https://github.com/MatteoLucantonio/secureai-java)

## Summary
irs-partition software is a Java application that employs RL techniques to build an Intrusion Response System to thwart unseen breaches in a system production environment. The software creates multiple deep neural networks and uses a system model as its input. We utilize a model-free and off-policy Reinforcement Learning (RL) approach in training the deep neural network (DNN) using multiple single RL agents in a non-production environment called training environment. First, the training environment simulates a live non-stationary system by loading the system model through configuration files. Next, we use transfer learning to detach the agents from the training environment and deploy them in a production environment. Finally, we use the agents that leverage the pre-trained DNNs to predict a near-optimal action when the environment detects a threat in the production environment. 

Software dependencies of the application include 'Eclipse Deeplearning4J'(dl4j), and 'Reinforcement Learning for Java'(rl4j), both are Java implementations of deep neural network algorithms and the RL framework. In addition, the software extends 'SecureAI: Deep Reinforcement Learning for Self-Protection in Non-Stationary Cloud Architectures'secureai-java that trains a single RL agent for the entire non-stationary system.

## Abstract
Intrusion Response is considered a relatively new field of research. However, the recent research papers use the Reinforcement Learning (RL) technique as a primary strategy for designing a self-protective Intrusion Response System (IRS). Many papers have already demonstrated using model-free, off-policy training, simulated non-production environment, transfer learning, etc., to design an IRS for a non-stationary system. Such IRS attempts to alleviate the continuous management of the rules-based response system, immediate actionable pre-trained agents with the deep neural networks (DNN), and other benefits. However, these IRS uses only one DNN to predict the following optimal action. The paper introduces system partitions to create multiple DNNs for each partition, focusing on the local optima.

## Packages and Tools Used
* dl4j
* rl4j

## Application used
Reference: The Online Boutique (OB) application from [here.](https://github.com/GoogleCloudPlatform/microservices-demo)

![Architecture](https://raw.githubusercontent.com/GoogleCloudPlatform/microservices-demo/master/docs/img/architecture-diagram.png?raw=true "Architecture")

## UML diagrams
Find below are the critical part of the diagrams. They show how the software is creating system partitions.The design uses the creational, structural, and behaviorial design patterns to do so.

![Partition system - main](uml/uml-classdiagram.png?raw=true "Partition system - main")

![Sequence Diagram - part1](uml/uml-sequencediagram.png?raw=true "Sequence Diagram")


## System partitions

The system model consists of information on all the microservices used in the system.  A service provides the software implementation in a container image. We define a component as an executable unit of a service, that holds the image, and a partition as a running unit of all the containers of a particular component image. Each partition contains at least one container of the respective component in a \textit{secure} state of the system.  Every service has a corresponding component with its separate partition, as shown in the diagram below:

![Partition system - systemcomponents](uml/system-components.png?raw=true "Partition system - systemcomponents")

The below diagram shows how the monolothic System's state variables are broken down into multiple set of variables one for each partitions.

![Partition system - statepartition](uml/system-partitions-stateattribs.png?raw=true "Partition system - statepartition")

The below diagram shows how the monolothic System's action set is broken down into multiple action set one for each partitions.

![Partition system - statepartition](uml/system-partitions-actionset.png?raw=true "Partition system - statepartition")

## Instructions
1. Clone the repo
2. Build, package, and run using Maven
3. pom.xml executes com.secureai.partition.main.PartitionDQNMain.main()
