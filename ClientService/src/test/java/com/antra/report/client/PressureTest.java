package com.antra.report.client;

import com.antra.report.client.pojo.reponse.GeneralResponse;
import com.antra.report.client.pojo.request.ReportRequest;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PressureTest {

    @Rule
    public ContiPerfRule contiPerfRule = new ContiPerfRule();

    private static final RestTemplate rs = new RestTemplate();
    private static final String REQUEST = "{\"description\":\"Student Math Course Report\", \"headers\":[\"Student #\",\"Name\",\"Class\",\"Score\"], \"data\":[[\"s-008\",\"Sarah\",\"Class-A\",\"B\"], [\"s-009\",\"Thomas\",\"Class-A\",\"B-\"], [\"s-010\",\"Joseph\",\"Class-B\",\"A-\"], [\"s-011\",\"Charles\",\"Class-C\",\"A123123\"]], \"submitter\":\"Mrs. York\" }";


    @Test
    @PerfTest(invocations = 9000, threads = 150)
    @Required(max = 1200, average = 250, totalTime = 30000)
    public void GettingAPITesting() {
        rs.getForEntity("http://localhost:8080/report", null, GeneralResponse.class).getBody();
    }

    @Test
    @PerfTest(invocations = 200, threads = 50)
    @Required(max = 6000, average = 4000, totalTime = 30000)
    public void SyncPostingAPITesting() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(REQUEST, headers);
        GeneralResponse mock = rs.postForObject("http://localhost:8080/report/sync", request, GeneralResponse.class);
        System.out.println(mock);
    }
}
