package com.arcticwolflabs.railify.core.utils;

import java.util.ArrayList;

public class KDTree {


    public static class Point2D {
        double x, y;
        int id;

        public Point2D(double x, double y, int id) {
            this.x = x;
            this.y = y;
            this.id = id;
        }

        public double distanceSquaredTo(Point2D p) {
            return (this.x-p.x)*(this.x-p.x)+(this.y-p.y)*(this.y-p.y);
        }

        public double x() {
            return x;
        }

        public double y() {
            return y;
        }
    }


    public static class RectHV {
        private final double xmin, ymin;
        private final double xmax, ymax;

        public RectHV(double xmin, double ymin, double xmax, double ymax) {
            if (xmax < xmin || ymax < ymin) {
                throw new IllegalArgumentException("Invalid rectangle");
            }
            this.xmin = xmin;
            this.ymin = ymin;
            this.xmax = xmax;
            this.ymax = ymax;
        }

        public double xmin() {
            return xmin;
        }

        public double ymin() {
            return ymin;
        }

        public double ymax() {
            return ymax;
        }

        public double xmax() {
            return xmax;
        }

        public boolean contains(Point2D p) {
            return (p.x() >= xmin) && (p.x() <= xmax)
                    && (p.y() >= ymin) && (p.y() <= ymax);
        }

        public boolean intersects(RectHV that) {
            return this.xmax >= that.xmin && this.ymax >= that.ymin
                    && that.xmax >= this.xmin && that.ymax >= this.ymin;
        }

        public double distanceSquaredTo(Point2D p) {
            double dx = 0.0, dy = 0.0;
            if      (p.x() < xmin) dx = p.x() - xmin;
            else if (p.x() > xmax) dx = p.x() - xmax;
            if      (p.y() < ymin) dy = p.y() - ymin;
            else if (p.y() > ymax) dy = p.y() - ymax;
            return dx*dx + dy*dy;
        }
    }

    private Node root;
    private int size;
    public KDTree() {
    }

    public boolean isEmpty(){
        return size == 0;
    }
    public int size(){
        return size;
    }
    public void insert(Point2D p){// add the point to the set (if it is not already in the set)
        if(!contains(p)){
            root = insert(root, p, true, new RectHV(8.08, 68.9, 34.26, 95.74));
            size++;
        }
    }
    private Node insert(Node node, Point2D p, boolean isVertical, RectHV rect){
        if(node == null){
            node = new Node(p, rect);
            return node;
        }
        if(isVertical){
            if(p.x() < node.point.x()){
                RectHV r = new RectHV(rect.xmin(), rect.ymin(), node.point.x(), rect.ymax());
                node.lb = insert(node.lb, p, !isVertical, r);
            } else {
                RectHV r = new RectHV(node.point.x(), rect.ymin(), rect.xmax(), rect.ymax());
                node.rt = insert(node.rt, p, !isVertical, r);
            }
        } else {
            if(p.y() < node.point.y()){
                RectHV r = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.point.y());
                node.lb = insert(node.lb, p, !isVertical, r);
            } else {
                RectHV r = new RectHV(rect.xmin(), node.point.y(), rect.xmax(), rect.ymax());
                node.rt = insert(node.rt, p, !isVertical, r);
            }
        }
        return node;
    }
    public boolean contains(Point2D p){// does the set contain point p?
        Node node = root;
        boolean isVertical = true;
        while(node != null){
            if(p.equals(node.point))
                return true;
            if(isVertical){
                if(p.x() < node.point.x())
                    node = node.lb;
                else
                    node = node.rt;
            } else {
                if(p.y() < node.point.y())
                    node = node.lb;
                else
                    node = node.rt;
            }
            isVertical = !isVertical;
        }
        return false;
    }

    public Iterable<Point2D> range(RectHV rect){ // all points that are inside the rectangle
        ArrayList<Point2D> list = new ArrayList<>();
        range(rect, root, list);
        return list;
    }
    private void range(RectHV query,  Node node, ArrayList<Point2D> list){
        if(node != null){
            if(query.contains(node.point))
                list.add(node.point);
            if(node.lb != null && query.intersects(node.lb.rect))
                range(query, node.lb, list);
            if(node.rt != null && query.intersects(node.rt.rect))
                range(query, node.rt, list);
        }
    }

    public Point2D nearest(Point2D p){  // a nearest neighbor in the set to point p; null if the set is empty
        if(isEmpty())
            return null;
        return nearest(p, root, root.point);
    }
    private Point2D nearest(Point2D query, Node node, Point2D closest){
        if(query.distanceSquaredTo(node.point) < query.distanceSquaredTo(closest))
            closest = node.point;
        if(node.lb != null && (node.lb.rect.distanceSquaredTo(query) < closest.distanceSquaredTo(query)))
            closest = nearest(query, node.lb, closest);
        if(node.rt != null && (node.rt.rect.distanceSquaredTo(query) < closest.distanceSquaredTo(query)))
            closest = nearest(query, node.rt, closest);
        return closest;
    }
    private static class Node{
        public Point2D point;
        public RectHV rect;
        public Node lb;
        public Node rt;

        public Node(Point2D point, RectHV rect){
            this.point = point;
            this.rect = rect;
        }
    }
}