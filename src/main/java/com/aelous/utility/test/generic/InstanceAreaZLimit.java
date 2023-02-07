package com.aelous.utility.test.generic;

/**
 * @author Jak Shadowrs tardisfan121@gmail.com
 */
public class InstanceAreaZLimit {

    public static void main(String[] args) {
        System.out.printf("%s %s %s %s | %s %s%n",
            5 / 4, 6/4, 5 % 4, 6 % 4, 12 / 4, 12 % 4);
        test(0, 0, true, true); // z match exact
        test(0, 0, false, true); // non-z match NON INSTANCE allows 0-3 values OK
        test(0, 1, false, true); // non-z match NON INSTANCE allows 0-3 values OK
        test(0, 2, false, true); // non-z match NON INSTANCE allows 0-3 values OK
        test(0, 3, false, true); // non-z match NON INSTANCE allows 0-3 values OK
        test(0, 4, false, false); // non-z match NON INSTANCE allows 0-3 values reject
        test(5, 6, true, false); // z match exact
        test(5, 5, false, true); // non-z match but INSTANCE
        test(5, 6, false, true); // non-z match but INSTANCE
        test(5, 7, false, true); // non-z match but INSTANCE
        test(5, 8, false, false); // non-z match but INSTANCE
        test(5, 9, false, false); // non-z match but INSTANCE
        test(5, 7, true, false); // z match exact
        test(5, 10, true, false); // z match exact
        test(16, 16, false, true); // non-z match but INSTANCE
        test(16, 17, false, true); // non-z match but INSTANCE
        test(16, 18, false, true); // non-z match but INSTANCE
        test(16, 19, false, true); // non-z match but INSTANCE
        test(16, 20, false, false); // non-z match but INSTANCE
        test(16, 21, false, false); // non-z match but INSTANCE

        test(21, 16, false, false); // non-z match but INSTANCE
        test(18, 16, false, true); // non-z match but INSTANCE
    }

    private static void test(int i, int i1, boolean matchZ, boolean expected) {
        if (contains(i, i1, matchZ) != expected) {
            new Exception("fail "+!expected+" vs "+expected).printStackTrace();
        }
    }


    public static boolean contains(int z1, int z2, boolean checkZ) {
        //if (t.x >= x1 && t.x <= x2 && t.y >= y1 && t.y <= y2) {
            if (checkZ) {
                return z2 == z1;
            } else {
                if (z1 < 3 && z2 < 3) {
                    return true;
                } else {
                    // deal with instances! allow 0-3 on your own instance gap
                    int lowerBound = (z1 / 4) * 4; // 16 /4 = 4th instance, *4 gets lowerBOund 16. if it was 17 it'd still be lowerbound 16.
                    int zInInstance = z1 % 4;
                    System.out.printf("test %s vs %s is lvl %s in %s-%s%n", z1, z2, zInInstance, lowerBound, (lowerBound + 3));
                    return z2 >= lowerBound && z2 <= (lowerBound + 3);
                }
            }
        //}
        //return false;
    }
}
