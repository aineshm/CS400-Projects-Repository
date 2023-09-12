// --== CS400 Spring 2023 File Header Information ==--
// Name: Ainesh Mohan
// Email: amohan28@wisc.edu
// Team: BS
// TA: Samuel Church
// Lecturer: Gary Dahl
// Notes to Grader: N/A

import java.util.LinkedList;
import java.util.Stack;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Red-Black Tree implementation with a Node inner class for representing the
 * nodes of the tree. Currently, this implements a Binary Search Tree that we
 * will turn into a red black tree by modifying the insert functionality. In
 * this activity, we will start with implementing rotations for the binary
 * search tree insert algorithm.
 */
public class RedBlackTree<T extends Comparable<T>> implements SortedCollectionInterface<T> {

	
	/**
	 * This class represents a node holding a single value within a binary tree.
	 */
	protected static class Node<T> {
		public T data;
		public int blackHeight;
		// The context array stores the context of the node in the tree:
		// - context[0] is the parent reference of the node,
		// - context[1] is the left child reference of the node,
		// - context[2] is the right child reference of the node.
		// The @SupressWarning("unchecked") annotation is used to supress an unchecked
		// cast warning. Java only allows us to instantiate arrays without generic
		// type parameters, so we use this cast here to avoid future casts of the
		// node type's data field.
		@SuppressWarnings("unchecked")
		public Node<T>[] context = (Node<T>[]) new Node[3];
		
		
		/**
		 * constructor for Node object
		 * 
		 * @param data - data that the node contains
		 */
		public Node(T data) {
			
			this.data = data;
			this.blackHeight = 0;
			
		}
		
		

		/**
		 * @return true when this node has a parent and is the right child of that
		 *         parent, otherwise return false
		 */
		public boolean isRightChild() {
			
			return context[0] != null && context[0].context[2] == this;
			
		}

	}
	
	
	
	
	

	// fields for RBT

	protected Node<T> root; // reference to root node of tree, null when empty
	protected int size = 0; // the number of values in the tree

	
	
	
	
	
	
	
	/**
	 * Performs a naive insertion into a binary search tree: adding the input data
	 * value to a new node in a leaf position within the tree. After this insertion,
	 * no attempt is made to restructure or balance the tree. This tree will not
	 * hold null references, nor duplicate data values.
	 * 
	 * @param data to be added into this binary search tree
	 * @return true if the value was inserted, false if not
	 * @throws NullPointerException     when the provided data argument is null
	 * @throws IllegalArgumentException when data is already contained in the tree
	 */
	public boolean insert(T data) throws NullPointerException, IllegalArgumentException {
		// null references cannot be stored within this tree
		if (data == null)
			throw new NullPointerException("This RedBlackTree cannot store null references.");

		
		Node<T> newNode = new Node<>(data);
		
		
		if (this.root == null) {
			
			// add first node to an empty tree
			root = newNode;
			size++;
			
			enforceRBTreePropertiesAfterInsert(newNode);
			
			return true;
			
			
		} else {
			
			// insert into subtree
			Node<T> current = this.root;
			while (true) {
				
				int compare = newNode.data.compareTo(current.data);
				
				if (compare == 0) {
					
					throw new IllegalArgumentException("This RedBlackTree already contains value " + data.toString());
				
				} else if (compare < 0) {
					
					// insert in left subtree
					if (current.context[1] == null) {
						
						// empty space to insert into
						current.context[1] = newNode;
						newNode.context[0] = current;
						this.size++;
						enforceRBTreePropertiesAfterInsert(newNode); // calls the enforce method to make sure the RBT
																		// properties are enforced
						return true;
						
					} else {
						
						// no empty space, keep moving down the tree
						current = current.context[1];
						
					}
				} else {
					
					// insert in right subtree
					if (current.context[2] == null) {
						
						// empty space to insert into
						current.context[2] = newNode;
						newNode.context[0] = current;
						this.size++;

						enforceRBTreePropertiesAfterInsert(newNode); // calls the enforce method to make sure the RBT
																		// properties are enforced
						return true;
					} else {
						
						// no empty space, keep moving down the tree
						current = current.context[2];
						
					}
				}
			}
		}
	}

	
	
	
	
	
	
	
	/**
	 * Protected method to fix any RBT violations upon the insertion of a new node
	 * Makes use of the getUncle() helper method in order to obtain the uncle of the
	 * newlyAdded node
	 * 
	 * @param newlyAdded - the newly inserted node
	 */
	protected void enforceRBTreePropertiesAfterInsert(Node<T> newlyAdded) {

		// If inserted node is the root node
		if (newlyAdded.equals(root) || newlyAdded.context[0].blackHeight == 1) {

			root.blackHeight = 1;
			return;
		}
		
		

		if (newlyAdded.context[0].context[0] == null) {
			
			root.blackHeight = 1;
			return;
		}
		
		
		

		// creating parent, grandparent and uncle nodes for convenience

		Node<T> parent = newlyAdded.context[0];
		Node<T> grandparent = newlyAdded.context[0].context[0];
		Node<T> uncle = getUncle(newlyAdded);
		
		
		

		// case 1 - inserted child has black uncle in the same direction

		// if the newlyAdded node is a left child
		if (newlyAdded.isRightChild()) {

			if (parent.blackHeight == 0 && !parent.isRightChild() // checking if parent is left child
					&& (grandparent.context[2] == null || grandparent.context[2].blackHeight == 1)) {

				// extra rotate to get to case 2
				rotate(newlyAdded, parent);
				enforceRBTreePropertiesAfterInsert(newlyAdded.context[1]);

				root.blackHeight = 1;
				return;
			}
		}

		
		
		
		// if the newlyAdded node is a left child
		if (!newlyAdded.isRightChild()) {

			if (parent.blackHeight == 0 && parent.isRightChild() // checking if parent is right child
					&& (grandparent.context[1] == null || grandparent.context[1].blackHeight == 1)) {

				// extra rotate to get case 2
				rotate(newlyAdded, parent);
				enforceRBTreePropertiesAfterInsert(newlyAdded.context[2]);

				root.blackHeight = 1;
				return;

			}
		}

		// case 2 - inserted child has black uncle of opposite direction

		// inserted node is left child
		if (!newlyAdded.isRightChild()) {

			if (parent.blackHeight == 0 && (uncle == null || uncle.blackHeight == 1)) {

				// rotate and color swap
				rotate(parent, grandparent);
				parent.blackHeight = 1;
				parent.context[2].blackHeight = 0;

				root.blackHeight = 1;
				return;
			}
		}
		
		
		

		// inserted node is right child
		if (newlyAdded.isRightChild()) {

			if (parent.blackHeight == 0 && (uncle == null || uncle.blackHeight == 1)) {

				// rotate and color swap
				rotate(parent, grandparent);
				parent.blackHeight = 1;
				parent.context[1].blackHeight = 0;

				root.blackHeight = 1;
				return;
			}
		}

		// case 3 - inserted child has a red uncle

		if (uncle != null && uncle.blackHeight == 0) {

			parent.blackHeight = 1;
			uncle.blackHeight = 1;
			grandparent.blackHeight = 0;
			enforceRBTreePropertiesAfterInsert(grandparent); // Recursively fix the grandparent

			return;
		}

		// determining what the RBT violation is

		root.blackHeight = 1;
	}
	
	
	

	/**
	 * Helper method that gets the uncle of the given node
	 *
	 * @param node - the node whose uncle to get
	 * @return the uncle of the node, or null if no uncle exists
	 */
	private Node<T> getUncle(Node<T> node) {
		Node<T> parent = node.context[0];
		Node<T> grandparent = parent != null ? parent.context[0] : null;
		if (grandparent == null) {
			return null;
		}
		return parent.isRightChild() ? grandparent.context[1] : grandparent.context[2];
	}
	
	
	

	/**
	 * Performs the rotation operation on the provided nodes within this tree. When
	 * the provided child is a left child of the provided parent, this method will
	 * perform a right rotation. When the provided child is a right child of the
	 * provided parent, this method will perform a left rotation. When the provided
	 * nodes are not related in one of these ways, this method will throw an
	 * IllegalArgumentException.
	 * 
	 * @param child  is the node being rotated from child to parent position
	 *               (between these two node arguments)
	 * @param parent is the node being rotated from parent to child position
	 *               (between these two node arguments)
	 * @throws IllegalArgumentException when the provided child and parent node
	 *                                  references are not initially (pre-rotation)
	 *                                  related that way
	 */
	private void rotate(Node<T> child, Node<T> parent) throws IllegalArgumentException {
		
		if (parent != null && parent.context[1] != child && parent.context[2] != child) {
			throw new IllegalArgumentException("Parent and child nodes given are not related");

		}

		// initializing a grandparent variable to store the value of the parent node of
		// the given parent node
		// in case the rotation is not at the root node
		Node<T> grandparent = null;

		// checking if parent argument has a parent
		if (parent.context[0] != null) {
			grandparent = parent.context[0];
			// removing link from grandparent to parent and instead connecting it to child
			if (grandparent.context[1] == parent) {
				grandparent.context[1] = child;
			} else if (grandparent.context[2] == parent) {
				grandparent.context[2] = child;
			}
		}

		// child node to the grandparent node
		child.context[0] = grandparent;

		// left rotation implementation

		if (parent.context[2] == child) {
			if (child.context[1] != null) {
				child.context[1].context[0] = parent;
			}

			parent.context[2] = child.context[1];
			child.context[1] = parent;
			parent.context[0] = child;
		}

		// right rotation implementation
		if (parent.context[1] == child) {

			if (child.context[2] != null) {
				child.context[2].context[0] = parent;
			}

			parent.context[1] = child.context[2];
			child.context[2] = parent;
			parent.context[0] = child;
		}

		if (root != child && grandparent == null) {
			root = child;
		}
	}
	
	
	

	/**
	 * Get the size of the tree (its number of nodes).
	 * 
	 * @return the number of nodes in the tree
	 */
	public int size() {
		return size;
	}
	
	
	
	

	/**
	 * Method to check if the tree is empty (does not contain any node).
	 * 
	 * @return true of this.size() return 0, false if this.size() > 0
	 */
	public boolean isEmpty() {
		return this.size() == 0;
	}
	
	
	
	
	
	

	/**
	 * Removes the value data from the tree if the tree contains the value. This
	 * method will not attempt to rebalance the tree after the removal and should be
	 * updated once the tree uses Red-Black Tree insertion.
	 * 
	 * @return true if the value was remove, false if it didn't exist
	 * @throws NullPointerException     when the provided data argument is null
	 * @throws IllegalArgumentException when data is not stored in the tree
	 */
	public boolean remove(T data) throws NullPointerException, IllegalArgumentException {
		// null references will not be stored within this tree
		if (data == null) {
			throw new NullPointerException("This RedBlackTree cannot store null references.");
		} else {
			Node<T> nodeWithData = this.findNodeWithData(data);

			// throw exception if node with data does not exist
			if (nodeWithData == null) {

				throw new IllegalArgumentException(

						"The following value is not in the tree and cannot be deleted: " + data.toString());

			}

			boolean hasRightChild = (nodeWithData.context[2] != null);
			boolean hasLeftChild = (nodeWithData.context[1] != null);

			if (hasRightChild && hasLeftChild) {

				// has 2 children
				Node<T> successorNode = this.findMinOfRightSubtree(nodeWithData);

				// replace value of node with value of successor node
				nodeWithData.data = successorNode.data;

				// remove successor node
				if (successorNode.context[2] == null) {

					// successor has no children, replace with null
					this.replaceNode(successorNode, null);

				} else {

					// successor has a right child, replace successor with its child
					this.replaceNode(successorNode, successorNode.context[2]);

				}

			} else if (hasRightChild) {

				// only right child, replace with right child
				this.replaceNode(nodeWithData, nodeWithData.context[2]);

			} else if (hasLeftChild) {

				// only left child, replace with left child
				this.replaceNode(nodeWithData, nodeWithData.context[1]);

			} else {

				// no children, replace node with a null node
				this.replaceNode(nodeWithData, null);

			}
			this.size--;
			return true;
		}
	}

	/**
	 * Checks whether the tree contains the value *data*.
	 * 
	 * @param data the data value to test for
	 * @return true if *data* is in the tree, false if it is not in the tree
	 */
	public boolean contains(T data) {

		// null references will not be stored within this tree
		if (data == null) {

			throw new NullPointerException("This RedBlackTree cannot store null references.");

		} else {

			Node<T> nodeWithData = this.findNodeWithData(data);

			// return false if the node is null, true otherwise
			return (nodeWithData != null);

		}
	}

	/**
	 * Helper method that will replace a node with a replacement node. The
	 * replacement node may be null to remove the node from the tree.
	 * 
	 * @param nodeToReplace   the node to replace
	 * @param replacementNode the replacement for the node (may be null)
	 */
	protected void replaceNode(Node<T> nodeToReplace, Node<T> replacementNode) {

		if (nodeToReplace == null) {

			throw new NullPointerException("Cannot replace null node.");

		}

		if (nodeToReplace.context[0] == null) {

			// we are replacing the root
			if (replacementNode != null)
				replacementNode.context[0] = null;

			this.root = replacementNode;

		} else {

			// set the parent of the replacement node
			if (replacementNode != null)
				replacementNode.context[0] = nodeToReplace.context[0];

			// do we have to attach a new left or right child to our parent?
			if (nodeToReplace.isRightChild()) {

				nodeToReplace.context[0].context[2] = replacementNode;

			} else {

				nodeToReplace.context[0].context[1] = replacementNode;
			}
		}
	}

	/**
	 * Helper method that will return the inorder successor of a node with two
	 * children.
	 * 
	 * @param node the node to find the successor for
	 * @return the node that is the inorder successor of node
	 */
	protected Node<T> findMinOfRightSubtree(Node<T> node) {

		if (node.context[1] == null && node.context[2] == null) {
			throw new IllegalArgumentException("Node must have two children");
		}

		// take a steop to the right
		Node<T> current = node.context[2];

		while (true) {

			// then go left as often as possible to find the successor
			if (current.context[1] == null) {
				// we found the successor
				return current;

			} else {
				current = current.context[1];
			}
		}
	}

	/**
	 * Helper method that will return the node in the tree that contains a specific
	 * value. Returns null if there is no node that contains the value.
	 * 
	 * @return the node that contains the data, or null of no such node exists
	 */
	protected Node<T> findNodeWithData(T data) {
		Node<T> current = this.root;

		while (current != null) {

			int compare = data.compareTo(current.data);

			if (compare == 0) {

				// we found our value
				return current;

			} else if (compare < 0) {

				// keep looking in the left subtree
				current = current.context[1];

			} else {

				// keep looking in the right subtree
				current = current.context[2];

			}
		}
		// we're at a null node and did not find data, so it's not in the tree
		return null;
	}

	/**
	 * This method performs an inorder traversal of the tree. The string
	 * representations of each data value within this tree are assembled into a
	 * comma separated string within brackets (similar to many implementations of
	 * java.util.Collection, like java.util.ArrayList, LinkedList, etc).
	 * 
	 * @return string containing the ordered values of this tree (in-order
	 *         traversal)
	 */
	public String toInOrderString() {

		// generate a string of all values of the tree in (ordered) in-order
		// traversal sequence
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");

		if (this.root != null) {

			Stack<Node<T>> nodeStack = new Stack<>();
			Node<T> current = this.root;

			while (!nodeStack.isEmpty() || current != null) {

				if (current == null) {

					Node<T> popped = nodeStack.pop();
					sb.append(popped.data.toString());

					if (!nodeStack.isEmpty() || popped.context[2] != null)
						sb.append(", ");

					current = popped.context[2];

				} else {

					nodeStack.add(current);
					current = current.context[1];

				}
			}
		}

		sb.append(" ]");
		return sb.toString();
	}

	/**
	 * This method performs a level order traversal of the tree. The string
	 * representations of each data value within this tree are assembled into a
	 * comma separated string within brackets (similar to many implementations of
	 * java.util.Collection). This method will be helpful as a helper for the
	 * debugging and testing of your rotation implementation.
	 * 
	 * @return string containing the values of this tree in level order
	 */
	public String toLevelOrderString() {

		StringBuffer sb = new StringBuffer();
		sb.append("[ ");

		if (this.root != null) {

			LinkedList<Node<T>> q = new LinkedList<>();
			q.add(this.root);

			while (!q.isEmpty()) {

				Node<T> next = q.removeFirst();

				if (next.context[1] != null)
					q.add(next.context[1]);

				if (next.context[2] != null)
					q.add(next.context[2]);

				sb.append(next.data.toString());

				if (!q.isEmpty())
					sb.append(", ");
			}
		}
		sb.append(" ]");
		return sb.toString();
	}

	public String toString() {
		return "level order: " + this.toLevelOrderString() + "\nin order: " + this.toInOrderString();
	}

	
	
	
	
	// defining an RBT with type Integer for tester methods
	RedBlackTree<Integer> testTree;

	/**
	 * Setup before each test to instantiate a new RBT
	 * 
	 */
	@BeforeEach
	void setUp() {
		
		testTree = new RedBlackTree<Integer>();
		
	}

	
	
	
	/**
	 * Testing if root node is black and if newly inserted nodes that do not violate
	 * RBT properties are red
	 * 
	 */
	@Test
	void test1() {

		// testing if first node added (root) is black
		testTree.insert(5);
		assertEquals(1, testTree.root.blackHeight, "Root should have blackHeight 1");

		// testing if next nodes added are red (black parent)
		testTree.insert(3);
		assertEquals(0, testTree.root.context[1].blackHeight, "New node should have blackHeight 0");

		testTree.insert(7);
		assertEquals(0, testTree.root.context[2].blackHeight, "New node should have blackHeight 0");
	}
	
	
	
	
	

	/**
	 * Testing insertion case when the new node has a red uncle
	 */
	@Test
	void test2() {
		testTree.insert(3);
		testTree.insert(2);
		testTree.insert(5);

		// test case: node added has a red parent and red uncle
		testTree.insert(1);

		// testing if re-coloring has been done correctly for test case

		assertEquals(1, testTree.root.context[1].blackHeight, "New node should have blackHeight 1");

		assertEquals(1, testTree.root.context[2].blackHeight, "New node should have blackHeight 1");

	}
	
	
	
	
	

	/**
	 * Testing insertion case when the new node has a black uncle
	 * 
	 */
	@Test
	void test3() {
		// added node has a red parent and black uncle

		testTree.insert(6);
		testTree.insert(4);
		testTree.insert(8);
		testTree.insert(10);
		testTree.insert(3);
		testTree.insert(5);
		testTree.insert(2);
		testTree.insert(1);

		// node with data 2 should be black after rotation
		assertEquals(1, testTree.root.context[1].context[1].blackHeight, "node should have blackHeight 1");

		assertEquals(0, testTree.root.context[1].context[1].context[1].blackHeight, "node should have blackHeight 0");

		assertEquals("[ 6, 4, 8, 2, 5, 10, 1, 3 ]", testTree.toLevelOrderString(), "Level order should match");

		RedBlackTree<Integer> testTree1 = new RedBlackTree<Integer>();
		
		

		// most recently added node has a red parent and black uncle
		
		testTree1.insert(55);
		testTree1.insert(30);
		testTree1.insert(80);
		testTree1.insert(15);
		testTree1.insert(45);
		testTree1.insert(50);
		testTree1.insert(48);

		
		
		assertEquals(1, testTree1.root.context[1].context[2].blackHeight, "node should have blackHeight 1");

		assertEquals(0, testTree1.root.context[1].context[2].context[1].blackHeight, "node should have blackHeight 0");

		assertEquals(0, testTree1.root.context[1].context[2].context[2].blackHeight, "node should have blackHeight 0");

		assertEquals("[ 55, 30, 80, 15, 48, 45, 50 ]", testTree1.toLevelOrderString(),
				"toLevelOrderString should match desired output");

	}

}
