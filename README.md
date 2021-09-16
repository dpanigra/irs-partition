## Parent repo
The repo is forked from ['SecureAI: Deep Reinforcement Learning for Self-Protection in Non-Stationary Cloud Architectures.'](https://github.com/MatteoLucantonio/secureai-java)

## Summary
A responsive Intrusion Response System (IRS) is critical to every organization. It attempts to prevent cybersecurity attacks. The code uses a model-free approach of Reinforcement Learning technique to train deep neural networks. The model suggests a best optimal policy to bring the system to normalcy. The optimal policy comprises a set of actions that is executed in the system. The code partitions the system for scaling the solution. Having one partition for one component of the system reduces the training time of the IRS model. Each partition provides local optimal policy.

## Packages and Tools Used
* dl4j
* rl4j

## Application used
Reference: The Online Boutique (OB) application from [here.](https://github.com/GoogleCloudPlatform/microservices-demo)

![Architecture](https://raw.githubusercontent.com/GoogleCloudPlatform/microservices-demo/master/docs/img/architecture-diagram.png?raw=true "Architecture")

## UML diagrams
Find below are the crictical part of the diagrams. They show how the application is creating system partitions.The design uses the creational, structural, and behaviorial design patterns to do so.

![Partition system - main](uml/uml-classdiagram.png?raw=true "Partition system - main")

Please note for convience, the sequence diagram is broken into two parts for readability.

![Sequence Diagram - part1](uml/uml-sequence-diagram-part1.png?raw=true "Sequence Diagram - part1")

![Sequence Diagram - part2](uml/uml-sequence-diagram-part2.png?raw=true "Sequence Diagram - part2")

## System partitions

The below diagram shows the number of componets. There is one component for each of the images and for each of the software used. Each component corresponds to one partition of the system. Please note there could be many replicas of the compoents in each partition.

![Partition system - systemcomponents](uml/system-components.png?raw=true "Partition system - systemcomponents")

The below diagram shows how the monolothic System's state attributes are broken down into multiple set of attributes one for each partitions.

![Partition system - statepartition](uml/system-partitions-stateattribs.png?raw=true "Partition system - statepartition")

The below diagram shows how the monolothic System's action set is broken down into multiple action set one for each partitions.

![Partition system - statepartition](uml/system-partitions-actionset.png?raw=true "Partition system - statepartition")

## Instructions
1. Clone the repo
2. Build, package, and run using Maven
3. pom.xml executes com.secureai.partition.main.PartitionDQNMain.main()