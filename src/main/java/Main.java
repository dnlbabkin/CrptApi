import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try {
            CrptApi api = new CrptApi(TimeUnit.MINUTES, 5);

            CrptApi.Document document = new CrptApi.Document();
            document.doc_id = "12345";
            document.doc_status = "NEW";
            document.importRequest = true;
            document.owner_inn = "1234567890";
            document.participant_inn = "0987654321";
            document.producer_inn = "1122334455";
            document.production_date = "2024-01-01";
            document.production_type = "TYPE";
            document.reg_date = "2024-01-01";
            document.reg_number = "98765";

            CrptApi.Document.Description description = new CrptApi.Document.Description();
            description.participantInn = "1234567890";
            document.description = description;

            CrptApi.Document.Product product = new CrptApi.Document.Product();
            product.certificate_document = "doc";
            product.certificate_document_date = "2024-01-01";
            product.certificate_document_number = "0001";
            product.owner_inn = "1234567890";
            product.producer_inn = "1122334455";
            product.production_date = "2024-01-01";
            product.tnved_code = "01020304";
            product.uit_code = "UIT123";
            product.uitu_code = "UITU456";

            document.products = new CrptApi.Document.Product[]{product};

            api.createDocument(document, "MEUCIQD7k4GvLJNEeAvAHe5E3OH1w3K1H4TFXtfdRzXl+yyvZAIgRzr9Ez+qMfA/2HoJKqfZjX3AolTozz6w5q3v6PCDgjk=");

            System.out.println("Документ успешно отправлен");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
