package com.example.wenda.service;

import com.example.wenda.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private static final String SOLR_URL = "http://127.0.0.1:8984/solr/wenda";
    private HttpSolrClient client= new HttpSolrClient.Builder(SOLR_URL).build();
    private static final String Question_TITLE_FIELD = "question_title";
    private static final String Question_CONTENT_FIELD = "question_content";


    public List<Question> searchQuestion(String keyword, int offset, int count, String hlPre, String hlPos) throws Exception {


        List<Question> questionList = new ArrayList<>();
        SolrQuery solrQuery = new SolrQuery(keyword);
        solrQuery.setRows(count);
        solrQuery.setStart(offset);
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre(hlPre);
        solrQuery.setHighlightSimplePost(hlPos);
        solrQuery.set("hl.fl", Question_TITLE_FIELD + "," + Question_CONTENT_FIELD);
        QueryResponse response = client.query(solrQuery);
        for (Map.Entry<String, Map<String, List<String>>> entry : response.getHighlighting().entrySet()) {
            Question q = new Question();
            q.setId(Integer.parseInt(entry.getKey()));
            if (entry.getValue().containsKey(Question_CONTENT_FIELD)) {
                List<String> contentList = entry.getValue().get(Question_CONTENT_FIELD);
                if (contentList.size() > 0) {
                    q.setContent(contentList.get(0));
                }
            }
            if (entry.getValue().containsKey(Question_TITLE_FIELD)) {
                List<String> titleList = entry.getValue().get(Question_TITLE_FIELD);
                if (titleList.size() > 0) {
                        q.setTitle(titleList.get(0));
                }
            }
            questionList.add(q);

        }
        return questionList;
    }
    public boolean indexQuestion(int qid,String title,String content)throws Exception
    {
        SolrInputDocument doc=new SolrInputDocument();
        doc.setField("id",qid);
        doc.setField(Question_CONTENT_FIELD,content);
        doc.setField(Question_TITLE_FIELD,title);
        UpdateResponse response=client.add(doc);
        client.commit();
       return response!=null&&response.getStatus()==0;

    }

}
