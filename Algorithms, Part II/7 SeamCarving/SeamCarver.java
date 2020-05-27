import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {

    private Picture picture;
    private final double[][] energy;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException();
        }
        // always keep a clone, not the reference
        this.picture = new Picture(picture);
        energy = new double[picture.height()][picture.width()]; // 根据图片长宽，初始化数组长度
    }

    // current picture
    public Picture picture() {
        // always return a new object, not the reference
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    /**
     * 根据给定的颜色，计算 dual-gradient energy 公式
     *
     * @param c1 颜色1
     * @param c2 颜色2
     * @return dual-gradient energy公式的值
     */
    private int yielding(Color c1, Color c2) {
        // 计算两个颜色的R、G、B差值
        int r = (c1.getRed() - c2.getRed());
        int g = (c1.getGreen() - c2.getGreen());
        int b = (c1.getBlue() - c2.getBlue());
        // 代入公式计算结果
        return r * r + g * g + b * b;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException();
        }

        // 如果是边界情况，按照定义，能量是 1000
        final int DEFAULT_ENERGY = 1000;
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) {
            return DEFAULT_ENERGY;
        }

        // 计算纵向的梯度
        int yGradient = yielding(picture.get(x, y - 1), picture.get(x, y + 1));

        // 计算横向的梯度
        int xGradient = yielding(picture.get(x - 1, y), picture.get(x + 1, y));

        return Math.sqrt(xGradient + yGradient);
    }

    // 遍历图，计算所有点的能量值
    private void allEnergy() {
        for (int i = 0; i < picture.height(); i++) {
            for (int j = 0; j < picture.width(); j++) {
                // energy[][] 数组的横纵坐标是 [i][j] 而 energy() 函数的参数是 (j, i)
                energy[i][j] = energy(j, i);
            }
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        allEnergy();
        double[][] dynamicMinimumEnergy = new double[picture.height()][picture.width()];
        for (int col = 0; col < picture.width(); ++col) {
            for (int row = 0; row < picture.height(); ++row) {

                if (col == 0) {
                    dynamicMinimumEnergy[row][col] = energy[row][col];
                } else {
                    double possibleMinimumEnergy = dynamicMinimumEnergy[row][col - 1];
                    if (row - 1 >= 0) {
                        if (possibleMinimumEnergy > dynamicMinimumEnergy[row - 1][col - 1]) {
                            possibleMinimumEnergy = dynamicMinimumEnergy[row - 1][col - 1];
                        }
                    }
                    if (row + 1 < picture.height()) {
                        if (possibleMinimumEnergy > dynamicMinimumEnergy[row + 1][col - 1]) {
                            possibleMinimumEnergy = dynamicMinimumEnergy[row + 1][col - 1];
                        }
                    }
                    dynamicMinimumEnergy[row][col] = possibleMinimumEnergy + energy[row][col];
                }
            }
        }

        int[] horizontalSeam = new int[picture.width()];
        for (int col = picture.width() - 1; col >= 0; --col) {
            if (col == picture.width() - 1) {
                int minimumIndex = 0;
                for (int row = 0; row < picture.height(); ++row) {
                    if (dynamicMinimumEnergy[row][col] < dynamicMinimumEnergy[minimumIndex][col]) {
                        minimumIndex = row;
                    }
                }
                horizontalSeam[col] = minimumIndex;
            } else {
                horizontalSeam[col] = horizontalSeam[col + 1];
                double possibleMinimumEnergy = dynamicMinimumEnergy[horizontalSeam[col + 1]][col];
                if (horizontalSeam[col + 1] - 1 >= 0) {
                    if (dynamicMinimumEnergy[horizontalSeam[col + 1] - 1][col] < possibleMinimumEnergy) {
                        possibleMinimumEnergy = dynamicMinimumEnergy[horizontalSeam[col + 1] - 1][col];
                        horizontalSeam[col] = horizontalSeam[col + 1] - 1;
                    }
                }
                if (horizontalSeam[col + 1] + 1 < picture.height()) {
                    if (dynamicMinimumEnergy[horizontalSeam[col + 1] + 1][col] < possibleMinimumEnergy) {
                        horizontalSeam[col] = horizontalSeam[col + 1] + 1;
                    }
                }
            }
        }
        return horizontalSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // 计算所有像素的能量密度
        allEnergy();

        // 动态最小能量[i][j]表示到[i][j]点的最小花费
        double[][] dynamicMinimumEnergy = new double[picture.height()][picture.width()];

        // 遍历二维数组
        for (int row = 0; row < picture.height(); ++row) {
            for (int col = 0; col < picture.width(); ++col) {

                if (row == 0) {
                    // 如果这是最上面的一行，那么最小能量花费显然就是它自己的能量
                    dynamicMinimumEnergy[row][col] = energy[row][col];
                } else {
                    /*
                     * 如果不在最上面的一行，那么能量有三种路径可能到达 左上（如果存在）、正上、右上（如果存在）
                     * 所以需要在三个中找到最小的，从那个像素到达这个像素的路径保证是最短的
                     */
                    // 首先令可能的路径为正上的那个，因为这个必定存在
                    double possibleMinimumEnergy = dynamicMinimumEnergy[row - 1][col];
                    if (col - 1 >= 0) {
                        // 如果左上方不越界，就说明可能是，于是判断是不是比当前的更小，如果是比当前更小，就更新
                        if (possibleMinimumEnergy > dynamicMinimumEnergy[row - 1][col - 1]) {
                            possibleMinimumEnergy = dynamicMinimumEnergy[row - 1][col - 1];
                        }
                    }
                    if (col + 1 < picture.width()) {
                        // 右上方同理，如果存在，就判断
                        if (possibleMinimumEnergy > dynamicMinimumEnergy[row - 1][col + 1]) {
                            possibleMinimumEnergy = dynamicMinimumEnergy[row - 1][col + 1];
                        }
                    }
                    // 到这里求出了最短的那个路径，加上当前点的能量，就是累计最小能量
                    dynamicMinimumEnergy[row][col] = possibleMinimumEnergy + energy[row][col];
                }
            }
        }
        // 二维数组遍历结束，得到所有的能量路径总和
        int[] verticalSeam = new int[picture.height()]; // 存放最短的一条路径

        // 显然最短的路径如果存在，那么其一定满足最下方的那个像素有最小的能量密度总和
        for (int row = picture.height() - 1; row >= 0; --row) {
            // 从最后一行开始往前遍历，依次回溯出所有的路径
            if (row == picture.height() - 1) {
                // 如果在最后一行就遍历找到这一行中最小的总和
                int minimumIndex = 0;
                for (int col = 0; col < picture.width(); ++col) {
                    if (dynamicMinimumEnergy[row][col] < dynamicMinimumEnergy[row][minimumIndex]) {
                        minimumIndex = col;
                    }
                }
                // 这个点就是路径的最后一个点
                verticalSeam[row] = minimumIndex;
            } else {
                // 如果不在最后一行，注意这里不能遍历整个第row行去找最小的，而只能是与刚才那个路径点相邻的那3个点中找最小的
                // 显然其正上方的点一定存在，假设为最小的路径
                verticalSeam[row] = verticalSeam[row + 1];
                double possibleMinimumEnergy = dynamicMinimumEnergy[row][verticalSeam[row + 1]];
                // 如果其左上方的坐标合法，就判断是不是最上方的点下来的
                if (verticalSeam[row + 1] - 1 >= 0) {
                    if (dynamicMinimumEnergy[row][verticalSeam[row + 1] - 1] < possibleMinimumEnergy) {
                        possibleMinimumEnergy = dynamicMinimumEnergy[row][verticalSeam[row + 1] - 1];
                        verticalSeam[row] = verticalSeam[row + 1] - 1; // 如果是，就更新为左上方为前一个路径
                    }
                }
                // 右上方同理
                if (verticalSeam[row + 1] + 1 < picture.width()) {
                    if (dynamicMinimumEnergy[row][verticalSeam[row + 1] + 1] < possibleMinimumEnergy) {
                        verticalSeam[row] = verticalSeam[row + 1] + 1;
                    }
                }
            }
        }
        // 循环结束，得到整个路径数组
        return verticalSeam;
    }

    private void validCheck(int[] seam, int length) {
        if (seam == null || seam.length != length) {
            throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length; i++) {
            if (Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validCheck(seam, picture.width());
        Picture newPicture = new Picture(picture.width(), picture.height() - 1);
        for (int col = 0; col < picture.width(); ++col) {
            for (int row = 0; row < seam[col]; ++row) {
                newPicture.set(col, row, picture.get(col, row));
            }
            for (int row = seam[col]; row < picture.height() - 1; ++row) {
                newPicture.set(col, row, picture.get(col, row + 1));
            }
        }
        picture = newPicture;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validCheck(seam, picture.height());
        // 首先新建一个图片，宽度减1、高度不变
        Picture newPicture = new Picture(picture.width() - 1, picture.height());
        // 从0到高度，依次复制第row行
        for (int row = 0; row < picture.height(); ++row) {
            // 注意到前面seam[row]列照常复制
            for (int col = 0; col < seam[row]; ++col) {
                newPicture.set(col, row, picture.get(col, row));
            }
            // 后面的每一个，因为删掉了一条Seam，所以就有一个单位的位移
            for (int col = seam[row]; col < picture.width() - 1; ++col) {
                newPicture.set(col, row, picture.get(col + 1, row));
            }
        }
        // 把新图片保存下来
        picture = newPicture;
    }

}