# 自然语言处理-分类和向量空间

> Coursera 专项课程 Natural Language Processing Specialization（由 DeepLearning.AI 提供）的第 1 课 Natural Language Processing with Classification and Vector Spaces 的学习笔记

## 使用逻辑回归的情感分析

### 数据预处理

+ 去除超链接、标签和样式

  可以使用正则表达式

+ 分词

+ 去除停用词

  ```python
  stopwords_english = stopwords.words('english') 
  ```

+ 提取词干

  ```python
  stemmer = PorterStemmer() 
  stem_word = stemmer.stem(word)
  ```

  例如，将 happy、happiniess、happier 变成 happi

### 频率特征提取

将编码表示成维度为 3 的向量

<img src="https://img.jxtxzzw.com/2020/09/15/nqdtsh.png" style="zoom: 33%;" />

```python
#bias term is set to 1
x[0,0] = 1 

# loop through each word in the list of words
for word in word_l:

    # increment the word count for the positive label 1
    x[0,1] += freqs[(word, 1)] if (word, 1) in freqs else 0

    # increment the word count for the negative label 0
    x[0,2] += freqs[(word, 0)] if (word, 0) in freqs else 0
```

### 逻辑回归

有 Features $X$ 和 Labels $Y$，目标是降低错误率

对 $X$ 运用预测函数（参数为 $\theta$）得到输出 $Y$，比较预测值和实际值之间的差距，并调整参数，使误差最小

<img src="https://img.jxtxzzw.com/2020/09/15/lqnwru.png" style="zoom: 67%;" />

预测函数使用 Sigmoid 函数 $\displaystyle h(x^{(i)}, \theta) = \frac{1}{1+e^{-\theta^Tx^{(i)}}}$

![img](https://img.jxtxzzw.com/2020/10/13/qsixvt.png)

+ 初始化权重
+ 利用权重计算分类、预测
+ 获得梯度数据，并根据梯度数据更新权重

预测时计算 $h(X_{val}, \theta)$，并和 $0.5$ 比较，若大于，则认为是 positive，否则，negative

根据 $$ \displaystyle \sum_{i=1}^{m}\frac{pred^{(i)} == y_{val}^{(i)}}{m}$$ 计算准确率

$\displaystyle -\frac{1}{m} \sum_{i=1}^{m} [y^{(i)} \log h(x^{(i)}, \theta) + (1 - y^{(i)}) \log (1 - h(x^{(i)}, \theta))]$

$\displaystyle y^{(i)} \log h(x^{(i)}, \theta)$

| $y^{i}$ | $h(x^{(i)}, \theta)$ | Overall Cost |
| ------- | -------------------- | ------------ |
| 0       | any                  | 0            |
| 1       | 0.99                 | ~0           |
| 1       | ~0                   | -inf         |

$\displaystyle (1 - y^{(i)}) \log (1 - h(x^{(i)}, \theta))$

| $y^{i}$ | $h(x^{(i)}, \theta)$ | Overall Cost |
| ------- | -------------------- | ------------ |
| 1       | any                  | 0            |
| 0       | 0.01                 | ~0           |
| 0       | ~1                   | -inf         |

当标签是 0 或者 1 时，公式中的某一部分会起作用，表明预测值和实际值之间的差距，即 Cost

### 代码实现

$\displaystyle h(z) = \frac{1}{1+\exp^{-z}}$

<img src="https://img.jxtxzzw.com/2020/09/16/m1v546.png" style="zoom:50%;" />

```python
def sigmoid(z): 
    '''
    Input:
        z: is the input (can be a scalar or an array)
    Output:
        h: the sigmoid of z
    '''
    
    h = 1 / (1 + np.exp(-z))
    
    return h
```

$\displaystyle J = \frac{-1}{m} \times \left(\mathbf{y}^T \cdot log(\mathbf{h}) + \mathbf{(1-y)}^T \cdot log(\mathbf{1-h}) \right)$

$\displaystyle \mathbf{\theta} = \mathbf{\theta} - \frac{\alpha}{m} \times \left( \mathbf{x}^T \cdot \left( \mathbf{h-y} \right) \right)$

![](https://img.jxtxzzw.com/2020/10/13/qst9km.png)

```python
def gradientDescent(x, y, theta, alpha, num_iters):
    '''
    Input:
        x: matrix of features which is (m,n+1)
        y: corresponding labels of the input matrix x, dimensions (m,1)
        theta: weight vector of dimension (n+1,1)
        alpha: learning rate
        num_iters: number of iterations you want to train your model for
    Output:
        J: the final cost
        theta: your final weight vector
    Hint: you might want to print the cost to make sure that it is going down.
    '''

    # get 'm', the number of rows in matrix x
    m = x.shape[0]
    
    for i in range(0, num_iters):
        
        # get z, the dot product of x and theta
        z = np.dot(x, theta)
        
        # get the sigmoid of z
        h = sigmoid(z)
        
        # calculate the cost function
        J = -1 / m * (np.dot(y.T, np.log(h)) + np.dot((1 - y).T, np.log(1 - h)))

        # update the weights theta
        theta = theta - alpha / m * np.dot(x.T, h - y)
        
    J = float(J)
    return J, theta
```

利用梯度下降训练模型

```python
# collect the features 'x' and stack them into a matrix 'X'
X = np.zeros((len(train_x), 3))
for i in range(len(train_x)):
    X[i, :]= extract_features(train_x[i], freqs)

# training labels corresponding to X
Y = train_y

# Apply gradient descent
J, theta = gradientDescent(X, Y, np.zeros((3, 1)), 1e-9, 1500)
print(f"The cost after training is {J:.8f}.")
print(f"The resulting vector of weights is {[round(t, 8) for t in np.squeeze(theta)]}")
```

$\displaystyle y_{pred} = sigmoid(\mathbf{x} \cdot \theta)$ 可以用来预测情感

```python
def predict_tweet(tweet, freqs, theta):
    '''
    Input: 
        tweet: a string
        freqs: a dictionary corresponding to the frequencies of each tuple (word, label)
        theta: (3,1) vector of weights
    Output: 
        y_pred: the probability of a tweet being positive or negative
    '''
    
    # extract the features of the tweet and store it into x
    x = extract_features(tweet, freqs)
    
    # make the prediction using x and theta
    y_pred = sigmoid(np.dot(x, theta))
    
    return y_pred
```

评价准确率

```python
def test_logistic_regression(test_x, test_y, freqs, theta):
    """
    Input: 
        test_x: a list of tweets
        test_y: (m, 1) vector with the corresponding labels for the list of tweets
        freqs: a dictionary with the frequency of each pair (or tuple)
        theta: weight vector of dimension (3, 1)
    Output: 
        accuracy: (# of tweets classified correctly) / (total # of tweets)
    """
    
    # the list for storing predictions
    y_hat = []
    
    for tweet in test_x:
        # get the label prediction for the tweet
        y_pred = predict_tweet(tweet, freqs, theta)
        
        if y_pred > 0.5:
            # append 1.0 to the list
            y_hat.append(1)
        else:
            # append 0 to the list
            y_hat.append(0)

    # With the above implementation, y_hat is a list, but test_y is (m,1) array
    # convert both to one-dimensional arrays in order to compare them using the '==' operator
    m = len(y_hat)
    y_hat = np.reshape(np.array(y_hat), (m, 1))
    accuracy = np.sum((test_y == y_hat)) / m 
    
    return accuracy
```

### 数学推导

梯度：

一般形式的梯度定义是 $\theta_j = \theta_j - \alpha \frac{\partial}{\partial \theta_j} J(\theta)$

$\forall j$，我们可以使用 $\theta_j = \theta_j - \frac{\alpha}{m} \sum_{i=1}^{m}(h(x^{(i)}, \theta) - y^{(i)})x_j^{(i)}$ 求得梯度

用向量来表示就是

$\displaystyle{\theta = \theta - \frac{\alpha}{m}X^{T}(H(X, \theta)-Y)}$

$J(\theta)$ 的偏导：

首先，我们计算 Sigmoid 函数的导数，这对于我们寻找 $J(\theta)$ 的偏导很有帮助：

$\begin{align*}h(x)'&=\left(\frac{1}{1+e^{-x}}\right)'=\frac{-(1+e^{-x})'}{(1+e^{-x})^2}=\frac{-1'-(e^{-x})'}{(1+e^{-x})^2}=\frac{0-(-x)'(e^{-x})}{(1+e^{-x})^2}=\frac{-(-1)(e^{-x})}{(1+e^{-x})^2}=\frac{e^{-x}}{(1+e^{-x})^2} \newline &=\left(\frac{1}{1+e^{-x}}\right)\left(\frac{e^{-x}}{1+e^{-x}}\right)=h(x)\left(\frac{+1-1 + e^{-x}}{1+e^{-x}}\right)=h(x)\left(\frac{1 + e^{-x}}{1+e^{-x}} - \frac{1}{1+e^{-x}}\right)=h(x)(1 - h(x))\end{align*}$

注意我们用 $\theta_j$ 对 $h(x^{(i)}, \theta)$ 求导，我们会得到 $h(x^{(i)}, \theta)(1-h(x^{(i)}, \theta))x_j^{(i)}$

$\begin{align*}\frac{\partial}{\partial \theta_j} J(\theta) &= \frac{\partial}{\partial \theta_j} \frac{-1}{m}\sum_{i=1}^m \left [ y^{(i)} log ( h(x^{(i)}, \theta) ) + (1-y^{(i)}) log (1 -  h(x^{(i)}, \theta)) \right ] \newline&= - \frac{1}{m}\sum_{i=1}^m \left [     y^{(i)} \frac{\partial}{\partial \theta_j} log ( h(x^{(i)}, \theta))   + (1-y^{(i)}) \frac{\partial}{\partial \theta_j} log (1 -  h(x^{(i)}, \theta))\right ] \newline&= - \frac{1}{m}\sum_{i=1}^m \left [     \frac{y^{(i)} \frac{\partial}{\partial \theta_j}  h(x^{(i)}, \theta)}{ h(x^{(i)}, \theta)}   + \frac{(1-y^{(i)})\frac{\partial}{\partial \theta_j} (1 -  h(x^{(i)}, \theta))}{1 -  h(x^{(i)}, \theta)}\right ] \newline&= - \frac{1}{m}\sum_{i=1}^m \left [     \frac{y^{(i)} \frac{\partial}{\partial \theta_j}  h(x^{(i)}, \theta)}{ h(x^{(i)}, \theta)}   + \frac{(1-y^{(i)})\frac{\partial}{\partial \theta_j} (1 -  h(x^{(i)}, \theta))}{1 -  h(x^{(i)}, \theta)}\right ] \newline&= - \frac{1}{m}\sum_{i=1}^m \left [     \frac{y^{(i)}  h(x^{(i)}, \theta) (1 -  h(x^{(i)}, \theta)) \frac{\partial}{\partial \theta_j} \theta^T x^{(i)}}{ h(x^{(i)}, \theta)}   + \frac{- (1-y^{(i)})  h(x^{(i)}, \theta)(1 -  h(x^{(i)}, \theta)) \frac{\partial}{\partial \theta_j} \theta^T x^{(i)}}{1 -  h(x^{(i)}, \theta)}\right ] \newline&= - \frac{1}{m}\sum_{i=1}^m \left [     \frac{y^{(i)}  h(x^{(i)}, \theta) (1 -  h(x^{(i)}, \theta)) \frac{\partial}{\partial \theta_j} \theta^T x^{(i)}}{ h(x^{(i)}, \theta)}   - \frac{(1-y^{(i)}) h(x^{(i)}, \theta) (1 -  h(x^{(i)}, \theta)) \frac{\partial}{\partial \theta_j} \theta^T x^{(i)}}{1 -  h(x^{(i)}, \theta))}\right ] \newline&= - \frac{1}{m}\sum_{i=1}^m \left [     y^{(i)} (1 -  h(x^{(i)}, \theta)) x^{(i)}_j - (1-y^{(i)})  h(x^{(i)}, \theta) x^{(i)}_j\right ] \newline&= - \frac{1}{m}\sum_{i=1}^m \left [     y^{(i)} (1 -  h(x^{(i)}, \theta)) - (1-y^{(i)})  h(x^{(i)}, \theta) \right ] x^{(i)}_j \newline&= - \frac{1}{m}\sum_{i=1}^m \left [     y^{(i)} - y^{(i)}  h(x^{(i)}, \theta) -  h(x^{(i)}, \theta) + y^{(i)}  h(x^{(i)}, \theta) \right ] x^{(i)}_j \newline&= - \frac{1}{m}\sum_{i=1}^m \left [ y^{(i)} -  h(x^{(i)}, \theta) \right ] x^{(i)}_j  \newline&= \frac{1}{m}\sum_{i=1}^m \left [  h(x^{(i)}, \theta) - y^{(i)} \right ] x^{(i)}_j\end{align*}$

## 使用朴素贝叶斯的情感分析

### 概率、条件概率和贝叶斯规则

条件概率指当 A 发生的前提下，B 发生的概率

$\displaystyle{P(X|Y) = \frac{P(X \and Y)}{P(Y)}, P(Y|X) = \frac{P(X \and Y)}{P(X)}}$

贝叶斯规则是条件概率的推导

$\displaystyle{P(X|Y) = P(Y|X) \frac{P(X)}{P(Y)}}$

朴素贝叶斯是简单的二元分类：利用 $\displaystyle{\Pi_{i=1}^{m}\frac{P(w_i|pos)}{P(w_i|neg)}}$ 计算概率

### 使用贝叶斯规则时的技巧

1. 拉普拉斯光滑可以避免概率为 $0$

$\displaystyle{P(w_i|class) = \frac{freq(w_i, class) + 1}{N_{class} + V}}$

其中 $N_{class}$ 是该类别下所有单词出现的次数，$V$ 是所有 unique 单词的个数

2. *Log Likelihoods* 可以避免数据下溢出

$ratio(w_i) = \frac{P(w_i|Pos)}{P(w_i|Neg)}$：中性的单词为 $1$；积极的单词大于 $1$，数字越大、越积极；消极的单词小于 $1$，数字越小，越消极

使用 $\frac{P(pos)}{P(neg)}\displaystyle{\Pi_{i=1}^{m}}\frac{P(w_i|pos)}{P(w_i|neg)}$ 计算概率，乘积有数据下溢出的风险

根据 $\log(a*b) = \log(a) + \log(b)$，有 $\log(\frac{P(pos)}{P(neg)}\displaystyle{\Pi_{i=1}^{m}}\frac{P(w_i|pos)}{P(w_i|neg)}) = \log(\frac{P(pos)}{P(neg)}) + \sum_{i=1}^{m}\log(\frac{P(w_i|pos)}{P(w_i|neg)})$

$\lambda(w) = \log\frac{P(w_i|Pos)}{P(w_i|Neg)}$，然后 $\displaystyle{\sum_{i=1}^{m}\lambda(w_i)}$，将结果与 $0$ 比较，正为积极、负为消极，绝对值越大表示积极或消极的程度越大

### 训练、测试朴素贝叶斯，以及误差分析

+ 数据清洗
  + 变成小写
  + 移除语气词、URLs、名字
  + 消除停用词
  + 分词
  + 词干提取
  
+ 数据预处理
  + 向量化
  + 构建频数词典
  
+ 计算先验概率 log prior

  $\displaystyle{P(D_{pos})=\frac{D_{pos}}{D}}$、$\displaystyle{P(D_{neg})=\frac{D_{neg}}{D}}$

  $\text{logprior} = log \left( \frac{P(D_{pos})}{P(D_{neg})} \right) = log \left( \frac{D_{pos}}{D_{neg}} \right)$

  由于 $\log(\frac{A}{B})=\log{A}-\log{B}$，$\text{logprior} = \log (P(D_{pos})) - \log (P(D_{neg})) = \log (D_{pos}) - \log (D_{neg})$

  ```python
  # Calculate D, the number of documents
  D = len(train_y)
  # Calculate D_pos, the number of positive documents (*hint: use sum(<np_array>))
  D_pos = np.sum(train_y)
  # Calculate D_neg, the number of negative documents (*hint: compute using D and D_pos)
  D_neg = D - D_pos
  # Calculate logprior
  logprior = np.log(D_pos) - np.log(D_neg)
  ```

+ 计算一个单词的正负概率：$P(W_{pos}) = \frac{freq_{pos} + 1}{N_{pos} + V}$、$P(W_{neg}) = \frac{freq_{neg} + 1}{N_{neg} + V}$

  + 对于没有出现在频率表中的单词，认为是中性单词

  ```python
  # calculate V, the number of unique words in the vocabulary
  vocab = set([pair[0] for pair in freqs.keys()])
  V = len(vocab)
  # calculate N_pos and N_neg
  N_pos = N_neg = 0
  for pair in freqs.keys():
      # if the label is positive (greater than zero)
      if pair[1] > 0:
      # Increment the number of positive words by the count for this (word, label) pair
          N_pos += freqs[pair]
      # else, the label is negative
      else:
          # increment the number of negative words by the count for this (word,label) pair
          N_neg += freqs[pair]
          
   # For each word in the vocabulary...
  for word in vocab:
      # get the positive and negative frequency of the word
      freq_pos = freqs[(word, 1)] if (word, 1) in freqs else 0
      freq_neg = freqs[(word, 0)] if (word, 0) in freqs else 0
      # calculate the probability that each word is positive, and negative
      p_w_pos = (freq_pos + 1) / (N_pos + V)
      p_w_neg = (freq_neg + 1) / (N_neg + V)
  ```

+ 计算 log likelihood：$\text{loglikelihood} = \log \left(\frac{P(W_{pos})}{P(W_{neg})} \right)$

  ```python
  # calculate the log likelihood of the word
  loglikelihood[word] = np.log(p_w_pos / p_w_neg)
  ```

+ 预测：用 $p = logprior + \sum_i^N (loglikelihood_i)$ 计算出这句话属于不同分类的概率

  ```python
  # initialize probability to zero
  p = 0
  # add the logprior
  p += logprior
  for word in word_l:
      # check if the word exists in the loglikelihood dictionary
      if word in loglikelihood:
          # add the log likelihood of that word to the probability
          p += loglikelihood[word]
  ```

+ 测试：用 $\frac{1}{m} \displaystyle{\sum_{i=1}^{m}(pred_{i} == Y_{val_i})}$ 计算准确率

  ```python
  y_hats = []
  for tweet in test_x:
      # if the prediction is > 0
      if naive_bayes_predict(tweet, logprior, loglikelihood) > 0:
          # the predicted class is 1
          y_hat_i = 1
      else:
          # otherwise the predicted class is 0
          y_hat_i = 0
      # append the predicted class to the list y_hats
      y_hats.append(y_hat_i)
  
  # error is the average of the absolute values of the differences between y_hats and test_y
  error = 1 / len(test_y) * np.sum(y_hats != test_y)
  # Accuracy is 1 minus the error
  accuracy = 1 - error
  ```

+ 误差分析

  最主要的误差可能来自：
  + 标点符号

    `My beloved grandmother :(` 中的 `:(` 暗示了作者的情绪是负面的，可能祖母生病住院了、或者离世了，但去除标点符号后，`beloved` 可能一个积极的情绪

  + 移除单词

    将 because、not、even 去掉之后，语气和情感会发生变化

  + 单词的顺序

    `I am happy because I did not go.` 和 `I am not happy because I did go.` 说的是同一件事情，就是庆幸自己没去，即自己去了之后很不开心，然而会得到不同的情感分类

  + 对抗性攻击

    讽刺、反讽和委婉语

有些单词的积极程度比有些单词的积极程度更高，除了使用 log likelihood，我们还可以使用 ratio 来表示单词正面或者负面的程度

```python
def get_ratio(freqs, word):
    '''
    Input:
        freqs: dictionary containing the words
        word: string to lookup

    Output: a dictionary with keys 'positive', 'negative', and 'ratio'.
        Example: {'positive': 10, 'negative': 20, 'ratio': 0.5}
    '''
    pos_neg_ratio = {'positive': 0, 'negative': 0, 'ratio': 0.0}
    # use lookup() to find positive counts for the word (denoted by the integer 1)
    pos_neg_ratio['positive'] = lookup(freqs, word, 1)
    # use lookup() to find negative counts for the word (denoted by integer 0)
    pos_neg_ratio['negative'] = lookup(freqs, word, 0)
    # calculate the ratio of positive to negative counts for the word
    pos_neg_ratio['ratio'] = (pos_neg_ratio['positive'] + 1) / (pos_neg_ratio['negative'] + 1)
    
    return pos_neg_ratio
```

```python
def get_words_by_threshold(freqs, label, threshold):
    '''
    Input:
        freqs: dictionary of words
        label: 1 for positive, 0 for negative
        threshold: ratio that will be used as the cutoff for including a word in the returned dictionary
    Output:
        word_set: dictionary containing the word and information on its positive count, negative count, and ratio of positive to negative counts.
        example of a key value pair:
        {'happi':
            {'positive': 10, 'negative': 20, 'ratio': 0.5}
        }
    '''
    word_list = {}

    for key in freqs.keys():
        word, _ = key

        # get the positive/negative ratio for a word
        pos_neg_ratio = get_ratio(freqs, word)

        # if the label is 1 and the ratio is greater than or equal to the threshold...
        if label == 1 and pos_neg_ratio['ratio'] >= threshold:

            # Add the pos_neg_ratio to the dictionary
            word_list[word] = pos_neg_ratio

        # If the label is 0 and the pos_neg_ratio is less than or equal to the threshold...
        elif label == 0 and pos_neg_ratio['ratio'] <= threshold:

            # Add the pos_neg_ratio to the dictionary
            word_list[word] = pos_neg_ratio

        # otherwise, do not include this word in the list (do nothing)

    return word_list
```

### 朴素贝叶斯的应用

朴素贝叶斯进行分类的本质：计算属于各个类别的概率

朴素贝叶斯进行了如下假设：

1. 独立性

2. 依赖训练数据的频率分布，容易得到过分乐观和悲观的模型

朴素贝叶斯可以用来解决其他的问题：

+ 情感分析

+ 作者识别

+ 信息获取

  $P(document_k|query) \propto \Pi_{i=0}^{|query|}P(query_i|document_k)$，Retrieve document if $P(document_k|query) > threshold$

+ 单词二义性选择

  Bank 指的是河岸，还是银行？

  例如 $\frac{P(river|text)}{P(money|text)}$，或者 financial、natural……

## 向量空间模型

Where are you heading 和 Where are you from 只有一个单词不一样，但是含义完全不同；How old are you 和 What is your age 的单词完全不同，但是它们有相同的含义

向量空间模型可以把相似含义的文本放在互相靠近的位置

### 将上下文转变为向量表示

统计单词之间相差不超过 $k$ 个距离的前提下，单词一起出现的次数，例如当 $k=2$ 时，对于 I like simple data. I prefer simple raw data. 这句话的表示，应为

|      | simple | raw  | like | I    |
| ---- | ------ | ---- | ---- | ---- |
| data | 2      | 1    | 1    | 0    |

其中 data 和 simple 一起出现，且最长距离为 $k = 2$ 的有 2 次，为 simple data 和 simple raw data

又如

|      | 娱乐 | 经济 | 机器学习 |
| ---- | ---- | ---- | -------- |
| 数据 | 500  | 6620 | 9320     |
| 电影 | 7000 | 4000 | 1000     |

将这些数据绘制在图表中，根据距离和角度决定它们的相似性

![](https://img.jxtxzzw.com/2020/11/10/7tw7v71.png)

计算欧几里得距离是一种判断相似度的方法，距离越小，则约相似

但数据集的规模不一致，可能导致计算得到的距离不一定能代表真实的相似度

```python
d = np.linalg.norm(a - b)
```

在实际应用中，余弦相似度更常用

$\cos (\theta)=\frac{\mathbf{A} \cdot \mathbf{B}}{\|\mathbf{A}\|\|\mathbf{B}\|}=\frac{\sum_{i=1}^{n} A_{i} B_{i}}{\sqrt{\sum_{i=1}^{n} A_{i}^{2}} \sqrt{\sum_{i=1}^{n} B_{i}^{2}}}$

```python
beta = np.dot(a, b) / (np.linalg.norm(a) * np.linalg.norm(b))
```

### 词嵌入

通过词向量的加减操作可以预测相关联的词语

![](https://img.jxtxzzw.com/2020/11/11/wcm83z.png)

例如将国家 USA 减去美国首都 Washington，得到向量差值，将 Russia 加上该差值即可求得俄罗斯的首都是什么

```python
capital = vec('France') - vec('Paris')
country = vec('Madrid') + capital

print(country[0:5]) # Print the first 5 values of the vector

find_closest_word(country) # 'Spain'

find_closest_word(vec('Italy') - vec('Rome') + vec('Madrid')) # 'Spain'

find_closest_word(vec('Berlin') + capital) # Germany
find_closest_word(vec('Beijing') + capital) # China
```

### PCA

减少特征的维数，并尽可能保留原样的特征信息

## 实例

### 机器翻译

对于原语言词库中的每一个单词的向量表示 $X$，和目标语言词库中的每一个单词的向量表示 $Y$，找到一个变换规则 $R$ 使得 $XR$ 和 $Y$ 之间的距离最小

那么，将原语言单词 $x$ 的词向量表示通过变换规则 $R$ 更新为新的词向量 $y$，在目标语言中找到最接近新向量 $y$ 的词向量 $z$，则 $z$ 为翻译后的单词

损失函数：$L(X, Y, R)=\frac{1}{m}\sum_{i=1}^{m} \sum_{j=1}^{n}\left( a_{i j} \right)^{2}$

梯度：$\frac{d}{dR}L(X,Y,R)=\frac{d}{dR}\Big(\frac{1}{m}\| X R -Y\|_{F}^{2}\Big) = \frac{2}{m}X^{T} (X R - Y)$

### K-近邻算法和哈希

为了找到目标语言中找到最接近新向量 $y$ 的词向量 $z$，我们需要 K-近邻算法

为了方便进行 K-近邻查找，我们可以使用哈希表把相似的元素放在一起

Locality Sensitive Hashing 方法是处理词向量哈希时常用的方法

```python
def side_of_plane(P, v):
    dotproduct = np.dot(P, v.T)
    sign_of_dot_product = np.sign(dotproduct)
    sign_of_dot_product_scalar = np.asscalar(sign_of_dot_product)
```

判断向量的方向是否接近相等，来判断是否是相似的元素

我们可以使用多个 plane，然后根据一定的规则将它们组合在一起

例如根据 plane 1，我们发现其符号为 +，则记 $h_1 = 1$，根据 plane 2，我们发现其符号为 +，则记 $h_2 = 1$，根据 plane 3，我们发现其符号为 -，则记 $h_3 = 0$，最后得到哈希值为 $2^0 \times 1 + 2^1 \times 1 + 2^3 \times 0 = 3$

![](https://img.jxtxzzw.com/2020/11/14/pbk3ny.png)

### 文档搜索

将文档表示成词向量：把每一个词的词向量加起来

用 K-NN 搜索文档

