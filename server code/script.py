# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""
import pyedflib
import numpy as np
import pandas as pd
'''
f1 = pyedflib.EdfReader("S001R01.edf")

f2 = pyedflib.EdfReader("S001R02.edf")
n = f1.signals_in_file
m = f2.signals_in_file
signal_labels_1 = f1.getSignalLabels()
signal_labels_2 = f2.getSignalLabels()
sigbufs1 = np.zeros((n, f1.getNSamples()[0]),dtype = float)
sigbufs2 = np.zeros((n, f2.getNSamples()[0]),dtype = float)
for signal in np.arange(n):
    sigbufs1[signal, :] = f1.readSignal(signal)

for signal in np.arange(m):
    sigbufs2[signal, :] = f2.readSignal(signal)

f1._close()
f2._close()

f3 = pd.read_csv("Test_Subject_1.csv")

a = pd.DataFrame(data=sigbufs1)
b = pd.DataFrame(data=sigbufs2)
a = a.append(b)
a.to_csv("Unknown_test_subject.csv", sep=',',encoding='utf-8', index=False)
#b.to_csv("Test_Subject_1_2.csv", sep=',',encoding='utf-8', index=False)
'''
a = pd.read_csv("Test_Subject_1.csv")
b = pd.read_csv("Test_Subject_2.csv")
c = pd.read_csv("Test_Subject_3.csv")
d = pd.read_csv("Test_Subject_4.csv")
e = pd.read_csv("unknown_test_subject.csv")
a.to_csv("TrainData.csv",mode = 'a',sep = ",",encoding='utf-8', index=False)
b.to_csv("TrainData.csv",mode = 'a',sep = ",",encoding='utf-8', index=False)
c.to_csv("TrainData.csv",mode = 'a',sep = ",",encoding='utf-8', index=False)
d.to_csv("TrainData.csv",mode = 'a',sep = ",",encoding='utf-8', index=False)
e.to_csv("TrainData.csv",mode = 'a',sep = ",",encoding='utf-8', index=False)


#b = a.sample(n=5)
#b.to_csv("TestSubject4_Testdata.csv", sep=',',encoding='utf-8', index=False)
