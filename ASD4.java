
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ASD4 {

    public static void main(String[] args) throws FileNotFoundException {

        Scanner scan = new Scanner(new File(args[0]));
        BinaryHeap queue = new BinaryHeap();

        while (scan.hasNextLine()) //odczyt wartości i zakolejkowanie
            queue.addNode(new HeapNode(new TreeNode(scan.next().charAt(0)), scan.nextInt()));

        if (queue.size == 1) //jeśli tylko jeden znak - utwórz z niego drzewo
            queue.addNode(queue.popOne().concatWith(null));

        while (queue.size != 1) //łączenie węzłów z wartościami
            queue.addNode(queue.popOne().concatWith(queue.popOne()));

        queue.root.data.printAll(""); //wypisanie kodowań

    }
}

class BinaryHeap {
    public HeapNode root;
    int size = 0;

    public void addNode(HeapNode node) { //dodawanie węzła kolejki na odpowiednim miejscu
        if (root == null)
            root = node;
        else {
            HeapNode parent = findNextPlaceParent();
            if (parent.left == null)
                parent.left = node;
            else
                parent.right = node;
            node.parent = parent;
            node.revalidateToUp();
        }
        size++;
    }

    public HeapNode findNextPlaceParent() {

        int nodesAbove = 1;
        int numberOfLevels = 0; //obliczanie ile jest wypełnionych poziomów
        int index = size;       //i którego elementu w następnym rzędzie szukamy
        while (index - nodesAbove >= 0) {
            if (index - nodesAbove < 0)
                break;
            index -= nodesAbove;
            nodesAbove *= 2;
            numberOfLevels++;
        }

        HeapNode tmp = root;

        int cursor = 1 << (numberOfLevels - 1); //schodzenie bit po bicie aż do szukanego rodzica
        while (cursor != 1) {
            if ((index & cursor) != 0)
                tmp = tmp.right;
            else
                tmp = tmp.left;
            cursor /= 2;
        }
        return tmp;
    }


    public HeapNode popOne() {
        HeapNode newRoot = root;
        HeapNode toSwap = getLast();
        toSwap.swapValuesWith(root);

        if (toSwap.parent != null) {
            if (toSwap.parent.left == toSwap)
                toSwap.parent.left = null;
            else
                toSwap.parent.right = null;
        }
        newRoot.revalidateDown();
        size--;
        if (toSwap == root)
            root = null;
        return toSwap;
    }

    public HeapNode getLast() {
        if (size == 1)
            return root;

        int numberOfLevels = 0;
        while (size >= (1 << numberOfLevels))
            numberOfLevels++;

        HeapNode tmp = root;
        //rozmiar heap'a w zapisie binarnym
        //z usuniętym ostatnim bitem pokazuje ścieżkę do ostatniego elementu
        int cursor = (1 << (numberOfLevels - 2));

        for (int i = 0; i < numberOfLevels - 1; i++) {
            tmp = ((size & cursor) == 0) ? tmp.left : tmp.right;
            cursor >>= 1;
        }

        return tmp;
    }

}

class HeapNode {
    public int priority;
    public TreeNode data;
    public HeapNode parent, left, right;

    public HeapNode(TreeNode data, int priority) {
        this.priority = priority;
        this.data = data;
    }

    public HeapNode concatWith(HeapNode node) {
        TreeNode newTree = new TreeNode();
        newTree.left = data;
        if (node == null)
            return new HeapNode(newTree, priority);
        newTree.right = node.data;
        return new HeapNode(newTree, priority + node.priority);
    }

    public void revalidateToUp() {//sprawdzanie w górę po dodaniu nowego węzła
        if (parent == null)
            return;
        if (parent.priority > this.priority) {
            swapValuesWith(this.parent);
            parent.revalidateToUp();
        }
    }

    public void revalidateDown() {//sprawdzanie w dół po zamianie korzenia z ostatnim liściem
        int numberOfChildren = 0;
        if (left != null)
            numberOfChildren++;
        if (right != null)
            numberOfChildren++;

        if (numberOfChildren == 1) {
            if (left != null) {
                if (left.priority < priority) {
                    swapValuesWith(left);
                    left.revalidateDown();
                }
            } else {
                if (right.priority < priority) {
                    swapValuesWith(right);
                    right.revalidateDown();
                }
            }
        } else if (numberOfChildren == 2) {
            boolean toRight = right.priority <= left.priority;
            swapValuesWith(toRight ? right : left);
            (toRight ? right : left).revalidateDown();
        }
    }

    public void swapValuesWith(HeapNode with) {
        int pr = this.priority;
        TreeNode tr = this.data;
        this.priority = with.priority;
        this.data = with.data;
        with.priority = pr;
        with.data = tr;
    }
}

class TreeNode {
    public TreeNode(char value) {
        this.value = value;
    }

    public TreeNode() {
    }

    public char value;
    public TreeNode left, right;

    public void printAll(String prefix) {
        if (left != null)
            left.printAll(prefix + "0");
        if (right != null)
            right.printAll(prefix + "1");
        if (right == null && left == null)
            System.out.println(value + " " + prefix);
    }
}