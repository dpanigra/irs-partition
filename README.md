## Parent repo
The repo is forked from ['SecureAI: Deep Reinforcement Learning for Self-Protection in Non-Stationary Cloud Architectures.'](https://github.com/MatteoLucantonio/secureai-java)

## Summary
A responsive Intrusion Response System (IRS) is critical to every organization. It attempts to prevent cybersecurity attacks. The code uses a model-free approach of Reinforcement Learning technique to train deep neural networks. The model suggests a best optimal policy to bring the system to normalcy. The optimal policy comprises a set of actions that is executed in the system. The code partitions the system for scaling the solution. Having one partition for one component of the system reduces the training time of the IRS model. Each partition provides local optimal policy.

## Packages and Tools Used
* dl4j
* rl4j
* Visual Paradigm (for UML class and sequential diagrams)

## Application used
Reference: The Online Boutique (OB) application from [here.](https://github.com/GoogleCloudPlatform/microservices-demo)

![Architecture](https://raw.githubusercontent.com/GoogleCloudPlatform/microservices-demo/master/docs/img/architecture-diagram.png?raw=true "Architecture")

## Class diagrams
Find below are important class diagram pertaining to the creational, structural, and behaviorial pattern of the code.

Break down a complete system to a number of partions. There is one partition for one components of the applications and of the software components. There are 10 images and 1 redis cache used. Thus, there are 11 partitions. 

![Partition system - main](uml/secureai-partition-class-diagram-main.jpg?raw=true "Partition system - main")

![Partition system - action](uml/secureai-partition-class-diagram-action.jpg?raw=true "Partition system - action")

![Partition system - topology](uml/secureai-partition-class-diagram-topology.jpg?raw=true "Partition system - topology")

## Sequence diagrams
Please note for convience, a sequence diagram is broken into two parts for readability.

![Sequence Diagram - part1](uml/secureai-sequence-diagram-part1.jpg?raw=true "Sequence Diagram - part1")

![Sequence Diagram - part2](uml/secureai-sequence-diagram-part2.jpg?raw=true "Sequence Diagram - part1")

## Instructions
1. Clone the repo
2. Build, package, and run using Maven
3. pom.xml executes com.secureai.partition.main.PartitionDQNMain.main()