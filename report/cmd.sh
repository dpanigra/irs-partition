# Step 1 - Create ssh and upload pub key to cloudlab
#   -- one time setup of ssh keys in cloudlab
mkdir -p /Users/<userid_localbox>/Downloads/experiments/id_rsa
cd /Users/<userid_localbox>/Downloads/experiments/id_rsa
sshkey-gen

# Step 1 a: optional - ensure proper file permissions
# -- specially if the rs key pairs are repurposed
# chmod 600 irs_id_rsa
# chmod 644 irs_id_rsa.pub

# Step 1 b:
# upload pub key to cloudlab

# Step 2
# - ssh to the remote box
# - install jre,jdk,maven
# - create a dir ('irs-partition') for the code and gitclone the code
# - create a dir ('experiments') fo the log
#   -- tar with zip, scp the dir to the local box
# - copy the pom file to system and partition
#   -- idea is to edit the pom file for either system or partition irs s/w run
ssh -i /Users/<userid_localbox>/Downloads/experiments/irs_id_rsa <userid_remotebox>@c220g1-031104.wisc.cloudlab.us
sudo apt update -y
sudo apt install default-jre -y
sudo apt install default-jdk -y
sudo apt install maven -y
ls -ltrah
mkdir irs
cd irs/
git clone https://github.com/<userid_remotebox>gra/irs-partition.git
mkdir experiments
ls -ltrah
cd irs-partition/
ls -ltrah
cp pom.xml partition_pom.xml
cp pom.xml system_pom.xml

# Step 3 I
# - remove previous maven builds
# - ensure correct Java class is included in the META of jar
# - create the jar file
#   -- we will use this jar to execute the s/w
rm -rf /users/<userid_remotebox>/irs/irs-partition/target/
vi system_pom.xml
/usr/bin/mvn -f system_pom.xml package

# Step 4 I
# - specifiy a tag name for the run
# - run the jar file in bg
#   -- specify jvm and the program (QLearning and the NN hyperparams) arguments
setenv MYRUN "sysytem_run20"
echo $MYRUN
/usr/bin/java -Xms102400m -Xmx102400m -XX:MaxMetaspaceSize=40960m \
    -jar target/secureai.jar \
    --hiddenSize 16\
    --maxEpochStep 5000\
    --maxStep 300000 \
    > & ../experiments/$MYRUN.txt &

# Step 5 I
# - check the logs
tail -f ../experiments/$MYRUN.txt | grep Epoch
tail -f ../experiments/$MYRUN.txt | grep Episode

# Step 6 I
# - move the logs to a single directory
#   -- all logs are in this directoy
mkdir -p ../experiments/$MYRUN
mv ../experiments/$MYRUN.txt ../experiments/$MYRUN
ls -ltah ../experiments/$MYRUN
mkdir -p ../experiments/$MYRUN/rl4j-data
mv /users/<userid_remotebox>/rl4j-data/* ../experiments/$MYRUN/rl4j-data
ls -ltah ../experiments/$MYRUN
mkdir -p ../experiments/$MYRUN/irs-partition/output
mv /users/<userid_remotebox>/irs/irs-partition/output/* ../experiments/$MYRUN/irs-partition/output/
ls -ltah ../experiments/$MYRUN

# Step 6 I a
# - kill the java program and clean the log files
#   -- use only if Step 6 is skipped
fg
ctr+c
rm -rf /users/<userid_remotebox>/irs/irs-partition/output/*
ls -ltrah /users/<userid_remotebox>/irs/irs-partition/output/
rm -rf /users/<userid_remotebox>/rl4j-data/*
ls -ltrah /users/<userid_remotebox>/rl4j-data/

# Step 7 I
# - copy the logs from the remote box to local box
# - create episode vs reward csv file
# - create time vs reward csv file
#   -- both the csv file is then fed into the report program
export MYRUN="sysytem_run14"
echo $MYRUN
mkdir $MYRUN
cd $MYRUN
scp -i /Users/<userid_localbox>/Downloads/experiments/irs_id_rsa <userid_remotebox>@c220g2-010614.wisc.cloudlab.us:/users/<userid_remotebox>/irs/experiments/$MYRUN/irs-partition/output/mdp/out-0/stat.csv .
scp -i /Users/<userid_localbox>/Downloads/experiments/irs_id_rsa <userid_remotebox>@c220g2-010614.wisc.cloudlab.us:/users/<userid_remotebox>/irs/experiments/$MYRUN/$MYRUN.txt .
sed 's/,/\t/g' stat.csv > stat_1.csv
head stat_1.csv
sed 's/Seconds//g' stat_1.csv > stat_2.csv
head stat_2.csv
sed 's/Steps//g' stat_2.csv > stat_3.csv
head stat_3.csv
sed 's/Timestamp\t/Timestamp/g' stat_3.csv > stat_4.csv
head stat_4.csv
sed 's/Reward\t/Reward/g' stat_4.csv > stat_5.csv
head stat_5.csv
echo -e 'Epoch\tReward' > epoch_reward.csv
cat $MYRUN.txt | grep Epoch | awk -F 'SyncLearning  - ' '{print $2}' >> epoch_reward.csv
head epoch_reward.csv
sed 's/Epoch: //g' epoch_reward.csv > epoch_reward_1.csv
head epoch_reward_1.csv
sed 's/, reward: /\t/g' epoch_reward_1.csv > epoch_reward_2.csv
head epoch_reward_2.csv
sed ''/^$/d'' epoch_reward_2.csv > epoch_reward_3.csv
head epoch_reward_3.csv
cd ..


# Adjust steps 3 to 7 for the partition

# Step 3 II
# - remove previous maven builds
# - ensure correct Java class is included in the META of jar
# - create the jar file
#   -- we will use this jar to execute the s/w
rm -rf /users/<userid_remotebox>/irs/irs-partition/target/
vi partition_pom.xml
/usr/bin/mvn -f partition_pom.xml package

# Step 4 II
# - specifiy a tag name for the run
# - run the jar file in bg
#   -- specify jvm and the program (QLearning and the NN hyperparams) arguments
setenv MYRUN "sysytem_run20"
echo $MYRUN
/usr/bin/java -Xms102400m -Xmx102400m -XX:MaxMetaspaceSize=40960m \
    -jar target/secureai.jar \
    --hiddenSize 8\
    --maxStep 30000 \
    > & ../experiments/$MYRUN.txt &
# OR (for a specific partition)
/usr/bin/java -Xms102400m -Xmx102400m -XX:MaxMetaspaceSize=40960m \
    -jar target/secureai.jar \
    --hiddenSize 16\
    --maxEpochStep 5000\
    --maxStep 300000 \
    --partition frontend-service \
    > & ../experiments/$MYRUN.txt &


# Step 5 II
# - check the logs
# same as Step 5 I 

# Step 6 II
# - move the logs to a single directory
#   -- all logs are in this directoy
# same as Step 6 I

# Step 6 II a
# - kill the java program and clean the log files
#   -- use only if Step 6 is skipped
# same as Step 6 I a

# Step 7 II
# - copy the logs from the remote box to local box
# - create episode vs reward csv file
# - create time vs reward csv file
#   -- both the csv file is then fed into the report program
# from cl to mac
cd /Users/<userid_localbox>/Downloads/experiments
export MYRUN="partition_run7"
export MYPARTITION="frontend-service"
echo $MYRUN
mkdir $MYRUN
cd $MYRUN
scp -r -i /Users/<userid_localbox>/Downloads/experiments/irs_id_rsa <userid_remotebox>@c220g2-010614.wisc.cloudlab.us:/users/<userid_remotebox>/irs/experiments/$MYRUN/irs-partition/output/mdp/$MYPARTITION/out-0/stat.csv .
scp -i /Users/<userid_localbox>/Downloads/experiments/irs_id_rsa <userid_remotebox>@c220g2-010614.wisc.cloudlab.us:/users/<userid_remotebox>/irs/experiments/$MYRUN/$MYRUN.txt .

# from drive to mac (in case operated from the extracted tar.gz file in the local box)
cp cloudlab/$MYRUN/irs-partition/output/mdp/$MYPARTITION/out-0/stat.csv .
cp cloudlab/$MYRUN/$MYRUN.txt .
sed 's/,/\t/g' stat.csv > stat_1.csv
head stat_1.csv
sed 's/Seconds//g' stat_1.csv > stat_2.csv
head stat_2.csv
sed 's/Steps//g' stat_2.csv > stat_3.csv
head stat_3.csv
sed 's/Timestamp\t/Timestamp/g' stat_3.csv > stat_4.csv
head stat_4.csv
sed 's/Reward\t/Reward/g' stat_4.csv > stat_5.csv
head stat_5.csv
echo -e 'Epoch\tReward' > epoch_reward.csv
cat $MYRUN.txt | grep Epoch | awk -F 'SyncLearning  - ' '{print $2}' >> epoch_reward.csv
head epoch_reward.csv
sed 's/Epoch: //g' epoch_reward.csv > epoch_reward_1.csv
head epoch_reward_1.csv
sed 's/, reward: /\t/g' epoch_reward_1.csv > epoch_reward_2.csv
head epoch_reward_2.csv
sed ''/^$/d'' epoch_reward_2.csv > epoch_reward_3.csv
head epoch_reward_3.csv
cd ..

# Step 8
# - tar and zip the logs from all the runs
cd /users/<userid_remotebox>/irs/irs-partition
set date_now=`date "+%F-%H"`
echo $date_now
tar -czvf experiments$date_now.tar.gz ../experiments/
ls -ltrah experiments$date_now.tar.gz

# Step 9
# - scp the logs and unzip&untar it
# -- DATE: The date of remote and local boxes might differ
# -- If the dates differ then adjust the variable
cd /Users/<userid_localbox>/Downloads/experiments
export date_now=`date "+%F-%H"`
echo $date_now
export date_now='2022-01-29-16'
scp -i /Users/<userid_localbox>/Downloads/experiments/irs_id_rsa <userid_remotebox>@c220g2-<instanceid>.wisc.cloudlab.us:/users/<userid_remotebox>/irs/irs-partition/experiments$date_now.tar.gz .
ls -ltrah experiments$date_now.tar.gz
