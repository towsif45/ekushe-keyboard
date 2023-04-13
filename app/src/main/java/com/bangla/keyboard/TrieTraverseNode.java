package com.bangla.keyboard;

/**
 * Created by ranit on 8/21/17.
 */
public class TrieTraverseNode implements Comparable<TrieTraverseNode> {
    public TrieNode cur_node;
    public String cur_word;
    public double score;
    public boolean flag;
    TrieTraverseNode(){
        cur_node = null;
        cur_word="";
        score=0.0;
        flag = false;
    }

    @Override
    public int compareTo(TrieTraverseNode o) {

//        if(o.score > this.score) {
//            if(this.flag == true && o.flag == false)
//                return -1;
//            return 1;
//        }
//        else if(o.score < this.score) {
//            if(o.flag == true && this.flag == false)
//                return 1;
//            return -1;
//        }
//        else{
//            if(o.flag == true && this.flag == false)
//                return 1;
//            else if(this.flag == true && o.flag == false)
//                return -1;
//            else
//                return 0;
//        }
        return (int) (o.score - this.score);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
