package com.aelous.utility.test.generic;

import com.aelous.utility.chainedwork.Chain;

/**
 * See {@link Chain}
 * @author Jak | Shadowrs
 * @version 25/4/2020
 */
public class ChainWorkTest {

    public static void main(String[] args) {
        test();
    }

    static class Instance {
        public Instance(String name) {
            this.name = name;
        }

        public String name;

        @Override
        public String toString() {
            return "Instance{" +
                "name='" + name + '\'' +
                '}';
        }
    }

    public static void test() {
        Chain.DEBUG_CHAIN = true;

        final Instance owner = new Instance("testowner");
        Chain<Instance> v1 = Chain.bound(owner).name("ChainWorkTest").cancelWhen(() -> {
            System.out.println("[debug compare] " + owner.name + " to " + owner);
            return owner.name.equalsIgnoreCase("blyat");
        });

        v1.__TESTING_ONLY_doWork(); // expect run fine
        owner.name = "blyat";
        v1.__TESTING_ONLY_doWork(); // expect failure

    }
}
