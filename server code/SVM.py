#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import pandas as pd
from sklearn import preprocessing
from sklearn.cross_validation import train_test_split
from sklearn.svm import SVC
from sklearn.metrics import accuracy_score
import sys
#testFile = sys.argv[1]
testFile = "TestSubject1_Testdata.csv"
data = pd.read_csv('TrainData.csv',header = None)
test = pd.read_csv(testFile, header = None)
label = list(data[data.shape[1]-1])
testL = list(test[test.shape[1]-1])
testLabelContent = list(set(test[test.shape[1]-1]))
data = data.drop(data.shape[1]-1,axis = 1)
test = test.drop(test.shape[1]-1,axis = 1)

data = preprocessing.scale(data)
test = preprocessing.scale(test)

svmModel = SVC(kernel = 'linear', C = 1).fit(data, label)
prediction = svmModel.predict(test)
count = 0
for i in prediction:
    if i in testLabelContent:
        count = count + 1
accuracy = accuracy_score(testL, prediction, normalize = True)
print(accuracy)
if count > 0:
    print('1')
