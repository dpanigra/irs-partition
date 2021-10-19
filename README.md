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
irs-partition is a Java application that loads a system model, decomposes it into multiple partitions, and creates one neural network for each partition. It utilizes a model-free and off-policy approach to train the DNNs using multiple RL agents in a nonproduction environment called a training environment. The training environment simulates a live system by loading the system model through a set of configuration files. We use transfer learning to detach the agents from the training environment and deploy them in a production environment. When the production environment detects a threat, the agents, using the pre-trained DNNs, compute a near-optimal action given a particular state of the system at a given time to bring the system to a secure state. Software dependencies include dl4j and rl4j, Java implementations of deep neural network algorithms, and the RL framework. The software extends secureai-java that trains a single RL agent for the entire system.

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
