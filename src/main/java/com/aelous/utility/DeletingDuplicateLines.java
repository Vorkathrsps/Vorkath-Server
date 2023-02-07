package com.aelous.utility;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Patrick van Elderen | June, 11, 2021, 11:37
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DeletingDuplicateLines {

    public static void main(String[] args) throws Exception {
        String filePath = System.getProperty("user.home") + "/Desktop/emails_input.txt";
        String input = null;
        //Instantiating the Scanner class
        Scanner sc = new Scanner(new File(filePath));
        //Instantiating the FileWriter class
        FileWriter writer = new FileWriter(System.getProperty("user.home") + "/Desktop/emails_output.txt");
        //Instantiating the Set class
        Set set = new HashSet();
        while (sc.hasNextLine()) {
            input = sc.nextLine();
            if(set.add(input)) {
                writer.append(input+"\n");
            }
        }
        writer.flush();
        System.out.println("Contents added............");
    }
}
