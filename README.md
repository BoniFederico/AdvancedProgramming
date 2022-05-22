# AdvancedProgramming

Project for the advanced programming course at University of Trieste
```java 
 public class UnsortedDictionary<V> {
	 static public int SIZE = Short.MAX_VALUE; 
	 private int length; private Object[] keys; 
	 public UnsortedDictionary() { this.length = 0; this.keys = new Object[SIZE]; } 
	 public int size() { return length; } // Complexity: O(n) where n is the size of the dictionary 
	 public V deletion(V key) { for (int i = 0; i < length; i++) { if (keys[i].equals(key)) { System.arraycopy(keys, i + 1, keys, i, length - i); length--; return key; } } return null; } // Complexity: O(1)
	 public V insertion(V key) { if (length == SIZE) { return null; } else { keys[length] = key; length++; return key; } } // Worst case complexity: O(n) where n is length of the dictionary 
	 @SuppressWarnings("unchecked") public V search(V key) { for (int i = 0; i < length; i++) { if (keys[i].equals(key)) { return (V) keys[i]; } } return null; } }
```
