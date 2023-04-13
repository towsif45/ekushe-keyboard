package com.bangla.keyboard;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by ranit on 3/18/17.
 * https://discuss.leetcode.com/topic/19221/ac-java-solution-simple-using-single-array/3
 */

public class Trie {

    Context context;
    private TrieNode root;
    public Trie(Context context) {
        this.context = context;
        root = new TrieNode();
        create_mapping();
    }
    HashMap<Character, Integer> mCharIntMap = new HashMap<>();
    HashMap<Integer, Character> mIntCharMap = new HashMap<>();

    public TrieNode getRootNode() {
        return root;
    }
    public void setRootNode(TrieNode tp_root) {
        root = tp_root;
    }

    public TrieNode isAWord_treeTrav(TrieNode curNode, char curChar) {

//        if(curChar == '`') return curNode.children[27];

        char c = curChar;
//        System.out.println("Trie 33 curChar c   "+ c);
        int id;

        if('a' <= c  && c<= 'z' ) {
            id = c - 'a';
        }
        else {
            id = mCharIntMap.get(c);
        }

        return curNode.children[ id ];
//        if(curNode.children[curChar - 'a'] == null) return false;
//
//        curNode = curNode.children[ curChar - 'a'];
//
//            return ;
    }

    public void insert(String word) {
        TrieNode ws = root;
        int id;
        for(int i = 0; i < word.length(); i++){
            char c = word.charAt(i);

            if('a' <= c  && c<= 'z' ) {
                id = c - 'a';
//                System.out.println(">>"+c+"<<");
            }
            else {
//                System.out.println("problem : >>"+c+"<<");
//                try {
//                    id = mCharIntMap.get(c);
//                }catch (Exception e){
//                    System.out.println("problem : >>"+c+"<<");
//                }
                id = mCharIntMap.get(c);
            }

            if( ws.children[ id ] == null) {
                ws.children[ id ] = new TrieNode();
                ws = ws.children[ id ];
            }
            else {
                ws = ws.children[ id ];
            }


//            if(c == '`') {
//                if( ws.children[27] == null) {
//                    ws.children[27] = new TrieNode();
//                }
//                else ws = ws.children[27];
//            }
//            else if(ws.children[c - 'a'] == null) ws.children[c - 'a'] = new TrieNode();
//            else ws = ws.children[c - 'a'];
            //ws = ws.children[c - 'a'];
        }
        ws.isWord = true;
    }

    public boolean search(String word) {
        TrieNode ws = root;
        int id;
        for(int i = 0; i < word.length(); i++){

            char c = word.charAt(i);
            if('a' <= c  && c<= 'z' ) {
                id = c - 'a';
            }
            else {
                id = mCharIntMap.get(c);
            }

            if( ws.children[ id ] == null) {
                return false;
            }
            else {
                ws = ws.children[ id ];
            }
//            if(c == '`') {
//                if( ws.children[27] == null)  return false;
//                else ws = ws.children[27];
//            }
//            else if(ws.children[c - 'a'] == null) return false;
//            else ws = ws.children[c - 'a'];
        }

        return ws.isWord;
    }

    public boolean startsWith(String prefix) {
        TrieNode ws = root;
        int id;
        for(int i = 0; i < prefix.length(); i++){
            char c = prefix.charAt(i);
//            if(ws.children[c - 'a'] == null) return false;
//            ws = ws.children[c - 'a'];

            if('a' <= c  && c<= 'z' ) {
                id = c - 'a';
            }
            else {
                id = mCharIntMap.get(c);
            }

            if( ws.children[ id ] == null) {
                return false;
            }
            else {
                ws = ws.children[ id ];
            }
        }
        return true;
    }

    public void wordSuggest(String word) {
        TrieNode ws = root;
        String curPrefix = "";
        int id;
        for(int i = 0; i < word.length(); i++){
            char c = word.charAt(i);
//            if(c == '`' && ws.children[27] == null) break;
//            else if(ws.children[c - 'a'] == null) break;
//            if(c == '`') ws = ws.children[27];
//            else ws = ws.children[c - 'a'];
            if('a' <= c  && c<= 'z' ) {
                id = c - 'a';
            }
            else {
                id = mCharIntMap.get(c);
            }

            if( ws.children[ id ] == null) {
                break;
            }
            else {
                ws = ws.children[ id ];
            }

            curPrefix = curPrefix.concat( ""+c );
        }

        if(ws == root ) return;
      //  System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        wordsWithSamePrefix(ws, curPrefix);
        return;
    }


    public void wordsWithSamePrefix(TrieNode curNode, String prefix) {
        TrieNode ws = curNode;
        if(ws.isWord) {
        //    System.out.println("prefix  " + prefix);
            MyKeyboard.printCharInConsoleRun(context,prefix);
        }
        for(int i = 0; i < 40; i++){

            if(ws.children[i] != null)  {
                char c=' ';

                if(i>26) {

                    if(mIntCharMap.get(i) == null ) continue;

                    c = mIntCharMap.get(i);
                }
                else {
                    c = (char)('a'+i);
                }
//                if(i == 27 ) c = '`';
//                else c = (char)('a'+i);
                prefix = prefix.concat(""+c );

                if(MyKeyboard.totalSuggestedWOrdsCount ==20){
                    break;
                }
                wordsWithSamePrefix(ws.children[i], prefix);

                StringBuilder sb = new StringBuilder(prefix);
                sb.deleteCharAt(prefix.length()-1);
                prefix = sb.toString();
            }
            if(MyKeyboard.totalSuggestedWOrdsCount ==20){
                break;
            }

        }
        return;
    }

    private void create_mapping() {
        mCharIntMap.put('`', 27); mIntCharMap.put(27, '`');
        mCharIntMap.put('ং', 28); mIntCharMap.put(28, 'ং');
        mCharIntMap.put('ঃ', 29); mIntCharMap.put(29, 'ঃ');
        mCharIntMap.put('ঁ', 30); mIntCharMap.put(30, 'ঁ');
        mCharIntMap.put('T', 31); mIntCharMap.put(31, 'T');
        mCharIntMap.put('D', 32); mIntCharMap.put(32, 'D');
        mCharIntMap.put('O', 33); mIntCharMap.put(33, 'O');
        mCharIntMap.put('N', 34); mIntCharMap.put(34, 'N');
        mCharIntMap.put('S', 35); mIntCharMap.put(35, 'S');
        mCharIntMap.put('R', 36); mIntCharMap.put(36, 'R');
        mCharIntMap.put('I', 37); mIntCharMap.put(37, 'I');
        mCharIntMap.put('U', 38); mIntCharMap.put(38, 'U');
        mCharIntMap.put('Z', 39); mIntCharMap.put(39, 'Z');
        mCharIntMap.put('Y', 40); mIntCharMap.put(40, 'Y');
        mCharIntMap.put('J', 41); mIntCharMap.put(41, 'J');
        mCharIntMap.put('G', 42); mIntCharMap.put(42, 'G');
        mCharIntMap.put(':', 43); mIntCharMap.put(43, ':');
        mCharIntMap.put('^', 44); mIntCharMap.put(44, '^');
        mCharIntMap.put('-', 45); mIntCharMap.put(45, '-');
        mCharIntMap.put(',', 46); mIntCharMap.put(46, ',');
        mCharIntMap.put('.', 47); mIntCharMap.put(47, '.');
        mCharIntMap.put('’', 48); mIntCharMap.put(48, '’');
        mCharIntMap.put('্', 49); mIntCharMap.put(49, '্');
        mCharIntMap.put('(', 50); mIntCharMap.put(50, '(');
        mCharIntMap.put(')', 51); mIntCharMap.put(51, ')');
        mCharIntMap.put('\u200C', 52); mIntCharMap.put(52, '\u200C');
        mCharIntMap.put('–', 52); mIntCharMap.put(52, '–');
        mCharIntMap.put('H', 53); mIntCharMap.put(53, 'H');

    }
}
