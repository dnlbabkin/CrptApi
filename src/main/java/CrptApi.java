import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {
    private static final String API_URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RateLimiter rateLimiter;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.rateLimiter = new RateLimiter(timeUnit, requestLimit);
    }

    public void createDocument(Document document, String signature) throws IOException, InterruptedException {
        rateLimiter.acquire();

        String requestBody = objectMapper.writeValueAsString(document);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Signature", signature)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to create document: " + response.body());
        }
    }
    private static class RateLimiter {
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        private final int maxRequests;
        private final long intervalMillis;
        private final AtomicInteger requestCount = new AtomicInteger(0);

        public RateLimiter(TimeUnit timeUnit, int maxRequests) {
            this.maxRequests = maxRequests;
            this.intervalMillis = timeUnit.toMillis(1);
            scheduler.scheduleAtFixedRate(() -> requestCount.set(0), 0, intervalMillis, TimeUnit.MILLISECONDS);
        }

        public synchronized void acquire() throws InterruptedException {
            while (requestCount.get() >= maxRequests) {
                wait(intervalMillis);
            }
            requestCount.incrementAndGet();
        }
    }

    public static class Document {
        public Description description;
        public String doc_id;
        public String doc_status;
        public String doc_type = "LP_INTRODUCE_GOODS";
        public boolean importRequest;
        public String owner_inn;
        public String participant_inn;
        public String producer_inn;
        public String production_date;
        public String production_type;
        public Product[] products;
        public String reg_date;
        public String reg_number;

        public static class Description {
            public String participantInn;
        }

        public static class Product {
            public String certificate_document;
            public String certificate_document_date;
            public String certificate_document_number;
            public String owner_inn;
            public String producer_inn;
            public String production_date;
            public String tnved_code;
            public String uit_code;
            public String uitu_code;
        }
    }
}
