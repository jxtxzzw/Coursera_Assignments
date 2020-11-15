# 神经网络和深度学习

Coursera 专项课程 Deep Learning Specialization （由 DeepLearning.AI 提供）的第 1 课 Neural Networks and Deep Learning 的学习笔记

## 深度学习简介

### 深度学习概论

ReLU：Rectified Linear Unit

![](https://img.jxtxzzw.com/2020/10/19/pr2bi1.png)

+ Standard NN
+ Convolutional NN：卷积神经网络，通常用于图像数据
+ Recurrent NN：循环神经网络，非常适合处理一维数据、其中包含时间成分

![](https://img.jxtxzzw.com/2020/10/19/prncbs.png)

训练更大规模的神经网络，并投入足够多的数据

## 神经网络基础

### 神经网络基础知识

逻辑回归是一个二元分类算法，例如：输入是一张图片，输出 $$0$$ 或者 $$1$$ 表示是不是猫

在神经网络中使用按列排列会相对简单一些，$$X$$ 是一个 $$n_x \times m$$ 维的矩阵，$$n_x$$ 是向量长度，$m$ 是样本数量，$$Y$$ 是一个 $$1 \times m$$ 维的矩阵

Given $$x$$, want $$\hat{y} = P(y = 1 | x)$$, $$x \in \mathbb{R}^{n_x}$$，Parameters: $$w \in \mathbb{R}^{n_x}, b \in \mathbb{R}$$，Output：$$\hat{y} = w^Tx + b$$（对线性回归）

为了 $$\hat{y}$$ 取值在 $$[0, 1]$$ 之间，需要使用 sigmoid 函数 $$\sigma(z) = \frac{1}{1+e^{-z}}$$

![](https://img.jxtxzzw.com/2020/10/19/psc0cc.png)

通常会将 $$w$$ 和 $$b$$ 区分开，当作相互独立的变量，$$b$$ 表示偏移量

在一些课程中还会增加常数项 $$x_0 = 1$$，于是维度就变成 $$n_x + 1$$

#### 逻辑回归的损失函数

$$L(\hat{y}, y) = \frac{1}{2}(\hat{y} - y)^2$$ 在一般的洛基回归中并不好，因为会变成非凸问题，所以会产生多个局部最优解，梯度下降也不会找到全局最优解

常用的损失函数：$$L(\hat{y}, y) = -(y \log{\hat{y}} + (1-y) \log{(1-\hat{y})})$$

#### 代价函数

$$J(w, b) = \frac{1}{m} \sum_{i=1}^{m}{L(\hat{y^{(i)}}, y^{(i)})}$$

> What is the difference between the cost function and the loss function for logistic regression?
>
> The loss function computes the error for a single training example; the cost function is the average of the loss functions of the entire training set.

$$\hat{y} = w^Tx + b$$

$$\sigma(z) = \frac{1}{1+e^{-z}}$$

$$J(w, b) = \frac{1}{m} \sum_{i=1}^{m}{L(\hat{y^{(i)}}, y^{(i)})}$$

希望找到一个 $$w, b$$ 使得 $$J(w, b)$$ 最小

代价函数 $$J$$ 是一个凸函数（convex function）

$$w := w - \alpha \frac{\mathrm{d}J(w)}{\mathrm{d}w}$$

![](https://img.jxtxzzw.com/2020/10/19/qguqbx.png)

$$J(a, b, c) = 3(a + b \cdot c)$$

想要知道 $$a$$ 增大一点点，$$J$$ 会变化多少，就要求 $$J$$ 对 $$a$$ 的导数，这一部分就被称为反向传播

链式法则：$$\frac{\mathrm{d}J}{\mathrm{d}a} = \frac{\mathrm{d}J}{\mathrm{d}v} \frac{\mathrm{d}v}{\mathrm{d}a}$$

### 逻辑回归

Recap:

$$z = w^Tx + b$$

$$\hat{y} = a = \sigma(z) = \frac{1}{1+e^{-z}}$$

$$L(a, y) = - ( y \log (a) + (1 - y) \log (1 - a))$$

$$\frac{\mathrm{d}L(a,y)}{\mathrm{d}a} = -\frac{y}{a} + \frac{1 - y}{1 - a}$$

$$\frac{\mathrm{d}L(a, y)}{\mathrm{d}z} = \frac{\mathrm{d}L}{\mathrm{d}a} \cdot \frac{\mathrm{d}a}{\mathrm{d}z} = (-\frac{y}{a} + \frac{1 - y}{1 - a}) \cdot (a \cdot (1 - a)) = a - y$$

*对 Sigmoid 函数成立：*$$\frac{\mathrm{d}a}{\mathrm{d}z} = a \cdot (1 - a)$$

### 矢量化

#### 尽量避免显式的 for 循环

```python
import time

a = np.random.rand(1000000)
b = np.random.rand(1000000)

tic = time.time()
c = np.dot(a, b)
toc = time.time()

print(c)
print("Vectorized version: " + str(1000*(toc - tic)) + "ms")

c = 0
tic = time.time()
for i in range(1000000):
    c += a[i] * b[i]
toc = time.time()

print(c)
print("For loop: " + str(1000*(toc - tic)) + "ms")
```
```
250341.47647084185
Vectorized version: 1.0027885437011719ms
250341.47647084462
For loop: 710.904598236084ms
```

针对 CPU/GPU 的并行优化：SIMD 单指令集多数据集

调用 `numpy` 的内置函数
```
import math
n = 1000000
u = np.zeros((n, 1))

tic = time.time()
for i in range(n):
    u[i] = math.exp(u[i])
toc = time.time()
print(str(1000*(toc - tic)) + "ms")

tic = time.time()
np.exp(u)
toc = time.time()
print(str(1000*(toc - tic)) + "ms")
```
```
643.2774066925049ms
10.971546173095703ms
```

#### 使用矢量化优化逻辑回归的前向传播和后向传播
在逻辑回归中，使用 2 个向量计算代替 2 个 for 循环
![](https://img.jxtxzzw.com/2020/10/19/qhvdap.png)

$$Z = W^T + b$$

$$A = \sigma(Z)$$

$$dz = A - Y$$

$$dw = \frac{1}{m} X dz^T$$

$$db = \frac{1}{m} sum(dz)$$

$$w = w - \alpha dw$$

$$b = b - \alpha db$$

#### Python 广播

|   | Apples | Beef | Eggs | Potatoes |
| ------------ | ------------ | ------------ | ------------ | ------------ |
| Carb | 56.0 | 0.0 | 4.4 | 68.0 |
| Protein | 1.2 | 104.0 | 52.0 | 8.0 |
| Fat | 1.8 | 135.0 | 99.0 | 0.9 |

```python
import numpy as np
A = np.array([[56.0, 0.0, 4.4, 68.0],
             [1.2, 104.0, 52.0, 8.0],
             [1.8, 135.0, 99.0, 0.9]])
print(A)
cal = A.sum(axis=0) # axis=0 表示垂直相加，axis=1 表示水平求和
print(cal)
percentage = 100 * A / cal.reshape(1,4)
print(percentage)
```

如果你有一个 $$4 \times 1$$ 的向量，让它加一个数字，Python 会自动将这个数字扩展成一个 $$4 \times 1$$ 的向量

如果你有一个 $$m \times n$$ 的向量，让它加一个 $$(1, n)$$ 的向量，Python 会自动将这个向量扩展成一个 $$m \times n$$ 的向量（复制每一行 $$m$$ 次）

如果你有一个 $$m \times n$$ 的向量，让它加一个 $$(m, 1)$$ 的向量，Python 会自动将这个向量扩展成一个 $$m \times n$$ 的向量（复制每一列 $$n$$ 次）

![](https://img.jxtxzzw.com/2020/10/19/qi00ap.png)

#### 其他注意事项

![](https://img.jxtxzzw.com/2020/10/19/qhxixw.png)

a.shape = (5,) 既不是行向量，也不是列向量，这是一个 rank 1 array

不要使用这种秩为 1 的数组

要显式地确保这是一个 (5, 1) 的列向量，或者一个 (1, 5) 的行向量

```python
assert(a.shape == (5, 1))
a = a.reshape((5, 1)) # 将秩为 1 的数组转化为一个列向量
```

## 浅层神经网络

### 神经网络表示

#### 神经网络的输入层、隐藏层、输出层

1 个圆代表 2 步计算：
![](https://img.jxtxzzw.com/2020/10/19/qihf7f.png)

隐藏层把这个步骤重复了很多遍

$$a_{i}^{[l]}$$，$$i$$ 表示第 $$i$$ 个结点、$$l$$ 表示第 $$l$$ 层。

将 $$z_1^{[1]} = w_1^{[1]T}x + b_1^{[1]}, a_1^{[1]} = \sigma(z_1^{[1]})$$ 矢量化

![](https://img.jxtxzzw.com/2020/10/19/qikrb5.png
)

给出 $$x^{(i)}$$ 可以得到 $$a^{{[2]}{(i)}} = \hat{y}^{(i)}$$

$$[i]$$ 表示第几层，$$(i)$$ 表示第几个训练样本

```
for i = 1 to m
	z[1][i] = w[1] x[i] + b[1]
	a[1][i] = sigmoid(z[1][i])
	z[2][i] = w[2] a[1][i] + b[2]
	a[2][i] = sigmoid(z[2][i])
```

从左到右（横向看）：训练实例
从上往下（纵向看）：隐藏单元

![](https://img.jxtxzzw.com/2020/10/19/qinket.png)

### 激活函数

1. Sigmoid 函数

2. $$\displaystyle{a = \tanh(z) = \frac{e^{z} - e^{-z}}{e^{z} + e^{-z}}}$$ 使学习下一层更容易

3. ReLU: $$a = \max(0, z)$$实际上是不可微的，但是由于计算机中浮点数的定义，$$z = 0$$ 时可以认为导数为 $$1$$ 或者 $$0$$，然后继续

4. 由于$$x < 0$$ 时导数为 $$0$$，实际上并不好用，所以 Leaky ReLU $$a = \max(0.00001z, z) $$通常更优，但是实践中不怎么用（由于对于大量的样本，情况一般在正半轴，不怎么出现导数为 $$0$$ 的情况，所以实践中跑的速度通常较快）

#### 为什么需要非线性的激活函数

经过各隐藏层后，最终输出 $$y = w\'x+b\'$$ 只是输入的线性变化，所以还不如去掉全部的隐藏层

输入是实数域的，输出也是实数域的，从负无穷到正无穷

使用线性激活函数的地方通常是输出层，否则，除非是为了数据压缩，不会在隐藏层使用线性的激活函数

#### 激活函数的斜率

记 $$\frac{\mathrm{d}}{\mathrm{dz}} g(z)$$ 为 $$g\'(z) = $$

记 $$g(z) = a$$

1. Sigmoid

$$g(z) = \frac{1}{1 + e^{-z}}$$

$$\displaystyle{\frac{\mathrm{d}}{\mathrm{dz}} g(z) = \frac{1}{1 + e^{-z}} (1 - \frac{1}{1 + e^{-z}}) = g(z) (1 - g(z))}$$

因此 $$g'(z) = a (1 - a)$$

2. tanh

$$g'(z) = 1 -a^2$$

3. ReLU

当 $$z < 0$$ 时为 $$0$$，当 $$z > 0$$ 时为 $$1$$，当 $$z = 0$$ 时没有定义

实际操作中，$$z = 0$$ 时可设置为 $$1$$

4. Leaky ReLU

$$g(z) = \max(0.01z, z)$$

当 $$z < 0$$ 时为 $$0.01$$，当 $$z > 0$$ 时为 $$1$$，当 $$z = 0$$ 时可以设为 $$1$$

### 梯度下降

+ 前向传播

$$Z^{[1]} = W^{[1]} X + b^{[1]}$$

$$A^{[1]} = g^{[1]}(Z^{[1]})$$

$$Z^{[2]} = W^{[2]} A^{[1]} + b^{[2]}$$

$$A^{[2]} = g^{[2]}(Z^{[2]})$$

+ 后向传播

$$\mathrm{d}z^{[2]} = A^{[2]} - Y$$

$$\mathrm{d}w^{[2]} = \frac{1}{m} \mathrm{d}z^{[2]} A^{[1]T}$$

$$db^{[2]} = \frac{1}{m} np.sum(\mathrm{d}z^{[2]}, axis = 1, keepdims = True)$$

+ `np.sum(dz2, axis = 1, keepdims = True)` 中 `axis = 1` 表示水平方向求和，`keepdims = True` 防止 Python 输出奇怪的秩为 1 的数组 `(n,)` 而是 `(n,1)`。

$$\mathrm{d}z^{[1]} = W^{[2]T} dz^{[2]} * g'^{[1]}(Z^{[1]})$$

+ 这里 `*` 是 element-wise product

+ 这里 $$W^{[2]T} \mathrm{d}z^{[2]}$$ 和 $$g'^{[1]}(Z^{[1]})$$ 都是 $$(n^{[1]}, m)$$ 的矩阵

$$\mathrm{d}w^{[1]} = \frac{1}{m} \mathrm{d}z^{[1]} X^{T}$$

$$db^{[1]} = \frac{1}{m} np.sum(\mathrm{d}z^{[1]}, axis = 1, keepdims = True)$$

### 随机初始化

在逻辑回归中把权重参数初始化为 0 是可行的

但是把神经网络的权重参数全部初始化为 0 并使用梯度下降将无法得到预期的效果

![](https://img.jxtxzzw.com/2020/10/19/qjt2ks.png)

把 b 矩阵初始化为 0 是 OK 的，但是把 W 矩阵初始化为 0 会导致无论使用什么样本进行训练，所有激活函数将进行完全一致的工作，因此得到的结果是一样的，反向传播中的导数也不会有差别，那么这些隐含层就是对称的，于是，在一次训练后，得到了功能完全一致的隐含层

```python
w[1] = np.random.randn((2, 2)) * 0.01
b[1] = np.zeros((2, 1))
w[2] = np.random.randn((2, 2)) * 0.01
b[2] = np.zeros((2, 1))
```

为什么乘以 0.01 而不是 100 之类的数？对于 tanh 或者 sigmoid 函数，步长太大，a 的值就会很大，梯度下降会很慢

## 深度神经网络

### 什么是深度神经网络？

逻辑回归： 1 layer NN

1 个隐藏层： 2 layer NN

……

多个隐藏层： 深度神经网络

#### 深度神经网络记号

大写 $$L$$ 表示总层数，层数 L 包括隐藏层和输出层，不包括输入层

$$n^{[l]}$$ 表示第 $$l$$ 层有几个神经元

$$a^{[l]}$$ 表示第 $$l$$ 层的激活函数

$$a^{[l]} = g^{[l]}(Z^{[l]})$$

#### 在深度神经网络中进行前向和后向传播

$$Z^{[l]} = W^{[l]} a^{[l - 1]} + b^{[l]}$$

$$a^{[l]} = g^{[l]}(z^{[l]})$$

可使用矢量化优化 $$Z$$ 和 $$A$$ 的计算，但是从 $$1$$ 到 $$L$$ 层的计算只能显式地用 `for` 循环，没有办法不使用 `for` 循环

#### 矩阵的维数

由于 $$z^{[l]}$$ 是一个 $$(n^{[l]}, 1)$$ 的矢量，且 $$x$$ 是一个 $$(n^{[l - 1]}, 1)$$ 的矢量，由此可以反推 $$w$$ 需要是一个 $$(n^{[l]}, n^{[l -1]})$$ 的矢量

$$b[l]$$ 显然应该和 $$z^{[l]}$$ 维度一样，所以是一个 $$(n^{[l]}, 1)$$ 的矢量

$$db$$ 与 $$b$$ 维度一样，$$dw$$ 与 $$w$$ 维度一样

矢量化后，$$Z^{[l]}$$ 和 $$A^{[l]}$$ 是 $$(n^{[l]}, m)$$ 的矢量，$$W^{[l]}$$ 是 $$(n^{[l]}, n^{[l - 1]})$$ 的矢量，$$\mathrm{d}Z^{[l]}$$ 和 $$\mathrm{d}A^{[l]}$$ 是 $$(n^{[l]}, m)$$ 的矢量

#### 深度神经网络为何有效

+ 前面的层做简单的，后面的层做整合的，循序渐进

+ 浅层（指隐藏层数量少）的神经网络可能需要指数级的神经元，而使用深层神经网络可以更简单

### 构建深度神经网络

#### 前向

第 $$l$$ 层的前向传播：输入 $$a^{[l - 1]}$$，输出 $$a^{[l]}$$，并缓存 $$z^{[l]}, w^{[l]}, b^{[l]}$$

$$z^{[l]} = w^{[l]} a^{[l - 1]} + b^{[l]}$$

$$a^{[l]} = g^{[l]}(z^{[l]})$$

矢量化

$$Z^{[l]} = W^{[l]} A^{[l - 1]} + b^{[l]}$$

$$A^{[l]} = g^{[l]}(Z^{[l]})$$

#### 反向

第 $$l$$ 层的反向传播：输入 $$da^{[l]}$$，输出 $$da^{[l - 1]}, dW^{[l]}, db^{[l]}$$

$$dz^{[l]} = da^{[l]} * g'^{[l]}(z^{[l]})$$

$$dw^{[l]} = dz^{[l]} a^{[l - 1]T}$$

$$db^{[l]} = dz^{[l]}$$

$$da^{[l - 1]} = w^{[l]T} dz^{[l]}$$

矢量化

$$dZ^{[l]} = dA^{[l]} * g'^{[l]}(Z^{[l]})$$

$$dW^{[l]} = \frac{1}{m} dZ^{[l]} A^{[l - 1]T}$$

$$db^{[l]} = \frac{1}{m} np.sum(dZ^{[l]}, axis = 1, keepdim = True)$$

$$dA^{[l - 1]} = W^{[l]T} dZ^{[l]}$$

#### 参数与超常

目的：使神经网络运作更高效

参数是 $$w$$ 和 $$b$$

超参数有：学习率 $$\alpha$$，迭代次数、隐藏层的层数 $$L$$、隐藏神经元个数 $$n^{[l]}$$，激活函数 $$g(\cdot)$$，以及之后的课程会涉及到的动量、mini_batch_size 等等

超参数会最终影响参数 $$w$$ 和 $$b$$