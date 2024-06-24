import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CrptApi {
    private final TimeUnit timeUnit;
    private final int requestLimit;
    private List<Date> requestsList;
    private final HttpClient client;
    private final Gson gson;
    private final Object lock;

    public CrptApi(TimeUnit timeUnit, int requestLimit){
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        this.requestsList = new ArrayList<>();
        this.client = HttpClient.newHttpClient();
        this.gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        this.lock = new Object();
    }

    public String createDocument(Document doc, String sign) {
        Date now = new Date();
        synchronized (lock) {
            removeOldRequests(now);
            if (requestsList.size() >= requestLimit) {
                return "Limit of requests is exceeded. Please try later.";
            }
            requestsList.add(now);
        }

        HttpRequest request;
        try {
            request = buildHttpRequest(doc);
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e){
            return "URI syntax error";
        } catch (Exception e){
            return "Error making request";
        }
        return "Document Posted";
    }

    private HttpRequest buildHttpRequest(Document doc) throws URISyntaxException {
        HttpRequest request;
        request = HttpRequest.newBuilder()
                .uri(new URI("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(doc, Document.class)))
                .build();
        return request;
    }

    private void removeOldRequests(Date now) {
        long timeLimit = now.getTime() - timeUnit.toMillis(1);
        requestsList = requestsList.stream()
                .filter(date -> date.getTime() > timeLimit)
                .collect(Collectors.toList());
    }

    public static class Document{
        private Description description;
        @SerializedName("doc_id")
        private String docId;
        @SerializedName("doc_status")
        private String docStatus;
        @SerializedName("doc_type")
        private String docType;
        private boolean importRequest;
        @SerializedName("owner_inn")
        private String ownerInn;
        @SerializedName("participant_inn")
        private String participantInn;
        @SerializedName("producer_inn")
        private String producerInn;
        @SerializedName("production_date")
        private Date productionDate;
        @SerializedName("production_type")
        private String productionType;
        private List<Product> products;
        @SerializedName("reg_date")
        private Date regDate;
        @SerializedName("reg_number")
        private String regNumber;

        public Document(Description description, String docId, String docStatus, String docType, boolean importRequest, String ownerInn, String participantInn, String producerInn, Date productionDate, String productionType, List<Product> products, Date regDate, String regNumber) {
            this.description = description;
            this.docId = docId;
            this.docStatus = docStatus;
            this.docType = docType;
            this.importRequest = importRequest;
            this.ownerInn = ownerInn;
            this.participantInn = participantInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.productionType = productionType;
            this.products = products;
            this.regDate = regDate;
            this.regNumber = regNumber;
        }

        public Description getDescription() {
            return description;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }

        public String getDocType() {
            return docType;
        }

        public void setDocType(String docType) {
            this.docType = docType;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public void setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public void setProducerInn(String producerInn) {
            this.producerInn = producerInn;
        }

        public Date getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(Date productionDate) {
            this.productionDate = productionDate;
        }

        public String getProductionType() {
            return productionType;
        }

        public void setProductionType(String productionType) {
            this.productionType = productionType;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public Date getRegDate() {
            return regDate;
        }

        public void setRegDate(Date regDate) {
            this.regDate = regDate;
        }

        public String getRegNumber() {
            return regNumber;
        }

        public void setRegNumber(String regNumber) {
            this.regNumber = regNumber;
        }
    }
    public static class Description{
        String participantInn;

        public Description(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }
    }
    public static class Product{
        @SerializedName("certificate_document")
        private String certificateDocument;
        @SerializedName("certificate_document_date")
        private Date certificateDocumentDate;
        @SerializedName("certificate_document_number")
        private String certificateDocumentNumber;
        @SerializedName("owner_inn")
        private String ownerInn;
        @SerializedName("producer_inn")
        private String producerInn;
        @SerializedName("production_date")
        private Date productionDate;
        @SerializedName("tnved_code")
        private String tnvedCode;
        @SerializedName("uit_code")
        private String uitCode;
        @SerializedName("uitu_code")
        private String uituCode;

        public Product(String certificateDocument, Date certificateDocumentDate, String certificateDocumentNumber, String ownerInn, String producerInn, Date productionDate, String tnvedCode, String uitCode, String uituCode) {
            this.certificateDocument = certificateDocument;
            this.certificateDocumentDate = certificateDocumentDate;
            this.certificateDocumentNumber = certificateDocumentNumber;
            this.ownerInn = ownerInn;
            this.producerInn = producerInn;
            this.productionDate = productionDate;
            this.tnvedCode = tnvedCode;
            this.uitCode = uitCode;
            this.uituCode = uituCode;
        }

        public String getCertificateDocument() {
            return certificateDocument;
        }

        public void setCertificateDocument(String certificateDocument) {
            this.certificateDocument = certificateDocument;
        }

        public Date getCertificateDocumentDate() {
            return certificateDocumentDate;
        }

        public void setCertificateDocumentDate(Date certificateDocumentDate) {
            this.certificateDocumentDate = certificateDocumentDate;
        }

        public String getCertificateDocumentNumber() {
            return certificateDocumentNumber;
        }

        public void setCertificateDocumentNumber(String certificateDocumentNumber) {
            this.certificateDocumentNumber = certificateDocumentNumber;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public void setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public void setProducerInn(String producerInn) {
            this.producerInn = producerInn;
        }

        public Date getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(Date productionDate) {
            this.productionDate = productionDate;
        }

        public String getTnvedCode() {
            return tnvedCode;
        }

        public void setTnvedCode(String tnvedCode) {
            this.tnvedCode = tnvedCode;
        }

        public String getUitCode() {
            return uitCode;
        }

        public void setUitCode(String uitCode) {
            this.uitCode = uitCode;
        }

        public String getUituCode() {
            return uituCode;
        }

        public void setUituCode(String uituCode) {
            this.uituCode = uituCode;
        }
    }
}
