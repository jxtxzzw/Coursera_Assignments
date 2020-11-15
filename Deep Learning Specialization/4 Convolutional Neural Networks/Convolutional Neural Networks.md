# 卷积神经网络

> Coursera 专项课程 Deep Learning Specialization （由 DeepLearning.AI 提供）的第 4 课 Convolutional Neural Networks 的学习笔记

## 卷积神经网络基础

### 计算机视觉

#### 边缘检测

星号 * 是卷积的标志

卷积的计算

![](https://img.jxtxzzw.com/2020/10/19/qwzykq.png)

用小的图片会使得边缘看起来很粗，用大的图片就会很好

正边缘、负边缘

其他的过滤器

Sobel： 更加稳定

![](https://img.jxtxzzw.com/2020/10/19/qxc88c.png)

Scharr：

![](https://img.jxtxzzw.com/2020/10/19/qxesym.png)

#### 填充

使用过滤器的缺陷：

1. 对一个 $$n \times n$$ 的矩阵应用一个 $$f \times f$$ 的过滤器，得到的图片是 $$(n - f + 1) \times (n - f + 1)$$ 的维度

2. 角落和边际上的像素只会被使用一次，所以会丢失靠近边界的信息

改进：使用填充（padding）

+ Valid 卷积: 没有填充；

+ Same 卷积: 填充若干个像素使得输出图像的大小与原始输入图像的大小相同，即对一个 $$n \times n$$ 的矩阵应用一个 $$f \times f$$ 的过滤器，为了得到的图片是 $$n \times n$$ 的维度，需要填充 $$p = \frac{f - 1}{2}$$ 个像素，因为 $$n + 2 \times p - f + 1 = n$$。在实践中，$$f$$ 始终是奇数，这可以避免上述 $$p$$ 出现不对称的填充，同时 $$f \times f$$ 将有一个中心像素可以描述过滤器的位置；

+ Strided 卷积：每次移动的步长为 $$S$$，对一个 $$n \times n$$ 的矩阵应用一个 $$f \times f$$ 的过滤器，得到的图片是 $$(\frac{n + 2 \times p- f}{S} + 1) \times (\frac{n + 2 \times p- f}{S} + 1)$$ 的维度，其中 $$p$$ 是填充像素个数、$$S$$ 是步长，商 $$\frac{n + 2 \times p- f}{2}$$ 向下取整。

#### Pooling Layers

Max Pooling 的每个输出值将会是其对应颜色区域的最大值

![](https://img.jxtxzzw.com/2020/10/19/qxgvh7.png)

![](https://img.jxtxzzw.com/2020/10/19/qxj5rr.png)

与之对应的，Average Pooling 就是取平均

![](https://img.jxtxzzw.com/2020/10/19/qxl5dc.png)

## 案例学习





## 物体检测



## 应用：人脸识别和风格迁移

