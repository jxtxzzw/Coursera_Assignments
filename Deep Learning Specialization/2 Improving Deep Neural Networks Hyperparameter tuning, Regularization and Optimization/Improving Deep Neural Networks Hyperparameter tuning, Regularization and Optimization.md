# 提高深度神经网络：超参数调整、正则化和优化

> Coursera 专项课程 Deep Learning Specialization （由 DeepLearning.AI 提供）的第 2 课 Improving Deep Neural Networks: Hyperparameter tuning, Regularization and Optimization 的学习笔记

## 深度学习的实践情况

### 基本原则

#### 训练集、开发集、测试集

**开发集的作用是评估哪一个模型更好**

最佳实践是训练:测试=7:3，或者训练:开发:测试=6:2:2

在大数据时代，开发集的作用只需要评估哪一个模型更好，所以可选训练:开发:测试=98:1:1，甚至99.5:0.4:0.1

确保开发集和测试集中的数据分布是类似的（例如图像分辨率、上传渠道等）

> Rule of thumb: Make sure dev and test come from same distribution

**测试集的目的是进行一个无偏估计，可以没有测试集，而只有开发集**

#### 偏差（偏离度）和方差（集中度）

![](https://img.jxtxzzw.com/2020/10/19/qodl1j.png)

+ high bias --> unfer fitting
+ high variance -> over fitting

| 训练集错误 | 开发集错误 | 定义 | 现象 |
| ------------ | ------------ | ------------ | ------------ |
| 1% | 11% | 高方差 | 过拟合 |
| 15% | 16% | 高偏差 | 欠拟合 |
| 15% | 30% | 高方差、高偏差 |  |
| 0.5% | 1% | 低方差、低偏差 |  |

*假设人工识别的误差为 0*

> 如果 理想误差（贝叶斯误差）是 15%，那么这时候看第二行的数据，(15%, 16%) 就是可以接受的，而不是高偏差了

#### 机器学习的基本原则

高偏差（训练集上都不能拟合很好）：

+ 更多的隐藏层
+ 延长训练时间
+ 更高级的优化算法

高方差（不具有一般化）：

+ 获取更多的数据
+ 正则化
+ 更高级的优化算法

### 正则化

#### L2 正则化

##### 逻辑回归的 L2 正则化

$$J(w, b) = \frac{1}{m} \sum_{i = 1} ^ {m} L(\hat{y^{i}}, y^{i}) + \frac{\lambda}{2m} {\left\|\left\|w\right\|\right\|_{2}}^2$$

$$w \in \mathbb{R}^{n_x}, b \in \mathbb{R}$$

$$\displaystyle{{\left\|\left\|w\right\|\right\|_{2}}^2}$$ = \sum_{j=1}^{n_x} w_j^2 = w^T w$$

为什么只正则化 $$w$$，而不把 $$b$$ 的相关项也加进去？

实际上可以，但是因为 $$w$$ 往往是一个非常高维的参数，而出现问题是因为没能拟合所有的参数，而 $$b$$ 只是大量参数中的一个，所以可以不加上 $$\frac{\lambda}{2m} b^2$$，加上也起不到大用处

如果不使用 L2 范式 $${\left\|\left\|\cdot\right\|\right\|}_2^2$$，使用 L1

$$\frac{\lambda}{m}{\left\|\left\|w\right\|\right\|_1} = \frac{\lambda}{m} \sum_{j=1}^{n_x}|w_j|$$ 最后会使得 $w$ 变得稀疏（$$w$$ 矢量有很多 $$0$$）

除以 $$m$$ 还是 $$2m$$ 都可以，只是一个系数

$$\lambda$$ 被称为正则化参数（regularization parameter），通常使用开发集或者 hold-out 交叉验证来配置这个参数：保持 L2 范式较小以避免过拟合

##### 神经网络的 L2 正则化

$$J(w^{[1]}, b^{[1]}, \cdots, w^{[L]}, b^{[L]}) = \frac{1}{m} \sum_{i = 1} ^ {m} L(\hat{y^{i}}, y^{i}) + \frac{\lambda}{2m} \sum_{l=1}^{L} {\left\|\left\|w^{[l]}\right\|\right\|}^2$$

$${\left\|\left\|w^{[l]}\right\|\right\|} = \sum_{i = 1}^{n^[l]} \sum_{j = 1}^{n^{[l - 1]}} (w_{ij}^{[l]})^2$$

$$w$$ 是一个 $$(n^{[l]}, n^{[l - 1]})$$ 的矩阵

由于一些神秘的原因，这个矩阵的范式没有被叫做 L2 范式，而是被叫做 Frobenius Norm，记作 $${\left\|\left\|\cdot\right\|\right\|}_F^2$$。

L2 正则化也被称为权重衰减（weight decay），因为代入公式后第一项小于 0

##### 为什么 L2 正则化能防止过拟合

如果把 $$\lambda$$ 设置为很大，由于代价函数最终会被优化到很小，所以 $$\lambda$$ 乘上的那个 $$w$$ 就会被优化到很小

*A = B + CD *，目标是 A 很小，B 固定，C 被设置很大，所以 D 会很小

相当于把很多隐藏单元的权重设置为 $$0$$，很多权重的影响就被消除了

于是就把一个很大的神经网络变成一个很小的神经网络，最终变成了线性的（逻辑回归），就不容易过拟合

但是可能会变得偏差很大，但是总是存在一个中间值让它变得刚刚好

J 包含了第二项，那么每次迭代后都单调递减，如果只包含原来的第一项，那么可能不是单调递减的，可以利用训练时的这个图像来验证自己的 L2 正则化

#### 随机失活正则化

以一定的概率保留和消除任意的节点

##### 反向随机失活

```python
# 给定隐藏单元被保留的概率
keep_prob = 0.8

# 以第 3 层为例，生成一个随机的矩阵，其中元素右 0.8 的概率被设置为 1，0.2 的概率被设置为 0，是一个 True/False
d3 = np.random.rand(a3.shape[0], a3.shape[1]) < keep_prob

# a3 是原来计算得到的矩阵
# 相乘，把去掉的单元清除掉，True/False 当做 1/0 来算
a3 = np.multiply(a3, d3)
# 放大，提供校正值，这样 a3 的期望值就不会变（z4 = w4 a3 + b4）
a3 /= keep_prob
```

注意在测试阶段没有 drop out，否则会增加噪声

除以 keep_prob 的目的是在测试阶段没有针对随机失活算法进行缩放（scaling），那么激活函数的输出期望值也不会变，就不需要引入额外的参数了

##### 原理

随机失活，就好像是在一个更小的神经网络中训练，就好像是做了正则化

同时，每一个输入都可能随机失活，所以不会依赖于任何一个神经元，所以不会把很大的权重放在任意一个神经元上

##### 说明

因此，事实上，与 L2 正则化的惩罚不一样，随机失活更多是自适应的，不太算正则化

神经元多的层可以设置一个低一点的存活率，对于神经元少的层不用太担心过拟合，可以设置高一点的存活率

可以在输入层做失活，当实践中不这么做，一般把输入层的留存率设置为 1 或者 0.9

相对计算机视觉领域，因为输入数据太大了，所以默认会使用 dropout，但是其他领域，除非已经过拟合了，否则不要随便用 dropout

#### 其他正则化方法简介

1. 水平翻转图像、随机裁剪（旋转 + 缩放）图像后加入训练集

2. early stopping，在 dev set error 开始增加的时候，停止训练

### 优化问题

#### 归一化输入

1. 将均值归零（减去均值）
2. 将方差归一化

$$\mu = \frac{1}{m} \sum{i = 1}^{m}{x^{(i)}}$$

$$x = x - \mu$$

$$\sigma^2 = \frac{1}{m} \sum_{i = 1}^{m} {x^{(i)}}^2$$

$$x = x / \sigma$$

训练集和测试集用一样的 $$\mu$$ 和 $$\sigma$$ 处理

如果不归一化，$$w1, w2, \cdots$$ 的取值会区别很大，$$\alpha$$ 需要设置很小，并且需要经过很多曲折才能找到最小值，而如果是对称的圆，无论从哪里开始，都会很快下降到最小值

#### 梯度消失和梯度爆炸

在训练很深的神经网络时，损失函数的导数会变得非常大或者非常小，使得训练变得很困难，可以通过谨慎选择初始化的权重来减少问题的发生

设 $$g(z) = z, b^{[l]} = 0$$

$$y = w^{[L]} w^{[L - 1]} w^{[L - 2]} \cdots w^{[3]} w^{[2]} w^{[1]} x$$ 中

假设所有 $$w$$ 都一样，那么，y = w^{L - 1} x$$

哪怕 $$w$$ 只比 $$1$$ 大一点点，在一个很深的网络中，$$y$$ 也会变得很大，哪怕只比 $$1$$ 小一点点，在一个很深的网络中，$$y$$ 也会指数级变小

#### 权重初始化

更细致地随机初始化可以部分解决这个问题

单个神经元

$$z = w_1 x_1 + w_2 x_2 + \cdots + w_n x_n + b$$

若 $$b = 0$$，为了让 $$z$$ 不会太大也不会太小，随着 $$n$$ 的增大，$$w_i$$ 应该越小

一种合理的做法是，让 $$w_i$$ 的方差为 $$\frac{2}{n}$$，$$n$$ 是神经元的特征数

```python
w[l] = np.random.randn(shape) * np.sqrt(2 / n[l - 1])
```

这里用 ReLU 为激活函数 g(z)

`n[l - 1]` 表示 $$n^{[ l - 1]}$$

事实上，用高斯随机并乘以这一项的平方根也是可以的

用 tanh 的话，系数 1 会比系数 2 要好，就是 `np.sqrt(1 / n[l - 1])`

也可能会用到 `np.sqrt(2 / (n[l - 1] + n[l]))`

### 梯度检验

#### 如何在数值上近似计算梯度

取双侧差值，而不是单侧差值

$$\displaystyle{\frac{f(\theta + \epsilon) - f(\theta - \epsilon)} {2\epsilon} \approx g(\theta)}$$

#### 梯度检查

Take $$W^{[1]}, b^{[1]}, \cdots, W^{[L]}, b^{[L]}$$ and reshape into a big vector $$\theta$$

$$J($W^{[1]}, b^{[1]}, \cdots, W^{[L]}, b^{[L]}) = J(\theta)$$

Take $$dW^{[1]}, db^{[1]}, \cdots, dW^{[L]}, db^{[L]}$$ and reshape into a big vector $$d\theta$$

$$\displaystyle{\forall i, d\theta_{approx}^{[i]} = \frac{J(\theta_1, \theta_2, \cdots, \theta_i + \epsilon, \cdots) - J(\theta_1, \theta_2, \cdots, \theta_i - \epsilon, \cdots)}{2\epsilon} \approx d\theta^{[i]} = \frac{\partial J}{\partial \theta_i}}$$

检查  $$d\theta_{approx}$$ 和  $$d\theta$$ 是不是大致相等：计算欧几里得距离

$$\displaystyle{\frac{\left\|\left\|d\theta_{approx} - d\theta\right\|\right\|_{2}}{\left\|\left\|d\theta_{approx}\right\|\right\|_{2} + \left\|\left\|d\theta\right\|\right\|_{2}}}$$

除以分母是为了避免数值过大过小，变成一个比值

一般取 $$\epsilon = 10^{-7}$$

#### 小窍门

+ 只在调试的时候，因为太慢了

+ 检查不同的 i 值确认问题出在哪

+ 记得使用正则化

+ 梯度检验不能与随机失活一起使用

+ 在随机初始化的时候运行梯度检验，在几次迭代后再次梯度检验

## 优化算法

### 小批量梯度下降

将 $$X$$ 和 $$Y$$ 做拆分处理，例如每 $$1000$$ 个样本作为一个 Mini-batch

第 $$T$$ 个小批量样本，被记作 $$X^{\{T\}}$$

$$X^{(i)}$$ 表示训练集中的第 $$i$$ 个样本

$$Z^{[l]}$$ 表示第 $$l$$ 层的 $$z(\cdot)$$ 值

小批量梯度下降的代价函数可能是有噪声的，不一定每次都会下降，但是整体趋势是向下的，噪声可能与使用的那一个 batch 的 X{t} 和 Y{t} 有关，也许是一个错误的标签

定一个 mini-batch size

![](https://img.jxtxzzw.com/2020/10/19/qpl2ah.png)

+ 如果 mini-batch size = m
	+ 只有一个 (X{1}, Y{1}) = (X, Y)
	+ 就是批量梯度下降
	+ 每一步相对大些，会较快到达最小值
	+ 但是每一次迭代的时间很久
	

![](https://img.jxtxzzw.com/2020/10/19/qpo3yd.png)

+ 如果 mini-batch size = 1
	+ 每一个样本都是一个 mini-batch
	+ 就是随机梯度下降
	+ 大多数时候可以到达全局最优，但是也会因为错误的数据走向错误的方向，噪声比较大
	+ 无法到达最低点，会在最低点附近摆动
	+ 无法使用矢量化加速

![](https://img.jxtxzzw.com/2020/10/19/qpqf05.png)

+ mini-batch size 在 1 到 m 之间
	1. 如果样本比较小，例如 2000，没有必要使用 mini-batch，直接批量梯度下降就可以了
	2. 样本比较大的时候，一般取 64 到 512 之间的 2 的整数次幂
	3. 确保 mini-batch 中的 X{t} 和 Y{t} 可以被放入 CPU/GPU 内存
	
### 指数加权平均

$$V_0 = 0, V_t = 0.9 V_{t - 1} + 0.1 \theta_t$$

更一般的，$$V_t = \beta V_{t - 1} + (1 - \beta) \theta_t$$

其中 $$\beta$$ 的意思是前 $$\frac{1}{1 - \beta}$$ 个数据的平均值

例如本例中，$$\beta = 0.9$$，就是前 10 天气温的平均值

+ 当 $$\beta$$ 很大的时候，对更多的天数做了平滑，曲线会更平滑，更缓慢地适应，而且会往右移动，造成一定延迟

+ 当 $$\beta$$ 很小的时候，容易受到噪声的影响

### 偏差修正

帮助更精确地计算平均值

![](https://img.jxtxzzw.com/2020/10/19/qpt4bi.png))

由于 $$v_0 = 0$$，所以变成了紫色的线，无法准确估计初期的值

为了变成绿色的线：用 $$\frac{v_t}{1 - \beta^t}$$

t 值足够大时，偏差修正值对运算将基本没有影响

如果在初始阶段就开始考虑偏差，指数加权移动均值仍处于预热阶段，偏差修正可以帮助尽早做出更好的估计

### 动量梯度下降算法

计算梯度的指数加权平均，然后用这个梯度来更新权重

![](https://img.jxtxzzw.com/2020/10/19/qq4loj.png)

上下的振动使得无法使用过大的学习率

在 iteration $$t$$，需要计算 $$dw$$ 和 $$db$$，然后

$$v_{dw} = \beta v_{dw} + (1 - \beta) dw$$

$$v_{db} = \beta v_{db} + (1 - \beta) db$$

$$w = w - \alpha v_{dw}$$

$$b = b - \alpha v_{db}$$

使得在水平方向上速度更快，在垂直方向上学习更慢，减弱了前往最低点路径中的振幅

在很多文献中，$$(1 - \beta)$$ 的系数被忽略了，而变成了 $$\beta v_{dw} + dw$$

这种写法也可以，但是当 $$\beta$$ 改变时，也会影响 $$\alpha$$ 的最佳值，需要重新对 $$\alpha$$ 缩放后的值寻找超参数，因此不够直观

### 均方根传递 RMSprop

$$S_{dw} = \beta_2 S_{dw} + (1 - \beta_2) dw^2$$

$$w = w - \alpha \frac{dw}{\sqrt{S_{dw}}}$$

$$S_{db} = \beta_2 S_{db} + (1 - \beta_2) db^2$$

$$b = b - \alpha \frac{db}{\sqrt{S_{db}}}$$

+ 这里的平方是 element-wise 的

+ 这里是 $$\beta_2$$，是为了和动量中的 $$\beta$$ 区分

+ 为了使分母不为 $$0$$，一般还会加一个 $$\epsilon$$，取值在 $$10^{-8}$$ 数量级就可以了

### Adam 优化算法

1. 首先进行初始化 $$v_{dw}, s_{dw}, v_{db}, s_{db} = 0$$

2. 在每一个 iteration t：

	1. 使用 mini-batch 计算 $$dw, db$$

	2. 计算动量

	$$v_{dw} = \beta_1 v_{dw} + (1 - \beta_1) dw$$

	$$v_{db} = \beta_1 v_{db} + (1 - \beta_1) db$$

	3. 均方根传递

	$$s_{dw} = \beta_2 s_{dw} + (1 - \beta_2) dw^2$$

	$$s_{dw} = \beta_2 s_{dw} + (1 - \beta_2) dw^2$$

	4. 偏差修正
	
	$$\displaystyle{v_{dw}^{correct} = \frac{v_{dw}}{1 - \beta_1^t}}$$
	
	$$\displaystyle{v_{db}^{correct} = \frac{v_{db}}{1 - \beta_1^t}}$$

	$$\displaystyle{s_{dw}^{correct} = \frac{s_{dw}}{1 - \beta_2^t}}$$

	$$\displaystyle{s_{db}^{correct} = \frac{s_{db}}{1 - \beta_2^t}}$$

	5. 执行更新
	
	$$\displaystyle{w = w - \alpha \frac{v_{dw}^{correct}}{\sqrt{s_{dw}^{correct}} + \epsilon}}$$
	
	$$\displaystyle{b = b - \alpha \frac{v_{db}^{correct}}{\sqrt{s_{db}^{correct}} + \epsilon}}$$
	

超参数的选择

$$\alpha$$ 需要调试

推荐取 $$\beta_1$$ = $$0.9$$，$$\beta_2 = 0.9999$$，$$\epsilon = 10^{-8}$$

### 学习率下降

较低的学习率可使用更小的步长

$$\displaystyle{\alpha = \frac{1}{1 + decayRate \times epochNumber} \alpha_0}$$

| Epoch | Learning Rate |
| ------------ | ------------ |
| Epoch 1 | 0.1 |
| Epoch 2 | 0.067 |
| Epoch 3 | 0.05 |
| Epoch 4 | 0.04 |

需要尝试的超参数包括 $$\alpha_0$$ 和 $$decayRate$$

也可以使用

+ $$\alpha = 0.95^{epochNumber} \alpha_0$$

+ $$\alpha = \frac{k}{\sqrt{epochNumber}} \alpha_0$$

+ 离散阶梯下降

![](https://img.jxtxzzw.com/2020/10/19/qqa7rm.png)

+ 手动调整（每小时，或者每天，手动调整一次）

### 局部优化

只要训练的是一个有很多参数的巨大的神经网络，不太会陷入局部最优解，代价函数是一个高维的函数，所以更多的情况是遇到鞍点

鞍点

![](https://img.jxtxzzw.com/2020/10/19/qqd4fb.png)

真正会降低学习速率的是停滞区，它会使得训练变得非常慢，可以使用动量、Adam 等方法优化

## 超参数调整

#### 超参数

重要程度依次是：

$$\alpha$$, $$\beta$$, `#hidden unit`, mini-batch size, `#layers`, learning rate decay

![](https://img.jxtxzzw.com/2020/10/19/qt4e7m.png)

选择随机的点，而不要使用一个网格，因为事先无法知道哪个参数是重要的

对于 $$n^{[l]}$$（第 $$l$$ 层的隐藏单元的个数）、对于 `#layers`（隐藏层个数），使用均匀随机采样是合理的

对于 $$\alpha$$（学习率），则应该使用对数尺度上的均匀采样（例如 0.1、0.01、0.001）

```python
r = -4 * np.random.rand() # (-4, 0]
alpha = 10 ** r # (10^{-4}, 1]
```

对于 $$\beta$$ （用于计算指数加权平均），在 $$0.9 \to 0.999$$ 采样，可以对 $$1 - \beta$$ 在 $$0.1 \to 0.001$$ 进行对数随机采样

```python
r = -2 * np.random.rand() - 1 # (-3, -1]
beta = 1 - 10**r
```

熊猫策略 Panda：在没有足够的计算资源的时候，Babysitting 一个模型，每过一天调整一下学习率，或者用一下动量优化，发现不够好的时候回到前面的参数，发现好的时候继续优化……

鱼子酱策略 Caviar：计算机资源足够的时候，同时训练很多模型

![](https://img.jxtxzzw.com/2020/10/19/qt7c9y.png)

#### 归一化

批量归一化可以使搜索超参数更简单、增加鲁棒性、并可以训练更加深的网络

$$\mu = \frac{1}{m} \sum_{i}{x^{(i)}}$$

$$x = x - \mu$$

$$\sigma^2 = \frac{1}{m} \sum_{i}{x^{(i)^2}}, element-wise$$

$$x = \frac{x}{\sigma ^ 2}$$

在激活函数前作归一化（对于 $$Z$$）还是在激活函数后做归一化（对于 $$A$$），是有争议的，但是普遍上对 $$Z$$ 做归一化要多

$$\mu = \frac{1}{m} \sum_{i}{z^{(i)}}$$

$$\sigma^2 = \frac{1}{m} \sum_{i}{z_i - \mu}^2$$

$$Z_{norm} = \frac{z^{(i)} - \mu}{\sqrt{\sigma ^2 + \epsilon}}$$

加上 $$\epsilon$$ 可以避免分母为 $$0$$

通过 $$\gamma$$ 和 $$\beta$$ 可以使 $$Z^{(i)}$$ 处于自己希望的范围内，例如合适的方差等，因为 Sigmoid 函数激活后，最终的值可能会局限在中间的范围，这是不希望的，所以可以通过这个来控制一下

他们需要被调优，可以通过 Adam 优化、动量梯度下降、RMSprop 等方法获得，不只能用梯度下降，他们不是通过随机采样获得的

他们设置了给定层的线性变量 $$z^{[l]}$$ 的平均数和方差

$$\tilde{z}^{(i)} = \gamma z^{(i)}_{norm} + \beta$$

![](https://img.jxtxzzw.com/2020/10/19/qtbbb5.png)

x --(w1, b1)--> z1 --(beta1, gamma1, Batch Norm)--> z tilde 1 --> a1 = g1(z1) --(w2, b2)--> z2 --(beta2, gamma2, Batch Norm)--> z tilde 2 --> a2 --(....)--> ....

```python
bb-batch-normalization()
```

通常运用到训练集上的是小批量（mini-batch）的 batch-normalization

通过归一化使所有的输入特征 X 拥有相同的变化范围将加速学习，而 BN 也同样如此，只不过它应用于隐藏层的值

BN 使得产生的权重更具有鲁棒性

均值和方差都有噪声，由此计算得到的 tilde z 也是有噪声的

dropout 有许多噪声因为要么乘以 0 要么乘以 1

BN 通过在隐藏层增加噪声使得后面的计算不过分依赖任何一个特定的单元，有轻微的正则化效果

通过更大尺寸的 mini batch，会减少噪声，由此也会削弱正则化效果

不要把 BN 看作正则化（regularization），它是归一化（normalization），正则化只是附带的副作用

在测试时（一次只有一个样本），使用指数加权平均来完成 BN

#### 多分类

Softmax Regression

$$C$$ 表示 `#classes`，即分类的数目，包括“不属于任何分类”（$$0$$）

有 $$C$$ 个输出，分别代表每一个分类的概率，其和为 $$1$$

$$z^{[l]} = w^{[l]} a^{[l - 1]} + b^{[l]}$$

$$t = e^{z^{[l]}}$$

$$a^{[l]} = \displaystyle{\frac{e^{z^{[l]}}}{\sum_{i = i}^{C}{t_i}}}$$

当 C = 2 时，Softmax Regression 退化成 Logistics Regression

#### 深度学习框架

```python
w = tf.Variable(0, dtype = tf.float32)
cost = tf.add(tf.add(w**2, tf.multiply(-10., w)), 25) # w^2 - 10w + 25
train = tf.train.GradientDescentOptimizer(0.01).minimize(cost)

init= tf.global_variable_initializer()
session = tf.Session()
session.run(init)
print(session.run(w))
# 0.0

session.run(train)
print(session.run(w))
# 0.1

for i in range(1000):
	session.run(train)
print(session.run(w))
# 4.99999
```

只需要定义前向传播，TF 会根据 `tf.multiply()`、`tf.add()` 等操作自动求导。TF 重载了加减乘除，可以写成 `cost = w**2 - 10 * w + 25`。

```python
coefficient = np.array([1.], [-10.], [25.])
x = tf.placeholder(tf.float32, [3, 1])
cost = x[0][0]*w**2 + x[1][0]*w + x[2][0]
# ...
session.run(train, feed_dict = {x: coefficient})
# 4.9999
```