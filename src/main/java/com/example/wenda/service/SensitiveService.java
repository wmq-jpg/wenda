package com.example.wenda.service;


import com.example.wenda.controller.QuestionController;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(  SensitiveService.class);

    private class TrieNode{
        //该节点是否是敏感词的末尾
        private boolean  end=false;

        //节点的下一个节点，character是字符，TrieNode是对应的下一个节点
        private  HashMap<Character,TrieNode> subNodes=new HashMap<>();

        public boolean isKeyWordEnd()
        {
            return end;
        }
        void addSubNode(Character c,TrieNode  node)
        {
            subNodes.put(c,node);
        }
        void setKeywordEnd(boolean end)
        {
            this.end=end;
        }

        public TrieNode getSubNode(Character c)
        {
            return subNodes.get(c);
        }

    }
    private TrieNode rootNode=new TrieNode();

    //0x2E80到0x9FFF是东亚范围
    public boolean isSymbol(char c)
    {
        int ic=(int)c;
        return !CharUtils.isAsciiAlphanumeric(c)&&(ic < 0x2E80 || ic > 0x9FFF);
    }
    public void addWord(String word)
    {
         TrieNode tempNode=rootNode;
         for(int i=0;i<word.length();i++)
         {
             if(isSymbol(word.charAt(i)))
                 continue;
             TrieNode node=tempNode.getSubNode(word.charAt(i));
             if(node==null)
             {   node=new TrieNode();
                 tempNode.addSubNode(word.charAt(i),node);
             }
             tempNode=node;
             if(i==word.length()-1)
               tempNode.setKeywordEnd(true);
         }

    }


    public String filter(String text)
    {
         if(StringUtils.isBlank(text))
         {
             return null;
         }

         String REPLACEMENT="XXX";
         StringBuilder result=new StringBuilder();
         TrieNode tempNode=rootNode;
         int begin=0;
         //当前比较的位置
         int position=0;
         while(position<text.length())
         {  char c=text.charAt(position);
           if(isSymbol(c))
           {
               if(tempNode==rootNode)
               {
                   begin++;
                   result.append(c);
               }
               position++;
               continue;
           }
             tempNode=tempNode.getSubNode(c);
             if(tempNode==null)
             {


                 result.append(text.charAt(begin));
                 position=begin+1;
                 begin=position;
                 tempNode=rootNode;
             }
             else if(tempNode.isKeyWordEnd())
             {
                 result.append(REPLACEMENT);
                 position++;
                 begin=position;
                 tempNode=rootNode;
             }
             else {
                 position++;
             }

         }
      result.append(text.substring(begin));
      return result.toString();
    }

    @Override
    public void afterPropertiesSet()throws Exception
    {
        rootNode=new TrieNode();
        try{
            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader  reader=new InputStreamReader(is);
            BufferedReader bufferedReader=new BufferedReader(reader);
            String word;
            while((word=bufferedReader.readLine())!=null)
            {      word=word.trim();
                addWord(word);
            }
            reader.close();

        }
        catch(Exception e)
        {
            logger.error("读取敏感词失败"+e.getMessage());
        }

    }



  /*public static void main(String[] argv)throws Exception {
        SensitiveService s = new SensitiveService();
        s.afterPropertiesSet();
        System.out.println(s.filter("你我  色 情"));
        System.out.print(s.filter("你wo&色**情"));
    }
    */



}
