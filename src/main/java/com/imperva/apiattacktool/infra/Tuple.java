package com.imperva.apiattacktool.infra;

public class Tuple<L, R> {

    public L left;
    public R right;

    public Tuple(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + String.valueOf(left) + ", " + String.valueOf(right) + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Tuple) {
            Tuple<?, ?> other = (Tuple<?, ?>) obj;
            return left.equals(other.left) && right.equals(other.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (left != null) {
            result += (31 * result + left.hashCode());
        }
        if (right != null) {
            result += (31 * result + right.hashCode());
        }
        return result;
    }

}