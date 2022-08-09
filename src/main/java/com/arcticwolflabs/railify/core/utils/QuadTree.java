package com.arcticwolflabs.railify.core.utils;

public class QuadTree {

    public class QuadNode {
        int axis;
        double[] x;
        int id;
        boolean checked;
        boolean orientation;
        QuadNode Parent;
        QuadNode Left;
        QuadNode Right;

        public QuadNode(double[] x0, int id0, int axis0) {
            x = new double[2];
            id = id0;
            axis = axis0;
            for (int k = 0; k < 2; k++)
                x[k] = x0[k];
            Left = Right = Parent = null;
            checked = false;
            id = 0;
        }

        public QuadNode FindParent(double[] x0) {
            QuadNode parent = null;
            QuadNode next = this;
            int split;
            while (next != null) {
                split = next.axis;
                parent = next;
                if (x0[split] > next.x[split]) next = next.Right;
                else next = next.Left;
            }
            return parent;
        }

        public int getID() {
            return qid;
        }

        public QuadNode Insert(double[] p, int id) {
            x = new double[2];
            QuadNode parent = FindParent(p);
            if (equal(p, parent.x, 2) == true) return null;
            QuadNode newNode = new QuadNode(p, id, parent.axis + 1 < 2 ? parent.axis + 1 : 0);
            newNode.Parent = parent;
            if (p[parent.axis] > parent.x[parent.axis]) {
                parent.Right = newNode;
                newNode.orientation = true; //
            } else {
                parent.Left = newNode;
                newNode.orientation = false; //
            }
            return newNode;
        }

        boolean equal(double[] x1, double[] x2, int dim) {
            for (int k = 0; k < dim; k++) {
                if (x1[k] != x2[k]) return false;
            }
            return true;
        }

        double distance2(double[] x1, double[] x2, int dim) {
            double S = 0;
            for (int k = 0; k < dim; k++)
                S += (x1[k] - x2[k]) * (x1[k] - x2[k]);
            return S;
        }
    }

    QuadNode Root;
    int TimeStart, TimeFinish;
    int CounterFreq;
    double d_min;
    QuadNode nearest_neighbour;
    int qid;
    int nList;
    QuadNode CheckedNodes[];
    int checked_nodes;
    QuadNode List[];
    double x_min[], x_max[];
    boolean max_boundary[], min_boundary[];
    int n_boundary;

    public QuadTree(int i) {
        Root = null;
        qid = 0;
        nList = 0;
        List = new QuadNode[i];
        CheckedNodes = new QuadNode[i];
        max_boundary = new boolean[2];
        min_boundary = new boolean[2];
        x_min = new double[2];
        x_max = new double[2];
    }

    public boolean add(double[] x, int id) {
        if (nList >= 2000000 - 1) return false; // can't add more points
        if (Root == null) {
            Root = new QuadNode(x, id, 0);
            Root.id = qid++;
            List[nList++] = Root;
        } else {
            QuadNode pNode;
            if ((pNode = Root.Insert(x, id)) != null) {
                pNode.id = qid++;
                List[nList++] = pNode;
            }
        }
        return true;
    }

    public QuadNode find_nearest(double[] x) {
        if (Root == null) return null;
        checked_nodes = 0;
        QuadNode parent = Root.FindParent(x);
        nearest_neighbour = parent;
        d_min = Root.distance2(x, parent.x, 2);
        ;
        if (parent.equal(x, parent.x, 2) == true) return nearest_neighbour;
        search_parent(parent, x);
        uncheck();
        return nearest_neighbour;
    }

    public void check_subtree(QuadNode node, double[] x) {
        if ((node == null) || node.checked) return;
        CheckedNodes[checked_nodes++] = node;
        node.checked = true;
        set_bounding_cube(node, x);
        int dim = node.axis;
        double d = node.x[dim] - x[dim];
        if (d * d > d_min) {
            if (node.x[dim] > x[dim]) check_subtree(node.Left, x);
            else check_subtree(node.Right, x);
        } else {
            check_subtree(node.Left, x);
            check_subtree(node.Right, x);
        }
    }

    public void set_bounding_cube(QuadNode node, double[] x) {
        if (node == null) return;
        int d = 0;
        double dx;
        for (int k = 0; k < 2; k++) {
            dx = node.x[k] - x[k];
            if (dx > 0) {
                dx *= dx;
                if (!max_boundary[k]) {
                    if (dx > x_max[k]) x_max[k] = dx;
                    if (x_max[k] > d_min) {
                        max_boundary[k] = true;
                        n_boundary++;
                    }
                }
            } else {
                dx *= dx;
                if (!min_boundary[k]) {
                    if (dx > x_min[k]) x_min[k] = dx;
                    if (x_min[k] > d_min) {
                        min_boundary[k] = true;
                        n_boundary++;
                    }
                }
            }
            d += dx;
            if (d > d_min) return;
        }
        if (d < d_min) {
            d_min = d;
            nearest_neighbour = node;
        }
    }

    public QuadNode search_parent(QuadNode parent, double[] x) {
        for (int k = 0; k < 2; k++) {
            x_min[k] = x_max[k] = 0;
            max_boundary[k] = min_boundary[k] = false; //
        }
        n_boundary = 0;
        QuadNode search_root = parent;
        while (parent != null && (n_boundary != 2 * 2)) {
            check_subtree(parent, x);
            search_root = parent;
            parent = parent.Parent;
        }
        return search_root;
    }

    private void uncheck() {
        for (int n = 0; n < checked_nodes; n++)
            CheckedNodes[n].checked = false;
    }

    private void inorder() {
        inorder(Root);
    }

    private void inorder(QuadNode root) {
        if (root != null) {
            inorder(root.Left);
            System.out.print("(" + root.x[0] + ", " + root.x[1] + ")  ");
            inorder(root.Right);
        }
    }

    private void preorder() {
        preorder(Root);
    }

    private void preorder(QuadNode root) {
        if (root != null) {
            System.out.print("(" + root.x[0] + ", " + root.x[1] + ")  ");
            inorder(root.Left);
            inorder(root.Right);
        }
    }

    private void postorder() {
        postorder(Root);
    }

    private void postorder(QuadNode root) {
        if (root != null) {
            inorder(root.Left);
            inorder(root.Right);
            System.out.print("(" + root.x[0] + ", " + root.x[1] + ")  ");
        }
    }

    public void Test() {
        int numpoints = 5;
        QuadTree kdt = new QuadTree(numpoints);
        double x[] = new double[2];
        x[0] = 0.0;
        x[1] = 0.0;
        kdt.add(x, 0);
        x[0] = 3.3;
        x[1] = 1.5;
        kdt.add(x, 1);
        x[0] = 4.7;
        x[1] = 11.1;
        kdt.add(x, 2);
        x[0] = 5.0;
        x[1] = 12.3;
        kdt.add(x, 3);
        x[0] = 5.1;
        x[1] = 1.2;
        kdt.add(x, 4);
        System.out.println("Inorder of 2D Kd tree: ");
        kdt.inorder();
        System.out.println("\nPreorder of 2D Kd tree: ");
        kdt.preorder();
        System.out.println("\nPostorder of 2D Kd tree: ");
        kdt.postorder();
    }
}


