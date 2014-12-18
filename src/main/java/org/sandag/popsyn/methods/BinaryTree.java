/*   
 * Copyright 2014 Parsons Brinckerhoff

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License
   *
   */

package org.sandag.popsyn.methods;


// BinaryTree.java 
public class BinaryTree { 
  // Root node pointer. Will be null for an empty tree. 
  private Node root; 
 

  /* 
   --Node-- 
   The binary tree is built using this nested node class. 
   Each node stores one data element, and has left and right 
   sub-tree pointer which may be null. 
   The node is a "dumb" nested class -- we just use it for 
   storage; it does not have any methods. 
  */ 
  private static class Node { 
    Node left; 
    Node right; 
    int data;

    Node(int newData) { 
      left = null; 
      right = null; 
      data = newData; 
    } 
  }

  /** 
   Creates an empty binary tree -- a null root pointer. 
  */ 
  public void BinaryTree() { 
    root = null; 
  } 
 

  /** 
   Returns true if the given target is in the binary tree. 
   Uses a recursive helper. 
  */ 
  public boolean lookup(int data) { 
    return(lookup(root, data)); 
  } 
 

  /** 
   Recursive lookup  -- given a node, recur 
   down searching for the given data. 
  */ 
  private boolean lookup(Node node, int data) { 
    if (node==null) { 
      return(false); 
    }

    if (data==node.data) { 
      return(true); 
    } 
    else if (data<node.data) { 
      return(lookup(node.left, data)); 
    } 
    else { 
      return(lookup(node.right, data)); 
    } 
  } 
 

  /** 
   Inserts the given data into the binary tree. 
   Uses a recursive helper. 
  */ 
  public void insert(int data) { 
    root = insert(root, data); 
  } 
 

  /** 
   Recursive insert -- given a node pointer, recur down and 
   insert the given data into the tree. Returns the new 
   node pointer (the standard way to communicate 
   a changed pointer back to the caller). 
  */ 
  private Node insert(Node node, int data) { 
    if (node==null) { 
      node = new Node(data); 
    } 
    else { 
      if (data <= node.data) { 
        node.left = insert(node.left, data); 
      } 
      else { 
        node.right = insert(node.right, data); 
      } 
    }

    return(node); // in any case, return the new pointer to the caller 

  } 

  /** 
  Prints the node values in the "inorder" order. 
  Uses a recursive helper to do the traversal. 
 */ 
 public void printTree() { 
  printTree(root); 
  System.out.println(); 
 }
 private void printTree(Node node) { 
  if (node == null) return;

  // left, node itself, right 
  printTree(node.left); 
  System.out.print(node.data + "  "); 
  printTree(node.right); 
 } 
  

}