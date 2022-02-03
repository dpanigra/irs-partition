# %%
# ^^^^^^^^^^^^^^^^^^^^^
# beg utils
# ^^^^^^^^^^^^^^^^^^^^^
#%% remove all localvariables
# local scope
myvar = [key for key in locals().keys() if not key.startswith('_')]
print (len(locals().keys()))
print (len(myvar))
# print (myvar)
for eachvar in myvar:
    print (eachvar)
    del locals()[eachvar]
print (len(locals().keys()))
# global scope
myvar = [key for key in globals().keys() if not key.startswith('_')]
print (len(globals().keys()))
print (len(myvar))
# print (myvar)
for eachvar in myvar:
    print (eachvar)
    del globals()[eachvar]
print (len(globals().keys()))

# %%
# %%
# ^^^^^^^^^^^^^^^^^^^^^
# end utils
# ^^^^^^^^^^^^^^^^^^^^^
# %%
from cProfile import label

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from pandas.core.frame import DataFrame
# base_dir = '/Users/damodarpanigrahi/Downloads/experiments'
base_dir = '/Users/dpani/Downloads/experiments'

def analysis(
        time_reward, 
        epoch_reward, 
        rolling_window,
        maxReward,
        a_xytext_x,
        a_xytext_y,
        a_xy_x,
        a_xy_y,
        x='Episode',
        y='Cumulative Reward',
        graph_title='P - x vs y',
        # graph_avg_title='P - x vs y - Avg over X x',
        graph_avg_title='P - x vs y',
        rollgraph=False,
        partition='FE',
        ):

    time_reward_path=base_dir+time_reward
    epoch_reward_path=base_dir+epoch_reward
    print(f'time_reward_path:{time_reward_path}')
    print(f'epoch_reward_path:{epoch_reward_path}')
    df_system_time_reward  = pd.read_csv(time_reward_path, sep='\t')
    df_system_epoch_reward = pd.read_csv(epoch_reward_path, sep='\t')
    # (df_system_time_reward.head(),df_system_epoch_reward.head())
    # (df_system_time_reward.tail(), df_system_epoch_reward.tail())
    (df_system_time_reward.info(), df_system_epoch_reward.info())
    print("The data has Rows {:,}, Columns {}".format(*df_system_time_reward.shape))
    print("The data has Rows {:,}, Columns {}".format(*df_system_epoch_reward.shape))
    # insert epochs
    df_system_time_reward.insert(0, 'Epoch', range(0 + len(df_system_time_reward)))
    print("The data has Rows {:,}, Columns {}".format(*df_system_time_reward.shape))
    # fix the column names
    columns = ['Episode', 'Timestamp', 'Cumulative Reward']
    df_system_time_reward.columns = columns
    # add Time column
    start_time = df_system_time_reward['Timestamp'][0]
    def base_time(a):
        return a - start_time
    df_system_time_reward['Time'] = df_system_time_reward.apply (
        lambda row : base_time(row['Timestamp']), 
        axis = 1
    )
    df_system_time_reward['Time']=df_system_time_reward['Time']/1000
    if maxReward != '':
        df_system_time_reward['MaxCumReward'] = maxReward
    # df_system_time_reward['time_diff'] = df_system_time_reward['Timestamp'].diff()
    # add labels
    # def add_label(a, b, c):
    #     return 'e:'+str(a)+',t:'+str(b)+',r:'+str(c)
    # df_system_time_reward['labels'] = df_system_time_reward.apply (
    #     lambda row : add_label(row['Epoch'],
    #         row['Time'], row['Reward']), 
    #     axis = 1
    # )    

    print("The data has Rows {:,}, Columns {}".format(*df_system_time_reward.shape))
    df_system_time_reward.info()
    # system or partition
    T=partition + ' partition'
    if 'sys' in time_reward:
        T = 'System'

    import matplotlib.pyplot as plt
    # plt.style.use('seaborn')

    if maxReward == '':
        df_system_time_reward.plot(x=x, y=y, linewidth=3, figsize=(12,6))
    else:
        df_system_time_reward.plot(x=x, y=[y, 'MaxCumReward'], linewidth=3, figsize=(12,6))

    x_label = x
    if x == 'Time':
        x_label='Time (sec)'
    # modify ticks size
    plt.xticks(fontsize=14)
    plt.yticks(fontsize=14)
    # plt.legend('')

    # title and labels
    graph_title=graph_title.replace('x',x).replace('y',y).replace('P',T)
    plt.title(graph_title, fontsize=20)
    plt.xlabel(x_label, fontsize=16)
    plt.ylabel(y, fontsize=16)

    def format_ticks(plt):
        current_values = plt.gca().get_xticks()
        plt.gca().set_xticklabels(['{:,.0f}'.format(x) for x in current_values])    
        current_values = plt.gca().get_yticks()
        plt.gca().set_yticklabels(['{:,.0f}'.format(x) for x in current_values])    

    format_ticks(plt)

    if rollgraph == False:
        arrowprops=dict(facecolor='black', arrowstyle="wedge,tail_width=0.5", alpha=0.2, color='b')
        bbox=dict(boxstyle="round", alpha=0.2, color='b')
        annotation_text=f'Convergence\npoint\n({a_xytext_x} episodes)'
        if x=='Time':
            annotation_text=f'Convergence\npoint\n({a_xytext_x} sec)'
        plt.annotate(annotation_text, 
            xytext=(a_xytext_x, a_xytext_y), 
            xy=(a_xy_x, a_xy_y), 
            ha='center', 
            va="center",
            bbox=bbox,
            arrowprops=arrowprops,
            size=13,
            )

    if rollgraph:
        # create rolling average
        df_system_time_reward_rolling = df_system_time_reward.rolling(rolling_window).mean() 
        df_system_time_reward_rolling = df_system_time_reward_rolling.iloc[::rolling_window, :]

        import matplotlib.pyplot as plt

        if maxReward == '':
            df_system_time_reward_rolling.plot(x=x, y=y, linewidth=3, figsize=(12,6))
        else:
            df_system_time_reward_rolling.plot(x=x, y=[y, 'MaxCumReward'], linewidth=3, figsize=(12,6))

        # modify ticks size
        plt.xticks(fontsize=14)
        plt.yticks(fontsize=14)
        # plt.legend('')

        # title and labels
        graph_avg_title = graph_avg_title.replace('X', str(rolling_window))
        graph_avg_title=graph_avg_title.replace('x',x).replace('y',y).replace('P',T)
        plt.title(graph_avg_title, fontsize=20)
        plt.xlabel(x_label, fontsize=16)
        plt.ylabel(y, fontsize=16)
        format_ticks(plt)
        arrowprops=dict(facecolor='black', arrowstyle="wedge,tail_width=0.5", alpha=0.2, color='b')
        bbox=dict(boxstyle="round", alpha=0.2, color='b')
        annotation_text=f'Convergence\npoint\n({a_xytext_x} episodes)'
        if x=='Time':
            annotation_text=f'Convergence\npoint\n({a_xytext_x} sec)'
        plt.annotate(annotation_text, 
            xytext=(a_xytext_x, a_xytext_y), 
            xy=(a_xy_x, a_xy_y), 
            ha='center', 
            va="center",
            bbox=bbox,
            arrowprops=arrowprops,
            size=13,
            )

# %%
# for system
myrun='sysytem_run15'
time_reward='/'+myrun+'/stat_5.csv'
epoch_reward='/'+myrun+'/epoch_reward_3.csv'
analysis(
    time_reward=time_reward,
    epoch_reward=epoch_reward,
    rolling_window=10,
    maxReward='',
    a_xytext_x=15,
    a_xytext_y=-1300,
    a_xy_x=15,
    a_xy_y=-1000,
)
analysis(
    time_reward=time_reward,
    epoch_reward=epoch_reward,
    rolling_window=10,
    x='Time',
    y='Cumulative Reward',
    maxReward='',
    a_xytext_x=220,
    a_xytext_y=-1300,
    a_xy_x=220,
    a_xy_y=-1000,
)
# %%
# for partitions - fe
myrun='partition_run6'
time_reward='/'+myrun+'/stat_5.csv'
epoch_reward='/'+myrun+'/epoch_reward_3.csv'
partition='Front End'

analysis(
    time_reward=time_reward,
    epoch_reward=epoch_reward,
    rolling_window=50,
    rollgraph=True,
    partition=partition,
    maxReward=-1.3,
    a_xytext_x=4830,
    a_xytext_y=-4,
    a_xy_x=4830,
    a_xy_y=-1.3,
)
analysis(
    time_reward=time_reward,
    epoch_reward=epoch_reward,
    rolling_window=50,
    x='Time',
    y='Cumulative Reward',
    rollgraph=True,
    maxReward=-1.3,
    a_xytext_x=173,
    a_xytext_y=-4,
    a_xy_x=173,
    a_xy_y=-1.3,
    partition=partition,
)

# %%

# %%
