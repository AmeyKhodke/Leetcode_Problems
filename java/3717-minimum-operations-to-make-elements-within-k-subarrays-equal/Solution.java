class Solution {
    
    // Helper class to manage a balanced sliding window using TreeMaps to handle duplicates
    private static class BalancedWindow {
        private final TreeMap<Integer, Integer> left = new TreeMap<>();
        private final TreeMap<Integer, Integer> right = new TreeMap<>();
        private int leftSize = 0;
        private int rightSize = 0;
        private long leftSum = 0;
        private long rightSum = 0;

        // Adds an element to the window
        public void add(int val) {
            if (left.isEmpty() || val <= left.lastKey()) {
                left.put(val, left.getOrDefault(val, 0) + 1);
                leftSum += val;
                leftSize++;
            } else {
                right.put(val, right.getOrDefault(val, 0) + 1);
                rightSum += val;
                rightSize++;
            }
            balance();
        }

        // Removes an element from the window
        public void remove(int val) {
            if (left.containsKey(val)) {
                int count = left.get(val);
                if (count == 1) left.remove(val);
                else left.put(val, count - 1);
                leftSum -= val;
                leftSize--;
            } else {
                int count = right.get(val);
                if (count == 1) right.remove(val);
                else right.put(val, count - 1);
                rightSum -= val;
                rightSize--;
            }
            balance();
        }

        // Maintains the balance between the lower (left) and upper (right) halves
        private void balance() {
            if (leftSize > rightSize + 1) {
                int val = left.lastKey();
                removeNode(left, val);
                leftSum -= val;
                leftSize--;
                
                right.put(val, right.getOrDefault(val, 0) + 1);
                rightSum += val;
                rightSize++;
            } else if (leftSize < rightSize) {
                int val = right.firstKey();
                removeNode(right, val);
                rightSum -= val;
                rightSize--;
                
                left.put(val, left.getOrDefault(val, 0) + 1);
                leftSum += val;
                leftSize++;
            }
        }

        private void removeNode(TreeMap<Integer, Integer> map, int val) {
            int count = map.get(val);
            if (count == 1) map.remove(val);
            else map.put(val, count - 1);
        }

        // Calculates the absolute differences sum from the median
        public long getCost() {
            long median = left.lastKey();
            return (median * leftSize - leftSum) + (rightSum - median * rightSize);
        }
    }

    public long minOperations(int[] nums, int x, int k) {
        int n = nums.length;
        
        // Edge case: If the array isn't large enough to pick k windows of size x
        if ((long) k * x > n) {
            return -1; 
        }
        
        // Step 1: Precompute the minimum operations cost for each valid window of size x
        long[] windowCost = new long[n - x + 1];
        BalancedWindow window = new BalancedWindow();
        
        // Process the first window configuration
        for (int i = 0; i < x; i++) {
            window.add(nums[i]);
        }
        windowCost[0] = window.getCost();
        
        // Slide the window across the rest of the array
        for (int i = 1; i <= n - x; i++) {
            window.remove(nums[i - 1]);
            window.add(nums[i + x - 1]);
            windowCost[i] = window.getCost();
        }
        
        // Step 2: Use an optimized 1D Dynamic Programming grid to pick k non-overlapping windows
        long INF = (long) 1e18;
        long[] prevDp = new long[n + 1]; 
        long[] currDp = new long[n + 1]; 
        
        // Base case: 0 subarrays selected costs exactly 0 operations
        Arrays.fill(prevDp, 0);
        
        for (int i = 1; i <= k; i++) {
            Arrays.fill(currDp, INF);
            for (int j = i * x; j <= n; j++) {
                // Choice 1: Skip the current element at index j - 1
                currDp[j] = currDp[j - 1];
                
                // Choice 2: Form a valid subarray of length x ending at index j - 1 (starts at j - x)
                int startIdx = j - x;
                if (prevDp[startIdx] != INF) {
                    currDp[j] = Math.min(currDp[j], prevDp[startIdx] + windowCost[startIdx]);
                }
            }
            // Transition the current DP row array to the previous tracker
            System.arraycopy(currDp, 0, prevDp, 0, n + 1);
        }
        
        return prevDp[n];
    }
}