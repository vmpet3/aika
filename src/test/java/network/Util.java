/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public class Util {

    public static List<String> loadExamplesAsWords(File dir) throws IOException {
        ArrayList<String> words = new ArrayList<>();
        for (File f : dir.listFiles()) {
            InputStream is = new FileInputStream(f);
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            String txt = writer.toString();
            txt = txt + " ";

            int wb = 0;
            char lc = ' ';
            for(int i = 0; i < txt.length(); i++) {
                char c = txt.charAt(i);

                if(Character.isLetter(c) && !Character.isLetter(lc)) {
                    wb = i;
                } else if(!Character.isLetter(c) && Character.isLetter(lc)) {
                    String word = txt.substring(wb, i);

                    words.add(word.toLowerCase());
                }

                lc = c;
            }
        }
        return words;
    }
}
