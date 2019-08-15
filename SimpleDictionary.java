/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    /**
     * Computer plays simply by selecting the same word every time
     * obtained from the Binary Search
     * @param prefix
     * @return returns either the selected word or an appropriate string
     */
    @Override
    public String getAnyWordStartingWith(String prefix) {

        /**
         * If computer's turn is first, prefix would be null,
         * so we append the first character from the random selected word
         */
        if(prefix == ""){
            Random random = new Random();
            int randomIndex = random.nextInt(words.size());
            String randomPrefixWord = words.get(randomIndex);
            String randomPrefix = String.valueOf(randomPrefixWord.charAt(0));
            return randomPrefix;
        }
        /**
         * If computer's turn is not first, prefix would not be null,
         * so we search for the word and return appropriate string
         */
        else{
            int indexOfWordForComputer = searchIfWordIsPossible(prefix);
            String wordForComputer;
            if(indexOfWordForComputer != -1){
                wordForComputer = words.get(indexOfWordForComputer);
                if(wordForComputer.equals(prefix))
                    return "sameAsPrefix";
                else
                    return wordForComputer;
            }
            else
                return "noWord";
        }
    }

    /**
     * Search for the word starting with the input prefix
     * @param prefix
     * @return either index of the found word or -1
     */
    public int searchIfWordIsPossible(String prefix){
        int lowerIndex = 0;
        int upperIndex = words.size() - 1;
        int middleIndex, mismatchValue;
        String wordFromWordsList;
        while(lowerIndex <= upperIndex){
            middleIndex = (lowerIndex + upperIndex)/2;
            wordFromWordsList = words.get(middleIndex);
            mismatchValue = wordFromWordsList.startsWith(prefix) ? 0 : prefix.compareTo(wordFromWordsList);
            if(mismatchValue == 0)
                return middleIndex;
            else if(mismatchValue > 0)
                lowerIndex = middleIndex + 1;
            else
                upperIndex = middleIndex - 1;
        }
        return -1;
    }

    /**
     * Computer plays smartly by selecting a random word from the
     * short listed possible words. Think of downIndex as the index
     * moving from right to left & upIndex as the index moving from
     * left to right from the possibleWordIndex and all the indices
     * from a horizontal view
     * @param prefix
     * @param whoWentFirst is 1 if it's user's turn, else 0
     * @return returns either the selected word or an appropriate string
     */
    @Override
    public String getGoodWordStartingWith(String prefix, int whoWentFirst) {
        String selected = null;
        int  possibleWordIndex = 0,upIndex=0,downIndex=0,t;
        String possibleWord,wordFromWordsList;
        ArrayList<String> shortListedWords = new ArrayList<String>();
        Random randomWordIndex = new Random();
        System.out.println("Intially-->possiblewordindex-->"+possibleWordIndex+" upindex-->"+upIndex+" downlindx-->"+downIndex);
        if(prefix == ""){
            System.out.println("Computer turn first----->");
            Random random = new Random();

            int randomIndex = random.nextInt(words.size());
            return words.get(randomIndex);
        }
        else{
            possibleWordIndex = searchIfWordIsPossible(prefix);
            System.out.println("possiblewordindex-->"+possibleWordIndex);
            upIndex = downIndex = possibleWordIndex;
            System.out.println("Now()-->possiblewordindex-->"+possibleWordIndex+" upindex-->"+upIndex+" downlindx-->"+downIndex);

            if(possibleWordIndex == -1){
                return "noWord";
            }
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            possibleWord = words.get(possibleWordIndex);
            System.out.println("possibleowrd   "+possibleWord);
            shortListedWords.add(possibleWord);
            while(true){
                System.out.println("upindex entering loop-->"+upIndex);
                upIndex++;
                System.out.println("upindex ++-->"+upIndex);
                if(upIndex == words.size()){
                    System.out.println("upindex==breaking---"+words.size());
                    break;
                }
                wordFromWordsList = words.get(upIndex);
                System.out.println("wordfromwordslist-->"+wordFromWordsList);
                t = wordFromWordsList.startsWith(prefix)? 0 : prefix.compareTo(wordFromWordsList);
                System.out.println("t--->"+t);
                if(t != 0){

                    break;
                }
                System.out.println("whoendsfirdt-->"+whoWentFirst);
                System.out.println("wordfromwordslist.lengthmod2-->"+wordFromWordsList.length()%2);
                if(wordFromWordsList.length()%2 == whoWentFirst){

                    shortListedWords.add(wordFromWordsList);
                }
            }
            while(true){
                System.out.println("downindex entering loop-->"+downIndex);
                downIndex--;
                System.out.println("dwonindex -- -->"+downIndex);

                if(downIndex < 0){
                    break;
                }
                wordFromWordsList = words.get(downIndex);
                System.out.println("wordfromwordslist-->"+wordFromWordsList);
                t = wordFromWordsList.startsWith(prefix)? 0 : prefix.compareTo(wordFromWordsList);
                System.out.println("t--->"+t);
                if(t != 0){
                    break;
                }
                System.out.println("whoendsfirdt-->"+whoWentFirst);
                System.out.println("wordfromwordslist.lengthmod2-->"+wordFromWordsList.length()%2);
                if(wordFromWordsList.length()%2 == whoWentFirst){
                    shortListedWords.add(wordFromWordsList);
                }
            }
        }
        if(shortListedWords.size() == 0){
            return "noWord";
        }else{
            int selectedWordIndex = randomWordIndex.nextInt(shortListedWords.size());
            selected = shortListedWords.get(selectedWordIndex);
            if(selected.equals(prefix)){
                return "sameAsPrefix";
            }
        }
        return selected;
    }


}
