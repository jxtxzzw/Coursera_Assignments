import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

/**
 * @author jxtxzzw
 */
public class KdTree {
    private Node root;
    private int size;

    private static class Node {

        private final Point2D p;
        private final int level;
        private Node left;
        private Node right;
        private final RectHV rect;
        // 记录重叠的点
        private final ArrayList<Point2D> same = new ArrayList<>();


        // 对根结点
        public Node(Point2D p) {
            // 根结点层数是 0，范围是单位正方形
            this(p, 0, 0, 1, 0, 1);
        }

        public Node(Point2D p, int level, double xmin, double xmax, double ymin, double ymax) {
            this.p = p;
            this.level = level;
            rect = new RectHV(xmin, ymin, xmax, ymax);
        }

        public void addSame(Point2D point) {
            same.add(point);
        }

        public boolean hasSamePoint() {
            return !same.isEmpty();
        }
    }

    private Point2D currentNearest;
    private double min;

    public KdTree() {

    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private int compare(Point2D p, Node n) {
        if (n.level % 2 == 0) {
            // 如果是偶数层，按 x 比较
            if (Double.compare(p.x(), n.p.x()) == 0) {
                return Double.compare(p.y(), n.p.y());
            } else {
                return Double.compare(p.x(), n.p.x());
            }
        } else {
            // 按 y 比较
            if (Double.compare(p.y(), n.p.y()) == 0) {
                return Double.compare(p.x(), n.p.x());
            } else {
                return Double.compare(p.y(), n.p.y());
            }
        }
    }

    private Node generateNode(Point2D p, Node parent) {
        int cmp = compare(p, parent);
        if (cmp < 0) {
            if (parent.level % 2 == 0) {
                // 偶数层，比较结果是小于，说明是加在左边
                // 那么它的 xmin, ymin, ymax 都和父结点一样，xmax 设置为父结点的 p.x()
                return new Node(p, parent.level + 1, parent.rect.xmin(), parent.p.x(), parent.rect.ymin(), parent.rect.ymax());
            } else {
                // 奇数层，加在下边，那么只需要修改 ymax
                return new Node(p, parent.level + 1, parent.rect.xmin(), parent.rect.xmax(), parent.rect.ymin(), parent.p.y());
            }
        } else {
            if (parent.level % 2 == 0) {
                // 偶数层，加在右边，那么只需要修改 xmin
                return new Node(p, parent.level + 1, parent.p.x(), parent.rect.xmax(), parent.rect.ymin(), parent.rect.ymax());

            } else {
                // 奇数层，比较结果是大于，说明是加在上边，修改 ymin
                return new Node(p, parent.level + 1, parent.rect.xmin(), parent.rect.xmax(), parent.p.y(), parent.rect.ymax());

            }
        }
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        } else {
            if (root == null) {
                // 初始化根结点
                size++;
                root = new Node(p);
            } else {
                // 二叉树，用递归的写法去调用
                insert(p, root);
            }
        }
    }

    private void insert(Point2D p, Node node) {
        int cmp = compare(p, node);
        // 如果比较结果是小于，那么就是要往左边走，右边同理
        if (cmp < 0) {
            // 走到头了就新建，否则继续走
            if (node.left == null) {
                size++;
                node.left = generateNode(p, node);
            } else {
                insert(p, node.left);
            }
        } else if (cmp > 0) {
            if (node.right == null) {
                size++;
                node.right = generateNode(p, node);
            } else {
                insert(p, node.right);
            }
        }
        // 重叠的点，size 不加 1
    }


    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        } else {
            if (root == null) {
                return false;
            } else {
                // 递归的写法
                return contains(p, root);
            }
        }
    }

    private boolean contains(Point2D p, Node node) {
        if (node == null) {
            return false;
        } else if (p.equals(node.p)) {
            return true;
        } else {
            if (compare(p, node) < 0) {
                return contains(p, node.left);
            } else {
                return contains(p, node.right);
            }
        }
    }

    public void draw() {
        // 清空画布
        StdDraw.clear();
        // 递归调用
        draw(root);
    }

    private void draw(Node node) {
        if (node != null) {
            // 点用黑色
            StdDraw.setPenColor(StdDraw.BLACK);
            // 画点
            node.p.draw();
            // 根据是不是偶数设置红色还是蓝色
            if (node.level % 2 == 0) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
            } else {
                StdDraw.setPenColor(StdDraw.BLUE);
                StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
            }
            // 递归画
            draw(node.left);
            draw(node.right);
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            return null;
        }
        // 递归调用
        return new ArrayList<>(range(rect, root));
    }

    private ArrayList<Point2D> range(RectHV rect, Node node) {
        ArrayList<Point2D> list = new ArrayList<>();
        // A subtree is searched only if it might contain a point contained in the query rectangle.
        if (node != null && rect.intersects(node.rect)) {
            // 递归地检查左右孩子
            list.addAll(range(rect, node.left));
            list.addAll(range(rect, node.right));
            // 如果对当前点包含，则加入
            if (rect.contains(node.p)) {
                list.add(node.p);
                // 重叠点应该只被计算一次
            }
        }
        return list;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (isEmpty()) {
            return null;
        }
        currentNearest = null;
        min = Double.POSITIVE_INFINITY;
        findNearest(p, root);
        return currentNearest;
    }

    private void findNearest(Point2D p, Node node) {
        if (node == null) {
            return;
        }
        // The square of the Euclidean distance between the point {@code p} and the closest point on this rectangle; 0 if the point is contained in this rectangle
        if (node.rect.distanceSquaredTo(p) <= min) {
            // Do not call 'distanceTo()' in this program; instead use 'distanceSquaredTo()'. [Performance]
            double d = node.p.distanceSquaredTo(p);
            if (d < min) {
                min = d;
                currentNearest = node.p;
            }
            // 先左先右当然都可以得到正确的结果，但是
            // 这里必须调整递归的顺序，才能达到剪枝的效果
            if (node.left != null && node.left.rect.contains(p)) {
                // 如果左孩子包含 p，由于矩形是越来越小的，所以若点在某个 node 的矩形内被包含，则该 node 的 p 离这个所求 p 的距离就可能越小
                // min 越小，那么剪枝的效果就越明显，因为越来越多的就不需要再计算了
                // 于是，应该始终优先去递归那个 contains(p) 的方向（因为有且只有可能要么是 left 要么还是 right）包含 p
                findNearest(p, node.left);
                findNearest(p, node.right);
            } else if (node.right != null && node.right.rect.contains(p)) {
                // 如果右孩子包含就先去右边
                findNearest(p, node.right);
                findNearest(p, node.left);
            } else {
                // 也可能出现两个都不包含的情况，那么离谁近就先去谁那
                // 注意调用时 null 的问题要特别处理，可以设置为无穷大
                double toLeft = node.left != null ? node.left.rect.distanceSquaredTo(p) : Double.POSITIVE_INFINITY;
                double toRight = node.right != null ? node.right.rect.distanceSquaredTo(p) : Double.POSITIVE_INFINITY;
                if (toLeft < toRight) {
                    findNearest(p, node.left);
                    findNearest(p, node.right);
                } else {
                    findNearest(p, node.right);
                    findNearest(p, node.left);
                }
            }
        }

    }

    public static void main(String[] args) {
        KdTree kd;
        kd = new KdTree();
        kd.insert(new Point2D(0.7, 0.2));
        kd.insert(new Point2D(0.5, 0.4));
        kd.insert(new Point2D(0.2, 0.3));
        kd.insert(new Point2D(0.4, 0.7));
        kd.insert(new Point2D(0.9, 0.6));
        assert kd.nearest(new Point2D(0.73, 0.36)).equals(new Point2D(0.7, 0.2));
    }
}
