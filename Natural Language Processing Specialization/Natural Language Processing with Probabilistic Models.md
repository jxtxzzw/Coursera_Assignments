# 自然语言处理-概率模型

> Coursera 专项课程 Natural Language Processing Specialization（由 DeepLearning.AI 提供）的第 2 课 Natural Language Processing with Probabilistic Models 的学习笔记

## 自动纠错

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

## 最小编辑距离

`play` 到 `stay` 有 2 个字母发生变化，编辑为 2，类似地，我们可以计算出插入和删除的字母个数

编辑个数乘上编辑代价就是编辑距离，插入和删除的代价是 1，而替换的代价是 2

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

