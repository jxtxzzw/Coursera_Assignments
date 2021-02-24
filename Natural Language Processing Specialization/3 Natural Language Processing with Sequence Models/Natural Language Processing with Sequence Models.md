自然语言处理-序列模型

> Coursera 专项课程 Natural Language Processing Specialization（由 DeepLearning.AI 提供）的第 3 课 Natural Language Processing with Sequence Models 的学习笔记

# 用于情感分析的神经网络

![](https://img.jxtxzzw.com/2021/01/06/sj9lpl.png)



# 语言模型

## 传统语言模型

N-grams（n 元语言模型）

$P(w_2|w_1) = \frac{count(w_1, w_2)}{count(w_1)}$

$P(w_3|w_1,w_2) = \frac{count(w_1, w_2, w_3)}{count(w_1,w_2)}$

$P(w_1, w_2, w_3) = P(w_1) \times P(w_2|w_1) \times P(w_3|w_2)$

缺陷：

+ 需要庞大的数据库来表示长 n 元模型中单词之间的距离
+ 需要大量空间和内存

## 循环神经网络

RNN 不只看最近的 n 个单词，而是传播之前的每一个单词的含义

例如 I called her but she did not \____ 这个例子中，三元组 did not X 中，did not have、did not eat、did not answer 并不能提供额外的信息

RNN 传播之前的每一个单词的含义

![](https://img.jxtxzzw.com/2021/01/17/w03cxr.png)



一对多：给一张图片，生成一段描述该图片的文字

多对一：给一段文本，判断这段文本包含的情感

多对多：将一系列法语单词转化为英语中的等价单词



交叉熵

$P(W) = \sqrt[N]{\prod_{i=1}^{N} \frac{1}{P(w_i| w_1,...,w_{n-1})}}$



![](https://img.jxtxzzw.com/2021/01/25/xjnzj4.png)

梯度消失问题：随着计算的边长，之前的 消息会丢失

![](https://img.jxtxzzw.com/2021/01/25/xj13t5.png)

GRU 可以控制从之前的数据中忘记哪些信息，以及从现在的数据中获得多少信息





RNN 有梯度消失的问题



LSTM 是解决方案之一



LSTM 使用一系列的门来决定哪些信息需要保存：

+ Forget 门决定哪些要保持
+ Input 门决定哪些要增加
+ Output 门决定下一个隐藏状态会是什么
+ 



NER locates and extracts predefined entities from text



![](https://img.jxtxzzw.com/2021/02/06/3z0ryc.png)

