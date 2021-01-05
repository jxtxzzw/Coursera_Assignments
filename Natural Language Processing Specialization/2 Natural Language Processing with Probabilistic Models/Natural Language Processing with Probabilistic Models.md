自然语言处理-概率模型

> Coursera 专项课程 Natural Language Processing Specialization（由 DeepLearning.AI 提供）的第 2 课 Natural Language Processing with Probabilistic Models 的学习笔记

# 自动纠错

将 `Happy birthday deah friend` 修改为 `Happy birthday my dear friend`，实现 `deah` 到 `dear` 的自动纠错（拼写错误）

但是不会涉及 将 `Happy birthday deer friend` 修改为 `Happy birthday my dear friend`：当 `deer` 拼写正确但放在语境中含义不正确时，需要额外的算法来完成这样的纠错

纠错的步骤：

1. 找到拼写错误的单词
2. 找到 n 编辑距离
3. 找到拼写正确的候选词
4. 计算每一个候选词出现的概率

对字符串的一个修改被称为一次编辑，例如：

+ 插入：`to`  ->  `top`、`to` -> `two`
+ 删除：`hat` -> `ha`、`hot` -> `ht`
+ 交换：`eta` -> `eat`、`eta` -> `tea`（但注意 `eta` -> `ate` 不属于交换）
+ 修改：`jaw` -> `jar`

```python
def delete_letter(word, verbose=False):
    delete_l = []
    split_l = []
    
    length = len(word)
    for i in range(length):
        split_l.append((word[:i], word[i:]))
        delete_l.append(word[:i] + word[i + 1:])
    
    return delete_l
```

```python
def edit_one_letter(word, allow_switches = True):
    
    edit_one_set = set()
    
    lst = []
    lst += insert_letter(word)
    lst += delete_letter(word)
    lst += replace_letter(word)
    if allow_switches:
        lst += switch_letter(word)
    edit_one_set = set(lst)

    return edit_one_set
```



# 最小编辑距离

`play` 到 `stay` 有 2 个字母发生变化，编辑为 2，类似地，我们可以计算出插入和删除的字母个数

编辑个数乘上编辑代价就是编辑距离，习惯上：插入和删除的代价是 1，而替换的代价是 2

利用 DP 求解最小编辑距离

$$
D[i,j] = min\left\{
\begin{aligned}
D[i - 1, j] & + del\_cost \\
D[i, j - 1] & + ins\_cost \\
D[i - 1, j - 1] & + \left\{
\begin{aligned}
& rep\_cost & (if \quad src[i] = tar[j]) \\
& 0 & (if \quad src[i] \neq tar[j])
\end{aligned}
\right.
\end{aligned}
\right.
$$

```python
def min_edit_distance(source, target, ins_cost = 1, del_cost = 1, rep_cost = 2):
    # use deletion and insert cost as  1
    m = len(source) 
    n = len(target) 
    #initialize cost matrix with zeros and dimensions (m+1,n+1) 
    D = np.zeros((m+1, n+1), dtype=int) 
    
    # Fill in column 0, from row 1 to row m, both inclusive
    for row in range(0, m + 1): # Replace None with the proper range
        D[row,0] = row
        
    # Fill in row 0, for all columns from 1 to n, both inclusive
    for col in range(0, n + 1): # Replace None with the proper range
        D[0,col] = col
        
    # Loop through row 1 to row m, both inclusive
    for row in range(1, m + 1): 
        
        # Loop through column 1 to column n, both inclusive
        for col in range(1, n + 1):
            
            # Intialize r_cost to the 'replace' cost that is passed into this function
            r_cost = rep_cost
            
            # Check to see if source character at the previous row
            # matches the target character at the previous column, 
            if source[row - 1] == target[col - 1]:
                # Update the replacement cost to 0 if source and target are the same
                r_cost = 0
                
            # Update the cost at row, col based on previous entries in the cost matrix
            # Refer to the equation calculate for D[i,j] (the minimum of three calculated costs)
            D[row,col] = min(D[row - 1, col - 1] + r_cost, D[row - 1, col] + del_cost, D[row, col - 1] + ins_cost)
          
    # Set the minimum edit distance with the cost found at row m, column n
    med = D[m, n]
    
    return D, med
```



# 马尔科夫链与隐马尔科夫模型

马尔可夫模型是一种随机模型，描述了一系列可能的事件

例如，在一个动词后面，更有可能接一个名词，而不是再一个动词

用有向（可能有环）图表示这个模型

马尔科夫属性：不需要来自任何以前状态的信息，前往的下一个状态是哪一个，这概率只取决于当前状态

将马尔科夫链的图表示转化为 $N \times N$ 的矩阵表示可以使数据存储更紧凑，这个矩阵叫过渡矩阵

我们习惯上使用 $\pi$ 表示初始状态 (initial)



隐马尔可夫模型用来描述一个含有隐含未知参数的马尔可夫过程

其难点是从可观察的参数中确定该过程的隐含参数，然后利用这些参数来作进一步的分析，例如模式识别

在正常的马尔可夫模型中，状态对于观察者来说是直接可见的

如果状态 $t1$ 有概率前往隐藏状态 $w1$，我们称这个概率为发射概率



## 计算过渡矩阵

1. 计算标签对 $C(t_{i - 1}, t_i)$ 出现的次数
2. 计算 $P(t_i|t_{i-1}) = \frac{C(t_{i - 1}, t_i)}{\sum_{j=1}^{N}C(t_{i-1},t_j)}$
3. 必要时采用 $\epsilon$ 平滑

```python
def create_transition_matrix(alpha, tag_counts, transition_counts):
    # Get a sorted list of unique POS tags
    all_tags = sorted(tag_counts.keys())
    
    # Count the number of unique POS tags
    num_tags = len(all_tags)
    
    # Initialize the transition matrix 'A'
    A = np.zeros((num_tags,num_tags))
    
    # Get the unique transition tuples (previous POS, current POS)
    trans_keys = set(transition_counts.keys())
    
    # Go through each row of the transition matrix A
    for i in range(num_tags):
        
        # Go through each column of the transition matrix A
        for j in range(num_tags):

            # Initialize the count of the (prev POS, current POS) to zero
            count = 0
        
            # Define the tuple (prev POS, current POS)
            # Get the tag at position i and tag at position j (from the all_tags list)
            key = (all_tags[i], all_tags[j])

            # Check if the (prev POS, current POS) tuple 
            # exists in the transition counts dictionary
            if key in transition_counts: #complete this line
                
                # Get count from the transition_counts dictionary 
                # for the (prev POS, current POS) tuple
                count = transition_counts[key]
                
            # Get the count of the previous tag (index position i) from tag_counts
            count_prev_tag = tag_counts[all_tags[i]]
            
            # Apply smoothing using count of the tuple, alpha, 
            # count of previous tag, alpha, and total number of tags
            A[i,j] = (count + alpha) / (count_prev_tag + alpha * num_tags)

    return A
```

```python
def create_emission_matrix(alpha, tag_counts, emission_counts, vocab):
    # get the number of POS tag
    num_tags = len(tag_counts)
    
    # Get a list of all POS tags
    all_tags = sorted(tag_counts.keys())
    
    # Get the total number of unique words in the vocabulary
    num_words = len(vocab)
    
    # Initialize the emission matrix B with places for
    # tags in the rows and words in the columns
    B = np.zeros((num_tags, num_words))
    
    # Get a set of all (POS, word) tuples 
    # from the keys of the emission_counts dictionary
    emis_keys = set(list(emission_counts.keys()))
    
    # Go through each row (POS tags)
    for i in range(num_tags): # complete this line
        
        # Go through each column (words)
        for j in range(num_words): # complete this line

            # Initialize the emission count for the (POS tag, word) to zero
            count = 0
                    
            # Define the (POS tag, word) tuple for this row and column
            key = (all_tags[i], vocab[j])

            # check if the (POS tag, word) tuple exists as a key in emission counts
            if key in emission_counts: # complete this line
        
                # Get the count of (POS tag, word) from the emission_counts d
                count = emission_counts[key]
                
            # Get the count of the POS tag
            count_tag = tag_counts[all_tags[i]]
                
            # Apply smoothing and store the smoothed value 
            # into the emission matrix B for this row and column
            B[i,j] = (count + alpha) / (count_tag + alpha * num_words)
    return B
```



## Viterbi 算法

初始化

$ if A[s_{idx}, i] <> 0 : best\_probs[i,0] = log(A[s_{idx}, i]) + log(B[i, vocab[corpus[0]]])$

$ if A[s_{idx}, i] == 0 : best\_probs[i,0] = float('-inf')$

```python
def initialize(states, tag_counts, A, B, corpus, vocab):
    # Get the total number of unique POS tags
    num_tags = len(tag_counts)
    
    # Initialize best_probs matrix 
    # POS tags in the rows, number of words in the corpus as the columns
    best_probs = np.zeros((num_tags, len(corpus)))
    
    # Initialize best_paths matrix
    # POS tags in the rows, number of words in the corpus as columns
    best_paths = np.zeros((num_tags, len(corpus)), dtype=int)
    
    # Define the start token
    s_idx = states.index("--s--")
    
    # Go through each of the POS tags
    for i in range(num_tags): # complete this line
        
        # Handle the special case when the transition from start token to POS tag i is zero
        if A[s_idx, i] == 0: # complete this line
            
            # Initialize best_probs at POS tag 'i', column 0, to negative infinity
            best_probs[i,0] = float('-inf')
        
        # For all other cases when transition from start token to POS tag i is non-zero:
        else:
            
            # Initialize best_probs at POS tag 'i', column 0
            # Check the formula in the instructions above
            best_probs[i,0] = math.log(A[s_idx, i]) + math.log(B[i, vocab[corpus[0]]])
                        
    return best_probs, best_paths
```



### 前向传递

$\mathrm{prob} = \mathbf{best\_prob}_{k, i-1} + \mathrm{log}(\mathbf{A}_{k, j}) + \mathrm{log}(\mathbf{B}_{j, vocab(corpus_{i})})$

```python
def viterbi_forward(A, B, test_corpus, best_probs, best_paths, vocab):
    # Get the number of unique POS tags (which is the num of rows in best_probs)
    num_tags = best_probs.shape[0]
    
    # Go through every word in the corpus starting from word 1
    # Recall that word 0 was initialized in `initialize()`
    for i in range(1, len(test_corpus)): 
        
        # Print number of words processed, every 5000 words
        if i % 5000 == 0:
            print("Words processed: {:>8}".format(i))
            
        # For each unique POS tag that the current word can be
        for j in range(num_tags): # complete this line
            
            # Initialize best_prob for word i to negative infinity
            best_prob_i = float('-inf')
            
            # Initialize best_path for current word i to None
            best_path_i = None

            # For each POS tag that the previous word can be:
            for k in range(num_tags): # complete this line
            
                # Calculate the probability = 
                # best probs of POS tag k, previous word i-1 + 
                # log(prob of transition from POS k to POS j) + 
                # log(prob that emission of POS j is word i)
                prob = best_probs[k, i-1] + math.log(A[k, j]) + math.log(B[j, vocab[test_corpus[i]]])

                # check if this path's probability is greater than
                # the best probability up to and before this point
                if prob > best_prob_i: # complete this line
                    
                    # Keep track of the best probability
                    best_prob_i = prob
                    
                    # keep track of the POS tag of the previous word
                    # that is part of the best path.  
                    # Save the index (integer) associated with 
                    # that previous word's POS tag
                    best_path_i = k

            # Save the best probability for the 
            # given current word's POS tag
            # and the position of the current word inside the corpus
            best_probs[j,i] = best_prob_i
            
            # Save the unique integer ID of the previous POS tag
            # into best_paths matrix, for the POS tag of the current word
            # and the position of the current word inside the corpus.
            best_paths[j,i] = best_path_i

    return best_probs, best_paths
```



### 后向传递

```python
def viterbi_backward(best_probs, best_paths, corpus, states):
    # Get the number of words in the corpus
    # which is also the number of columns in best_probs, best_paths
    m = best_paths.shape[1] 
    
    # Initialize array z, same length as the corpus
    z = [None] * m
    
    # Get the number of unique POS tags
    num_tags = best_probs.shape[0]
    
    # Initialize the best probability for the last word
    best_prob_for_last_word = float('-inf')
    
    # Initialize pred array, same length as corpus
    pred = [None] * m
    
    ## Step 1 ##
    
    # Go through each POS tag for the last word (last column of best_probs)
    # in order to find the row (POS tag integer ID) 
    # with highest probability for the last word
    for k in range(num_tags): # complete this line

        # If the probability of POS tag at row k 
        # is better than the previously best probability for the last word:
        if best_probs[k, m - 1] > best_prob_for_last_word: # complete this line
            
            # Store the new best probability for the last word
            best_prob_for_last_word = best_probs[k, m - 1]
    
            # Store the unique integer ID of the POS tag
            # which is also the row number in best_probs
            z[m - 1] = k
            
    # Convert the last word's predicted POS tag
    # from its unique integer ID into the string representation
    # using the 'states' list
    # store this in the 'pred' array for the last word
    pred[m - 1] = states[z[m - 1]]
    
    ## Step 2 ##
    # Find the best POS tags by walking backward through the best_paths
    # From the last word in the corpus to the 0th word in the corpus
    for i in range(m - 1, -1, -1): # complete this line
        
        # Retrieve the unique integer ID of
        # the POS tag for the word at position 'i' in the corpus
        pos_tag_for_word_i = corpus[i]
        
        # In best_paths, go to the row representing the POS tag of word i
        # and the column representing the word's position in the corpus
        # to retrieve the predicted POS for the word at position i-1 in the corpus
        z[i - 1] = best_paths[z[i], i]
        
        # Get the previous word's POS tag in string form
        # Use the 'states' list, 
        # where the key is the unique integer ID of the POS tag,
        # and the value is the string representation of that POS tag
        pred[i - 1] = states[z[i - 1]]
        
    return pred
```



# 自动补全

训练一个 n 元语言模型可以：

+ 语音识别：P(I saw a van) > P(eyes awe of an)
+ 单词纠正：将 He entered the ship to buy 修改为 He entered the shop to buy，注意这与之前的自动纠错不同，这里 ship 也是词典中的单词



# n 元语言模型

n-gram 指的是一个由 $n$ 个单词组成的序列（顺序很重要）

例如 `I am happy because I am learning`： 

+ Unigrams: `{I, am, happy, because, learning}`

+ Bigrams: `{I am, am happy, happy because, ….}`

根据二元概率，$P(am|I)$ 表示出现了 `I` 之后出现 `am` 的概率，这是 $\frac{C(I am)}{C(I)}$，由于在这个语料库中，$C(I am)=2$，而 $C(I)=2$，所以认为出现 `I` 之后有 $100%$ 的概率出现 `am`，即 `I am` 一定连在一起出现

类似的，我们得到了三元概率、四元概率……
$$
P(w_N|w_1^{N-1}) = \frac{C(w_1^{N-1}w_N)}{C(w_1^{N-1})}
$$
马尔科夫假设：下一个单词出现的概率，只与最后 N 个单词相关，而无所谓之前的单词

因此，整个句子在二元语言模型下的概率为 $P(w_1^n)\approx\Pi_{i=1}^{n}P(w_i|w_{i-1})$

用 `<s>` 解决最起始的单词缺少前一个单词的问题：`The teacher` 变为 `<s> <s> The teacher`，这样在进行三元语言模型计算的时候，就可以用 $P(The \ teacher|<s>)$，类似地，用 `</s>` 解决句子结尾的问题

## 计算 n 元语言模型

$$
\hat{P}(w_t | w_{t-1}\dots w_{t-n}) = \frac{C(w_{t-1}\dots w_{t-n}, w_n)}{C(w_{t-1}\dots w_{t-n})}
$$
+ 为了避免概率相乘下溢出的问题，改用概率的对数相加

+ Perplexity 评估一个句子是不是看起来是人类写的，而不是一串随机的单词，该值越小，模型越好

$$
PP(W) =\sqrt[N]{ \prod_{t=n+1}^N \frac{1}{P(w_t | w_{t-n} \cdots w_{t-1})} }
$$

+ 为了处理没有在训练数据中出现的单词，我们可以将出现次数小于一定次数的单词替换为 `<UNK>`

```python
for word, cnt in word_counts.items(): # complete this line
    if cnt >= count_threshold:
        closed_vocab.append(word)
```

```python
for token in sentence:
    if token in vocabulary:
        replaced_sentence.append(token)
    else:
        replaced_sentence.append(unknown_token)
```

+ 平滑

$$
\hat{P}(w_t | w_{t-1}\dots w_{t-n}) = \frac{C(w_{t-1}\dots w_{t-n}, w_n) + k}{C(w_{t-1}\dots w_{t-n}) + k|V|}
$$

# 词向量

## one-hot

将所有单词依次排列组成向量，对应单词为 1、其余单词为 0

优点

+ 简单
+ 没有隐含顺序

缺点

+ 向量太大
+ 单词之间不包含意义

## 词嵌入

例如一个一维的数轴，用越靠近右半轴表示积极、越靠近左半轴表示消极，则 rage 可能是 -2.52、boring 是 -0.91、happy 是 1.75、excited 是 2.31，中立的单词靠近 0，例如 paper 0.03

可以拓展更多的维度，例如，增加抽象和具体的维度，在 y 轴上，越是抽象的单词，y 值越大，这时候 paper 可能是 (0.03, 0.98)、excited 可能是 (2.31, -0.66)、thought 可能是 (0.03, -0.98)

优点

+ 省空间
+ 能够直观表示单词之间的相似程度
+ 有具体的意义

缺点

+ 有些单词可能重叠，例如 snake 和 spider，可能是同一个点 (-0.66, 1.45)



## 构造嵌入词向量

语料库必须是完整的上下文，例如莎士比亚原文，而不能是经过处理的数据（例如有关莎士比亚研究的 PPT）

训练方法有很多，以机器学习为例，这里机器学习会用到自监督，自监督=无监督+有监督（self-supervised = unsupervised + supervised）



## 常用的词向量

+ word2vec (Google, 2013)
+ Global Vector (GloVe) (Standord, 2014)
+ fastText (Facebook, 2016)

高级词向量

+ BERT (Google, 2018)
+ ELMo (Allen Institute for AI, 2018)
+ GPT-2 (OpenAI, 2018)



使用外部评价的优点

+ 可以评估事实上的有用性

使用外部评价的缺点

+ 耗时
+ 难以排查故障



# 语料库处理的技巧

1. 应该处理成大小写不敏感

   通常全部变成小写

2. 处理标点符号

   例如将 `,` `!` `?` `!!` 变成 `.`

   而将 `“` `<<` 变成空集，即忽略

3. 数字

   例如 1 2 3 5 8，可以变成空集，即忽略

   但有些语料库中，数字可能有是有意义的，这种时候可以原样保留，例如保留 2000、1234，或者用字符串 `<NUMBER>` 替换（如果不关心数字的具体值）

4. 特殊符号

   通常变成空集，即忽略

5. 表情、带`#`的标签

   将表情转化为对应的单词，例如将笑脸转化为 happy

   视情况，将标签也转化为单词，例如将 `#fwd` 转化为 forward，如果不关心的话，也可以丢弃

   

   